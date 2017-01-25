package com.pronvis.onefactor.test

import java.io.{BufferedWriter, File, FileWriter}

import com.pronvis.onefactor.test.data.UserMark
import com.pronvis.onefactor.test.data.generator.DataGenerator
import com.typesafe.config.ConfigFactory

object GenerateData {

  def main(args: Array[String]) {
    val config = ConfigFactory.load().getConfig("data-generator")

    val userMarksCount = config.getInt("users-count")
    val userMarksFile = new File(config.getString("user-marks-path "))

    writeUserMarks(userMarksCount, userMarksFile)
  }

  private def writeUserMarks(usersCount: Int, file: File) = {
    val generator = new DataGenerator()
    val userMarks = generator.generateUserMarks(usersCount)
    val bw = new BufferedWriter(new FileWriter(file))
    userMarks.foreach(uMark => bw.write(userMarkToString(uMark)))
    bw.close()
  }

  private def userMarkToString(userMark: UserMark): String = {
    s"${userMark.userId},${userMark.coord.latitude},${userMark.coord.longitude}\n"
  }

}
