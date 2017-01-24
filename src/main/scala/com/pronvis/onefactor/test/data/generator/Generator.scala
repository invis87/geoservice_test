package com.pronvis.onefactor.test.data.generator

import com.pronvis.onefactor.test.data.{EarthPoint, UserMark}

import scala.util.Random

class Generator {
  val random = new Random()

  def generateUserMarks(count: Int): Seq[UserMark] = {
    Range(0, count).map(id => UserMark(id, randomEarthPoint()))
  }


  private def randomEarthPoint(): EarthPoint = {
    val latitude = (random.nextDouble() * 180).toFloat - 90
    val longitude = (random.nextDouble() * 360).toFloat - 180

    EarthPoint(latitude, longitude)
  }
}
