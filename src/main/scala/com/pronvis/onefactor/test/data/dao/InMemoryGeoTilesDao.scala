package com.pronvis.onefactor.test.data.dao

import java.io.File

import com.pronvis.onefactor.test.data.{GeoTile, TileCoord}
import com.pronvis.onefactor.test.serialization.CsvProtocol
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object InMemoryGeoTilesDao extends LazyLogging{
  def apply(file: File): InMemoryGeoTilesDao = {
    logger.debug(s"Start parsing file `${file.getAbsolutePath}` to create InMemoryGeoTilesDao")
    val geoTiles = Source.fromFile(file).getLines.map(line => {
      val geoTile = CsvProtocol.stringToGeoTile(line)
      geoTile.coord -> geoTile
    }).toMap

    logger.debug("Parsing complete! Creating InMemoryGeoTilesDao")
    new InMemoryGeoTilesDao(geoTiles)
  }
}

class InMemoryGeoTilesDao(geoTiles: Map[TileCoord, GeoTile]) extends IGeoTilesDao {
  override def getTile(tileCoord: TileCoord): Option[GeoTile] = {
    geoTiles.get(tileCoord)
  }
}
