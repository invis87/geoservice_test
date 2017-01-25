package com.pronvis.onefactor.test

import java.io.{BufferedWriter, File, FileWriter}

import com.pronvis.onefactor.test.data.generator.DataGenerator
import com.pronvis.onefactor.test.serialization.CsvProtocol
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

object GenerateData extends LazyLogging {

  def main(args: Array[String]) {
    val config = ConfigFactory.load().getConfig("data-generator")

    val userMarksCount = config.getInt("users-count")
    val userMarksFile = new File(config.getString("user-marks-path"))
    createDirIfNotExists(userMarksFile.getParentFile)
    logger.info(s"Start generating $userMarksCount UserMarks to file `${userMarksFile.getAbsolutePath}`")
    writeUserMarks(userMarksCount, userMarksFile)
    logger.info("UserMarks generated successfully!")

    val geoTilesCount = config.getInt("geo-tiles-count")
    val maxTileError = config.getInt("max-tile-error")
    val geoTilesFiles = new File(config.getString("geo-tiles-path"))
    createDirIfNotExists(geoTilesFiles.getParentFile)
    logger.info(s"Start generating $geoTilesCount GeoTiles to file `${geoTilesFiles.getAbsolutePath}`")
    writeGeoTiles(geoTilesCount, maxTileError, geoTilesFiles)
    logger.info("GeoTiles generated successfully!")
  }

  private def writeUserMarks(usersCount: Int, file: File) = {
    val generator = new DataGenerator()
    val userMarks = generator.generateUserMarks(usersCount)
    val bw = new BufferedWriter(new FileWriter(file))
    userMarks.foreach(uMark => bw.write(CsvProtocol.userMarkToString(uMark)))
    bw.close()
  }

  private def writeGeoTiles(geoTilesCount: Int, maxTileError: Int, file: File) = {
    val generator = new DataGenerator()
    val geoTiles = generator.generateGeoTiles(geoTilesCount, maxTileError)
    val bw = new BufferedWriter(new FileWriter(file))
    geoTiles.foreach(gTile => bw.write(CsvProtocol.geoTileToString(gTile)))
    bw.close()
  }

  private def createDirIfNotExists(dir: File) = {
    if(!dir.exists()) {
      //yes, we can fail here, cause don't use return type
      dir.mkdirs()
    }
  }

}
