package com.pronvis.onefactor.test.data.dao

import com.pronvis.onefactor.test.data.UserMark

trait IUserMarksDao {
  def getUserMark(userId: Long): Option[UserMark]
}
