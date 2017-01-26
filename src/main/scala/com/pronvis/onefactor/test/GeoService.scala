package com.pronvis.onefactor.test

import akka.util.Timeout
import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, StringResponse, TileStatsResponse}
import com.pronvis.onefactor.test.data.{EarthPoint, TileCoord, UserMark}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao}
import com.typesafe.scalalogging.LazyLogging
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

trait GeoService extends HttpService with LazyLogging {

  implicit val timeout = Timeout(5.seconds) //todo: move to config

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def userMarksDao: IUserMarksDao
  def geoTilesDao: IGeoTilesDao


  def checkUserLocation(uId: String, latitude: String, longitude: String): Future[Response[StringResponse]] = {
    Future {
      logger.debug("inside checkUserLocation method")
      val userId = uId.toLong
      val uLocation = EarthPoint(latitude.toFloat, longitude.toFloat)
      logger.debug("arguments parsed successfully")

      val isErrorBigger = for {
        uMark <- userMarksDao.get(userId)
        distToMark = GeoMath.metersBetweenPoints(uMark.location, uLocation)
        markTileCoord = TileCoord(uMark.location)
        markTile <- geoTilesDao.getTile(markTileCoord)
      } yield distToMark < markTile.distanceError

      isErrorBigger match {
        case None                => FAIL(ErrorResponse(400, "User do not have a mark OR there is no information about tile distance error in marker tile."))
        case Some(isErrorBigger) => OK(StringResponse(messageByMarkNearness(isErrorBigger)))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "wrong arguments")) //todo
    }
  }

  private def messageByMarkNearness(isErrorBigger: Boolean): String = isErrorBigger match {
    case true  => "close to mark"
    case false => "far from mark"
  }

  def addUserMark(newMark: AddUserMark): Future[Response[StringResponse]] = {
    Future {
      userMarksDao.get(newMark.userId) match {
        case Some(_) => OK(StringResponse(s"${ newMark.userId } already have a mark."))
        case None    =>
          userMarksDao.add(newMark.userId, newMark.markLocation)
          OK(StringResponse(s"Successfully add mark for user ${ newMark.userId }"))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  def updateUserMark(updateMark: UpdateUserMark): Future[Response[StringResponse]] = {
    Future {
      updateMark.markLocation match {
        case None             =>
          val removeResult = userMarksDao.remove(updateMark.userId)
          val msg = messageAfterRemoving(updateMark.userId, removeResult)
          OK(StringResponse(msg))
        case Some(earthPoint) =>
          userMarksDao.update(updateMark.userId, earthPoint)
          OK(StringResponse(s"UserMark (userId: ${ updateMark.userId }) successfully updated"))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  private def messageAfterRemoving(userId: Long, removeResult: Option[UserMark]): String = removeResult match {
    case None    => s"Nothing was removed, user (id: $userId) don't have mark"
    case Some(_) => s"Mark was removed for user $userId"
  }

  def tileStat(latitude: String, longitude: String): Future[Response[TileStatsResponse]] = {
    Future {
      logger.debug("inside tileStat method")
      val point = TileCoord(EarthPoint(latitude.toFloat, longitude.toFloat))
      logger.debug("arguments parsed successfully")
      val marksInTile = userMarksDao.all().par.count(um => TileCoord(um.location) == point)
      OK(TileStatsResponse(marksInTile))
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  val route = {
    (get & path("checkUserLocation")) {
      pathEnd {
        parameter('uId, 'lat, 'lon) { (userId, lat, lon) =>
          complete { checkUserLocation(userId, lat, lon) }
        }
      }
    } ~
      (post & path("addUserMark")) {
        pathEnd {
          entity(as[AddUserMark]) { newMark =>
            complete { addUserMark(newMark) }
          }
        }
      } ~
      (post & path("updateUserMark")) {
        pathEnd {
          entity(as[UpdateUserMark]) { updateMark =>
            complete { updateUserMark(updateMark) }
          }
        }
      } ~
      (get & path("tileStat")) {
        pathEnd {
          parameter('lat, 'lon) { (lat, lon) =>
            complete { tileStat(lat, lon) }
          }
        }
      }
  }
}
