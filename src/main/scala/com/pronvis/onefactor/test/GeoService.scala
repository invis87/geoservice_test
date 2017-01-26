package com.pronvis.onefactor.test

import akka.util.Timeout
import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses.{StringResponse, TileStatsResponse}
import spray.routing.HttpService
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

trait GeoService extends HttpService {

  implicit val timeout = Timeout(5.seconds) //todo: move to config

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def checkUserLocation(userId: String, latitude: String, longitude: String): Future[Response[StringResponse]]
  def addUserMark(newMark: AddUserMark): Future[Response[StringResponse]]
  def updateUserMark(updateMark: UpdateUserMark): Future[Response[StringResponse]]
  def tileStat(latitude: String, longitude: String): Future[Response[TileStatsResponse]]

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
      (post & path("UpdateUserMark")) {
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
