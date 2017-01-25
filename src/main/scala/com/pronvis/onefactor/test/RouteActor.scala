package com.pronvis.onefactor.test

import akka.actor.{Actor, ActorRefFactory, Props}
import akka.util.Timeout
import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, LocationResponse, TestConnection}
import com.pronvis.onefactor.test.data.{EarthPoint, TileCoord}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao}
import com.typesafe.scalalogging.LazyLogging
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

object RouteActor {
  def props(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao): Props = Props(new RouteActor(userMarksDao, geoTilesDao))
}

class RouteActor(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao) extends Actor with GeoserviceService with LazyLogging {

  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory: ActorRefFactory = context

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    logger.debug("in testConnection")
    TestConnection("all fine!")
  }

  override def checkUserLocation(uId: String, latitude: String, longitude: String): Future[Response[LocationResponse]] = {
    Future {
      logger.debug("inside checkUserLocation method")
      val userId = uId.toLong
      val uLocation = EarthPoint(latitude.toFloat, longitude.toFloat)
      logger.debug("arguments parsed successfully")

      val isErrorBigger = for {
        uMark <- userMarksDao.getUserMark(userId)
      _ = logger.debug(s"userMarksDao return $uMark for $userId")
      _ = logger.debug(s"uMark.location.latitude=${uMark.location.latitude} uMark.location.longitude=${uMark.location.longitude}")
      _ = logger.debug(s"...latitude.toInt=${uMark.location.latitude.toInt} ...longitude.toInt=${uMark.location.longitude.toInt}")
        distToMark = GeoMath.metersBetweenPoints(uMark.location, uLocation)
        markTileCoord = TileCoord(uMark.location.latitude.toInt, uMark.location.longitude.toInt)
      _ = logger.debug(s"distToMark=$distToMark; markTileCoord=$markTileCoord")
        markTile <- geoTilesDao.getTile(markTileCoord)
      _ = logger.debug(s"geoTilesDao return $markTile for $markTileCoord. DistanceError = ${markTile.distanceError}")
      } yield distToMark < markTile.distanceError

      isErrorBigger match {
        case None                => FAIL(ErrorResponse(400, "User do not have a mark OR there is no information about tile distance error in marker tile."))
        case Some(isErrorBigger) => OK(LocationResponse(messageByMarkNearness(isErrorBigger)))
      }
    }.recover {
      case e: Exception => FAIL(ErrorResponse(400, "wrong arguments")) //todo
    }
  }

  private def messageByMarkNearness(isErrorBigger: Boolean): String = isErrorBigger match {
    case true  => "close to mark"
    case false => "far from mark"
  }
}

trait GeoserviceService extends HttpService {

  implicit val timeout = Timeout(5.seconds) //todo: move to config

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def testConnection: TestConnection

  def checkUserLocation(userId: String, latitude: String, longitude: String): Future[Response[LocationResponse]]

  val route = {
    (get & path("testConnection")) {
      complete {
        testConnection
      }
    } ~
      (get & path("checkUserLocation")) {
        pathEnd {
          parameter('uId, 'lat, 'lon) { (userId, lat, lon) =>
            complete { checkUserLocation(userId, lat, lon) }
          }
        }
      }
  }
}