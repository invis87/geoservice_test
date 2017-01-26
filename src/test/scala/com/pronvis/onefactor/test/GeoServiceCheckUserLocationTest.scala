package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses
import com.pronvis.onefactor.test.api.Responses.{StringResponse, StringResponses}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao, InMemoryGeoTilesDao, InMemoryUserMarksDao}
import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.testkit.Specs2RouteTest

import scala.concurrent.ExecutionContextExecutor

//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

class GeoServiceCheckUserLocationTest extends Specification with Specs2RouteTest with GeoService {
  override def executionContext: ExecutionContextExecutor = system.dispatcher
  override def actorRefFactory = system

  override val userMarksDao: IUserMarksDao = InMemoryUserMarksDao(collection.concurrent.TrieMap[Long, UserMark]())
  override val geoTilesDao: IGeoTilesDao = InMemoryGeoTilesDao(Map[TileCoord, GeoTile](
    TileCoord(10, 10) -> GeoTile(TileCoord(10, 10), Float.MaxValue),
    TileCoord(22, 10) -> GeoTile(TileCoord(22, 10), 0)
  ))


  "checkUserLocation " should {

    "use TileError in Tile where Marker is placed" in {
      Post("/addUserMark", AddUserMark(1l, EarthPoint(10.01f, 10.01f))) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markAdded(1l)
      }

      Get("/checkUserLocation?uId=1&lat=22.22&lon=10.12") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === Responses.closeToMarkResponse
      }

      Post("/updateUserMark", UpdateUserMark(1l, None)) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markRemoved(1l)
      }

      Post("/addUserMark", AddUserMark(1l, EarthPoint(22.07f, 10.51f))) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markAdded(1l)
      }

      Get("/checkUserLocation?uId=1&lat=10.22&lon=10.12") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === Responses.farFromMarkResponse
      }
    }
  }
}
