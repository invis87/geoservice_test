package com.pronvis.onefactor.test.serialization

import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, LocationResponse, TestConnection}
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)
  implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
  implicit val locationResposeFormat = jsonFormat1(LocationResponse)
}