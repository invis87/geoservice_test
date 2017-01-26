package com.pronvis.onefactor.test.api

import com.pronvis.onefactor.test.data.EarthPoint

object Requests {

  case class AddUserMark(userId: Long, markLocation: EarthPoint)
  case class UpdateUserMark(userId: Long, markLocation: Option[EarthPoint])
}
