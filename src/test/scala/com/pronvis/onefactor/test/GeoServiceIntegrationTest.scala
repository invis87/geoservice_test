package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses.{StringResponse, StringResponses, TileStatsResponse}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao, InMemoryGeoTilesDao, InMemoryUserMarksDao}
import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.testkit.Specs2RouteTest

import scala.concurrent.ExecutionContextExecutor

//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

class GeoServiceIntegrationTest extends Specification with Specs2RouteTest with GeoService {

  override def executionContext: ExecutionContextExecutor = system.dispatcher
  override def actorRefFactory = system

  override val userMarksDao: IUserMarksDao = InMemoryUserMarksDao(collection.concurrent.TrieMap[Long, UserMark]())
  override val geoTilesDao: IGeoTilesDao = InMemoryGeoTilesDao(Map[TileCoord, GeoTile]())

  "GeoService" should {
    "count new UserMarks when counting Tile stats" in {
      userMarksDao.add(1l, EarthPoint(10f, 20f))
      userMarksDao.add(2l, EarthPoint(10.5f, 20f))
      userMarksDao.add(3l, EarthPoint(10f, 20.999f))

      Post("/addUserMark", AddUserMark(3l, EarthPoint(10.9999f, 20.55f))) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.userAlreadyHaveMark(3l)
      }

      Get("/tileStat?lat=10.32&lon=20.23") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[TileStatsResponse].marksInTile === 3
      }

      Post("/addUserMark", AddUserMark(4l, EarthPoint(10.9999f, 20.55f))) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markAdded(4l)
      }

      Get("/tileStat?lat=10.69&lon=20.73") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[TileStatsResponse].marksInTile === 4
      }

      Post("/updateUserMark", UpdateUserMark(1l, Some(EarthPoint(20.9999f, 20.55f)))) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markUpdated(1l)
      }

      Get("/tileStat?lat=10.11&lon=20.223") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[TileStatsResponse].marksInTile === 3
      }
    }
  }
}
