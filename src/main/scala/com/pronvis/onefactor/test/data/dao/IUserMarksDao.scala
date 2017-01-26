package com.pronvis.onefactor.test.data.dao

import com.pronvis.onefactor.test.data.{EarthPoint, UserMark}

trait IUserMarksDao {
  def get(userId: Long): Option[UserMark]
  def add(userId: Long, location: EarthPoint): Option[UserMark]
  def remove(userId: Long): Option[UserMark]
  def update(userId: Long, location: EarthPoint): Option[UserMark]
  def all(): List[UserMark]
}
