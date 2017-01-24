package com.pronvis.onefactor.test.data

case class GeoTile(
  coord: TileCoord,
  distanceError: Int
)

//why tile coordinates is integer?
case class TileCoord(
  x: Int,
  y: Int
)