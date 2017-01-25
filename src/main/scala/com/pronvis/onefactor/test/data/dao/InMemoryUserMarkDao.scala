package com.pronvis.onefactor.test.data.dao

import java.io.File

import com.pronvis.onefactor.test.data.UserMark
import com.pronvis.onefactor.test.serialization.CsvProtocol
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object InMemoryUserMarkDao extends LazyLogging {
  def apply(file: File): InMemoryUserMarkDao = {
    logger.debug(s"Start parsing file `${file.getAbsolutePath}` to create InMemoryUserMarkDao")
    val uMarks = Source.fromFile(file).getLines.map(line => {
      val uMark = CsvProtocol.stringToUserMark(line)
      uMark.userId -> uMark
    }).toMap

    logger.debug("Parsing complete! Creating InMemoryUserMarkDao")
    new InMemoryUserMarkDao(uMarks)
  }
}

class InMemoryUserMarkDao(userMarks: Map[Long, UserMark]) extends IUserMarksDao {
  override def getUserMark(userId: Long): Option[UserMark] = {
    userMarks.get(userId)
  }
}

