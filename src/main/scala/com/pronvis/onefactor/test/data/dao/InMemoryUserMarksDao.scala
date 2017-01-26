package com.pronvis.onefactor.test.data.dao

import java.io.File

import com.pronvis.onefactor.test.data.{EarthPoint, UserMark}
import com.pronvis.onefactor.test.serialization.CsvProtocol
import com.typesafe.scalalogging.LazyLogging

import scala.collection.concurrent.{Map, TrieMap}
import scala.io.Source

object InMemoryUserMarksDao extends LazyLogging {
  def apply(file: File): InMemoryUserMarksDao = {
    val uMarks = Source.fromFile(file).getLines.map(line => {
      val uMark = CsvProtocol.stringToUserMark(line)
      uMark.userId -> uMark
    })

    logger.debug("Parsing file `${file.getAbsolutePath}` to create InMemoryUserMarksDao...")
    apply(TrieMap(uMarks.toSeq:_*))
  }

  def apply(map: Map[Long, UserMark]): InMemoryUserMarksDao = {
    new InMemoryUserMarksDao(map)
  }
}

class InMemoryUserMarksDao(userMarks: Map[Long, UserMark]) extends IUserMarksDao {
  override def get(userId: Long): Option[UserMark] = {
    userMarks.get(userId)
  }

  override def add(userId: Long, location: EarthPoint): Option[UserMark] = {
    userMarks.put(userId, UserMark(userId, location))
  }

  override def remove(userId: Long): Option[UserMark] = {
    userMarks.remove(userId)
  }

  override def update(userId: Long, location: EarthPoint): Option[UserMark] = {
    userMarks.put(userId, UserMark(userId, location))
  }
  override def all(): List[UserMark] = {
    userMarks.values.toList
  }
}

