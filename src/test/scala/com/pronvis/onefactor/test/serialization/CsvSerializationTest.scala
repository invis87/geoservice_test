package com.pronvis.onefactor.test.serialization

import com.pronvis.onefactor.test.data.{EarthPoint, GeoTile, TileCoord, UserMark}
import org.specs2.mutable.Specification

class CsvSerializationTest extends Specification {

  "CsvProtocol" should {
    "properly serialize and deserialize UserMark" in {
      serializeAndCompareUserMark(UserMark(32423l, EarthPoint(45.453f, 175.344f)))
      serializeAndCompareUserMark(UserMark(3l, EarthPoint(45f, 175f)))
      serializeAndCompareUserMark(UserMark(21l, EarthPoint(-45.34f, -5.00f)))
      serializeAndCompareUserMark(UserMark(456l, EarthPoint(-45f, 155.00f)))

    }

    "properly serialize and deserialize GeoTile" in {
      serializeAndCompareGeoTile(GeoTile(TileCoord(33, 140), 583.4678f))
      serializeAndCompareGeoTile(GeoTile(TileCoord(33, -134), 583f))
      serializeAndCompareGeoTile(GeoTile(TileCoord(-122, 140), 583f))
      serializeAndCompareGeoTile(GeoTile(TileCoord(-122, -22), 583.00f))
      serializeAndCompareGeoTile(GeoTile(TileCoord(0, 0), 0f))
    }
  }

  private def serializeAndCompareUserMark(userMark: UserMark) = {
    val serializedUserMark = CsvProtocol.userMarkToString(userMark)

    val deserializedUserMark = CsvProtocol.stringToUserMark(serializedUserMark)
    userMark.userId === deserializedUserMark.userId
    userMark.location.latitude === deserializedUserMark.location.latitude
    userMark.location.longitude === deserializedUserMark.location.longitude
  }

  private def serializeAndCompareGeoTile(geoTile: GeoTile) = {
    val serializedGeoTile = CsvProtocol.geoTileToString(geoTile)

    val deserializedGeoTile = CsvProtocol.stringToGeoTile(serializedGeoTile)
    geoTile.coord.latitude === deserializedGeoTile.coord.latitude
    geoTile.coord.longitude === deserializedGeoTile.coord.longitude
    geoTile.distanceError === deserializedGeoTile.distanceError
  }
}
