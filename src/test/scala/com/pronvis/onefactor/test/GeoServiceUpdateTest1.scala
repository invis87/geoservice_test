package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.api.Requests.UpdateUserMark
import com.pronvis.onefactor.test.api.Responses.StringResponse
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

class GeoServiceUpdateTest1 extends Specification with Specs2RouteTest with GeoService {

  override def executionContext: ExecutionContextExecutor = system.dispatcher
  override def actorRefFactory = system

  override val userMarksDao: IUserMarksDao = mock(classOf[IUserMarksDao])
  override val geoTilesDao: IGeoTilesDao = mock(classOf[IGeoTilesDao])


  "updateUserMark page (1)" should {

    "remove UserMark and return correct response if there was no such User" in {
      val userId = 5l
      doReturn(None).when(userMarksDao).remove(userId)
      Post(s"/updateUserMark", UpdateUserMark(userId, None)) ~> route ~> check {
        response.status === StatusCodes.OK
        responseAs[StringResponse].message === s"Nothing was removed, user (id: $userId) don't have mark"
      }
    }

  }
}
