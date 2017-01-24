package com.pronvis.onefactor.test

import java.io.{BufferedWriter, File, FileWriter}

import com.pronvis.onefactor.test.data.UserMark
import com.pronvis.onefactor.test.data.generator.Generator

object GenerateData {

  def main(args: Array[String]) {
    val userMarksCount = 10 * 1000 * 1000

    writeUserMarks(userMarksCount, new File("<path to file>"))

  }

  private def writeUserMarks(count: Int, file: File) = {
    val generator = new Generator()
    val userMarks = generator.generateUserMarks(count)
    val bw = new BufferedWriter(new FileWriter(file))
    userMarks.foreach(uMark => bw.write(userMarkToString(uMark)))
    bw.close()
  }

  private def userMarkToString(userMark: UserMark): String = {
    s"${userMark.userId},${userMark.coord.latitude},${userMark.coord.longitude}\n"
  }

}
