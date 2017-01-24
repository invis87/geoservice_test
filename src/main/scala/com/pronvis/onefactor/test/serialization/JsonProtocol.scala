package com.pronvis.onefactor.test.serialization

import com.pronvis.onefactor.test.api.Responses.TestConnection
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)
}