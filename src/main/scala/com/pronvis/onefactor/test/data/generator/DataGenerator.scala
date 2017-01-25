package com.pronvis.onefactor.test.data.generator

import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}

import scala.util.Random

class DataGenerator() {
  val random = new Random()

  def generateUserMarks(usersCount: Int): Seq[UserMark] = {
    Range(0, usersCount).map(id => UserMark(id, randomEarthPoint()))
  }

  def generateGeoTiles(tilesCount: Int, maxTileError: Int): Seq[GeoTile] = {
    if (tilesCount > 180 * 360) {
      throw new IllegalArgumentException(s"Impossible to generate $tilesCount GeoTiles.")
    }

    def tileError(): Float = (random.nextDouble() * maxTileError).toFloat

    val tiles = for {
      lat <- -90 until 90
      lon <- -180 until 180
    } yield (lat, lon)

    val shuffledTiles = Random.shuffle(tiles)
    shuffledTiles.take(tilesCount).map { case (lat, lon) =>
      GeoTile(TileCoord(lat, lon), tileError())
    }
  }

  private def randomEarthPoint(): EarthPoint = {
    val latitude = (random.nextDouble() * 180 - 90).toFloat
    val longitude = (random.nextDouble() * 360 - 180).toFloat

    EarthPoint(latitude, longitude)
  }
}
