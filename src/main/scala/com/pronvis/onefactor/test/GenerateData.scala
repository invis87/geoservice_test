package com.pronvis.onefactor.test

import java.io.{BufferedWriter, File, FileWriter}

import com.pronvis.onefactor.test.data.generator.DataGenerator
import com.pronvis.onefactor.test.serialization.CsvProtocol
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

object GenerateData extends LazyLogging {

  def main(args: Array[String]) {
    val config = Try {
      if (args.isEmpty) {
        configFromConfFile()
      } else {
        configFromArgs(args)
      }
    }

    config match {
      case Failure(e) => logger.error("Fail to create config for DataGenerator", e)
      case Success(config) => generateData(config)
    }
  }

  private def generateData(config: GeneratorConfig): Unit = {
    createDirIfNotExists(config.userMarksFile.getParentFile)
    logger.info(s"Start generating ${ config.usersCount } UserMarks to file `${ config.userMarksFile.getAbsolutePath }`")
    writeUserMarks(config.usersCount, config.userMarksFile)
    logger.info("UserMarks generated successfully!")

    createDirIfNotExists(config.geoTileFile.getParentFile)
    logger.info(s"Start generating ${ config.geoTilesCount } GeoTiles to file `${ config.geoTileFile.getAbsolutePath }`")
    writeGeoTiles(config.geoTilesCount, config.maxTileError, config.geoTileFile)
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
    if (!dir.exists()) {
      //yes, we can fail here, cause don't use return type
      dir.mkdirs()
    }
  }

  private def configFromConfFile(): GeneratorConfig = {
    val cfg = ConfigFactory.load().getConfig("data-generator")
    GeneratorConfig(
      cfg.getInt("users-count"),
      new File(cfg.getString("user-marks-path")),
      cfg.getInt("geo-tiles-count"),
      cfg.getInt("max-tile-error"),
      new File(cfg.getString("geo-tiles-path")))
  }

  private def configFromArgs(args: Array[String]): GeneratorConfig = {
    GeneratorConfig(
      args(0).toInt,
      new File(args(1)),
      args(2).toInt,
      args(3).toInt,
      new File(args(4)))
  }

  case class GeneratorConfig(
    usersCount: Int,
    userMarksFile: File,
    geoTilesCount: Int,
    maxTileError: Int,
    geoTileFile: File)
}
