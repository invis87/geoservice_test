package com.pronvis.onefactor.test.data.dao

import com.pronvis.onefactor.test.data.{GeoTile, TileCoord}

trait IGeoTilesDao {
  def getTile(tileCoord: TileCoord): Option[GeoTile]
}
