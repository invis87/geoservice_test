package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.api.Requests.UpdateUserMark
import com.pronvis.onefactor.test.api.Responses.{StringResponse, StringResponses}
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

class GeoServiceUpdateTest extends Specification with Specs2RouteTest with GeoService {

  override def executionContext: ExecutionContextExecutor = system.dispatcher
  override def actorRefFactory = system

  override val userMarksDao: IUserMarksDao = mock(classOf[IUserMarksDao])
  override val geoTilesDao: IGeoTilesDao = mock(classOf[IGeoTilesDao])


  "updateUserMark page (0)" should {

    "remove UserMark if UpdateUserMark.markLocation is None" in {
      val userId = 5l
      val earthPoint = EarthPoint(44.4f, 12.535f)
      doReturn(Some(UserMark(userId, earthPoint))).when(userMarksDao).remove(userId)
      Post(s"/updateUserMark", UpdateUserMark(userId, None)) ~> route ~> check {
        verify(userMarksDao).remove(userId)
        response.status === StatusCodes.OK
        responseAs[StringResponse] === StringResponses.markRemoved(userId)
      }
    }

  }
}
