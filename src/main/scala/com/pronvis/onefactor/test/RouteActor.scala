package com.pronvis.onefactor.test

import akka.actor.{Actor, ActorRefFactory, Props}
import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, StringResponse, TileStatsResponse}
import com.pronvis.onefactor.test.data.{EarthPoint, TileCoord, UserMark}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContextExecutor, Future}

object RouteActor {
  def props(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao): Props = Props(new RouteActor(userMarksDao, geoTilesDao))
}

class RouteActor(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao) extends Actor with GeoService with LazyLogging {

  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory: ActorRefFactory = context

  def receive = runRoute(route)

  override def checkUserLocation(uId: String, latitude: String, longitude: String): Future[Response[StringResponse]] = {
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

  override def addUserMark(newMark: AddUserMark): Future[Response[StringResponse]] = {
    Future {
      userMarksDao.get(newMark.userId) match {
        case Some(_) => OK(StringResponse(s"${newMark.userId} already have a mark."))
        case None =>
          userMarksDao.add(newMark.userId, newMark.markLocation)
          OK(StringResponse(s"Successfully add mark for user ${newMark.userId}"))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  override def updateUserMark(updateMark: UpdateUserMark): Future[Response[StringResponse]] = {
    Future {
      updateMark.markLocation match {
        case None =>
          val removeResult = userMarksDao.remove(updateMark.userId)
          val msg = messageAfterRemoving(updateMark.userId, removeResult)
          OK(StringResponse(msg))
        case Some(earthPoint) =>
          userMarksDao.update(updateMark.userId, earthPoint)
          OK(StringResponse(s"UserMark (userId: ${updateMark.userId}) successfully updated"))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "something went wrong")) //todo
    }
  }

  private def messageAfterRemoving(userId: Long, removeResult: Option[UserMark]): String = removeResult match {
    case None => s"Nothing was removed, user ($userId) don't have mark"
    case Some(_) => s"Mark was removed for user $userId"
  }

  override def tileStat(latitude: String, longitude: String): Future[Response[TileStatsResponse]] = {
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
}