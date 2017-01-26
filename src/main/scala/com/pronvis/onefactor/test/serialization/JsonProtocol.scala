package com.pronvis.onefactor.test.serialization

import com.pronvis.onefactor.test.api.Requests.{AddUserMark, UpdateUserMark}
import com.pronvis.onefactor.test.api.Responses.{ErrorResponse, StringResponse, TileStatsResponse}
import com.pronvis.onefactor.test.data.EarthPoint
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
  implicit val locationResposeFormat = jsonFormat1(StringResponse)

  implicit val earthPointFormat = jsonFormat2(EarthPoint)
  implicit val addUserMarkFormat = jsonFormat2(AddUserMark)
  implicit val updateUserMarkFormat = jsonFormat2(UpdateUserMark)

  implicit val tileStatsFormat = jsonFormat1(TileStatsResponse)
}