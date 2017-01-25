package com.pronvis.onefactor.test.data.generator

import com.pronvis.onefactor.test.data.{EarthPoint, UserMark}

import scala.util.Random

class DataGenerator {
  val random = new Random()
  private val maxMarksPerUser = 5

  def generateUserMarks(usersCount: Int): Seq[UserMark] = {
    Range(0, usersCount).flatMap(id => randomUserMarks(id))
  }

  private def randomUserMarks(userId: Int): Seq[UserMark] = {
    val marksCount = random.nextInt(maxMarksPerUser) + 1
    Range(0, marksCount).map(_ => UserMark(userId, randomEarthPoint()))
  }

  private def randomEarthPoint(): EarthPoint = {
    val latitude = (random.nextDouble() * 180).toFloat - 90
    val longitude = (random.nextDouble() * 360).toFloat - 180

    EarthPoint(latitude, longitude)
  }
}
