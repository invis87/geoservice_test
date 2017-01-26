package com.pronvis.onefactor.test.api

object Responses {

  case class ErrorResponse(errorCode: Int, description: String)
  case class StringResponse(message: String)
  case class TileStatsResponse(marksInTile: Long)

  val closeToMarkResponse = StringResponse("close to mark")
  val farFromMarkResponse = StringResponse("far from mark")

  object StringResponses {
    def markRemoved(userId: Long) = StringResponse(s"Mark was removed for user $userId")
    def markAdded(userId: Long) = StringResponse(s"Successfully add mark for user $userId")
    def userAlreadyHaveMark(userId: Long) = StringResponse(s"$userId already have a mark")
    def markWasNotRemoved(userId: Long) = StringResponse(s"Nothing was removed, user (id: $userId) don't have mark")
    def markUpdated(userId: Long) = StringResponse(s"UserMark (userId: $userId) successfully updated")
  }
}
