package com.pronvis.onefactor.test.data

case class GeoTile(
  coord: TileCoord,
  distanceError: Float
)

case class TileCoord(
  latitude: Int,
  longitude: Int
)

object TileCoord {
  def apply(point: EarthPoint): TileCoord = {
    TileCoord(point.latitude.toInt, point.longitude.toInt)
  }
}