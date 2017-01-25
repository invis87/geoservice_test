package com.pronvis.onefactor.test.data

case class GeoTile(
  coord: TileCoord,
  distanceError: Float
)

case class TileCoord(
  latitude: Int,
  longitude: Int
)