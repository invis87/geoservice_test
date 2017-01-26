package com.pronvis.onefactor.test.api

object Responses {

  case class ErrorResponse(errorCode: Int, description: String)
  case class StringResponse(message: String)
  case class TileStatsResponse(marksInTile: Long)

}
