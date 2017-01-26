package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, TileStatsResponse}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao}
import com.pronvis.onefactor.test.data.{EarthPoint, UserMark}
import org.mockito.Mockito._
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.testkit.Specs2RouteTest

import scala.concurrent.ExecutionContextExecutor

//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

class GeoServiceTitleTest extends Specification with Specs2RouteTest with GeoService {

  override def executionContext: ExecutionContextExecutor = system.dispatcher
  override def actorRefFactory = system

  override val userMarksDao: IUserMarksDao = mock(classOf[IUserMarksDao])
  override val geoTilesDao: IGeoTilesDao = mock(classOf[IGeoTilesDao])


  val testedPoint = EarthPoint(87.52f, 161.98f)
  val userMarks = List(
    //in same Tile (5 at all)
    UserMark(1, EarthPoint(87.33f, 161.336f)),
    UserMark(2, EarthPoint(87.9999f, 161.9999f)),
    UserMark(3, EarthPoint(87.1f, 161.0f)),
    UserMark(4, EarthPoint(87f, 161f)),
    UserMark(5, EarthPoint(87.555555f, 161.12345f)),

    UserMark(6, EarthPoint(81.34f, 66.3f)),
    UserMark(7, EarthPoint(11.34f, 32.3f)),
    UserMark(8, EarthPoint(31.34f, 152.3f)),
    UserMark(9, EarthPoint(51.34f, 132.3f)),
    UserMark(10, EarthPoint(71.34f, 122.3f))
  )

  "tileStat page" should {

    "return UserMarks in GeoTile" in {
      doReturn(userMarks).when(userMarksDao).all()
      val marksInSameTile = userMarks.count(ep =>
        ep.location.latitude.toInt == testedPoint.latitude.toInt &&
          ep.location.longitude.toInt == testedPoint.longitude.toInt)

      Get(s"/tileStat?lat=${ testedPoint.latitude }&lon=${ testedPoint.longitude }") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[TileStatsResponse].marksInTile === marksInSameTile
      }
    }

    "return 400 if latitude is not Float" in {
      Get(s"/tileStat?lat=23rnd&lon=33.23") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[ErrorResponse].errorCode === StatusCodes.BadRequest.intValue
      }
    }

    "return 400 if longitude is not Float" in {
      Get(s"/tileStat?lat=23&lon=33.rnd23") ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[ErrorResponse].errorCode === StatusCodes.BadRequest.intValue
      }
    }
  }

}
