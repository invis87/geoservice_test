package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}
import com.pronvis.onefactor.test.serialization.CsvProtocol
import org.specs2.mutable.Specification

class CsvSerializationTest extends Specification {

  "CsvProtocol" should {
    "properly serialize and deserialize UserMark" in {
      val userMark = UserMark(32423l, EarthPoint(45.453f, 175.344f))
      val serializedUserMark = CsvProtocol.userMarkToString(userMark)

      val deserializedUserMark = CsvProtocol.stringToUserMark(serializedUserMark)
      userMark.userId === deserializedUserMark.userId
      userMark.location.latitude === deserializedUserMark.location.latitude
      userMark.location.longitude === deserializedUserMark.location.longitude
    }

    "properly serialize and deserialize GeoTile" in {
      val geoTile = GeoTile(TileCoord(33, 140), 583.4678f)
      val serializedGeoTile = CsvProtocol.geoTileToString(geoTile)

      val deserializedGeoTile = CsvProtocol.stringToGeoTile(serializedGeoTile)
      geoTile.coord.latitude === deserializedGeoTile.coord.latitude
      geoTile.coord.longitude === deserializedGeoTile.coord.longitude
      geoTile.distanceError === deserializedGeoTile.distanceError
    }
  }

}
