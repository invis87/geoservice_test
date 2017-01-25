package com.pronvis.onefactor.test.serialization

import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}

object CsvProtocol {

  def geoTileToString(geoTile: GeoTile): String = {
    s"${geoTile.coord.latitude},${geoTile.coord.longitude},${geoTile.distanceError}\n"
  }

  def stringToGeoTile(str: String): GeoTile = {
    val strParts = str.split(',')
    val tileCoord = TileCoord(strParts(0).toInt, strParts(1).toInt)
    GeoTile(tileCoord, strParts(2).toFloat)
  }

  def userMarkToString(userMark: UserMark): String = {
    s"${userMark.userId},${userMark.location.latitude},${userMark.location.longitude}\n"
  }

  def stringToUserMark(str: String): UserMark = {
    val strParts = str.split(',')
    val earthPoint = EarthPoint(strParts(1).toFloat, strParts(2).toFloat)
    UserMark(strParts(0).toLong, earthPoint)
  }

}
