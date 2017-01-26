package com.pronvis.onefactor.test

import akka.util.Timeout
import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses
import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, StringResponse, StringResponses, TileStatsResponse}
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
      val userId = uId.toLong
      val uLocation = EarthPoint(latitude.toFloat, longitude.toFloat)
      logger.debug(s"[checkUserLocation]: arguments parsed successfully. userId=$userId; userLocation=$uLocation")

      val isErrorBigger = for {
        uMark <- userMarksDao.get(userId)
        distToMark = GeoMath.metersBetweenPoints(uMark.location, uLocation)
        markTileCoord = TileCoord(uMark.location)
        markTile <- geoTilesDao.getTile(markTileCoord)
      } yield distToMark < markTile.distanceError
      logger.debug(s"[checkUserLocation]: distance to mark calculated, result(close or not): $isErrorBigger")

      isErrorBigger match {
        case None                => FAIL(ErrorResponse(400, "User do not have a mark OR there is no information about tile distance error in marker tile."))
        case Some(isErrorBigger) => OK(responseByMarkNearness(isErrorBigger))
      }
    }.recover {
      case e: Exception =>
        logger.error("[checkUserLocation]", e)
        FAIL(ErrorResponse(400, "wrong arguments")) //todo
    }
  }

  private def responseByMarkNearness(isErrorBigger: Boolean): StringResponse = isErrorBigger match {
    case true  => Responses.closeToMarkResponse
    case false => Responses.farFromMarkResponse
  }

  def addUserMark(newMark: AddUserMark): Future[Response[StringResponse]] = {
    Future {
      logger.debug(s"[addUserMark]: to add $newMark")
      userMarksDao.get(newMark.userId) match {
        case Some(_) => OK(StringResponse(s"${ newMark.userId } already have a mark."))
        case None    =>
          userMarksDao.add(newMark.userId, newMark.markLocation)
          OK(StringResponses.markAdded(newMark.userId))
      }
    }.recover {
      case e: Exception =>
        logger.error("[addUserMark]", e)
        FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  def updateUserMark(updateMark: UpdateUserMark): Future[Response[StringResponse]] = {
    Future {
      logger.debug(s"[updateUserMark]: to add $updateMark")
      updateMark.markLocation match {
        case None             =>
          val removeResult = userMarksDao.remove(updateMark.userId)
          val response = messageAfterRemoving(updateMark.userId, removeResult)
          logger.debug(s"[updateUserMark]: UserMark removed (userId: ${updateMark.userId})")
          OK(response)
        case Some(earthPoint) =>
          userMarksDao.update(updateMark.userId, earthPoint)
          logger.debug(s"[updateUserMark]: UserMark updated (userId: ${updateMark.userId}) to $earthPoint")
          OK(StringResponses.markUpdated(updateMark.userId))
      }
    }.recover {
      case e: Exception =>
        logger.error("[updateUserMark]", e)
        FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  private def messageAfterRemoving(userId: Long, removeResult: Option[UserMark]): StringResponse = removeResult match {
    case None    => StringResponses.markWasNotRemoved(userId)
    case Some(_) => StringResponses.markRemoved(userId)
  }

  def tileStat(latitude: String, longitude: String): Future[Response[TileStatsResponse]] = {
    Future {
      logger.debug(s"[tileStat]: latitude=$latitude, longitude=$longitude")
      val point = TileCoord(EarthPoint(latitude.toFloat, longitude.toFloat))
      val marksInTile = userMarksDao.all().par.count(um => TileCoord(um.location) == point)
      logger.debug(s"[tileStat]: found $marksInTile UserMarkers in $point")
      OK(TileStatsResponse(marksInTile))
    }.recover {
      case e: Exception =>
        logger.error("[tileStat]", e)
        FAIL(ErrorResponse(400, "something went wrong")) //todo
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
