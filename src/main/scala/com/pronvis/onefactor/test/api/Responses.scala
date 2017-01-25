package com.pronvis.onefactor.test.api

object Responses {

  case class TestConnection(result: String)

  case class ErrorResponse(errorCode: Int, description: String)
  case class LocationResponse(nearness: String)
}
