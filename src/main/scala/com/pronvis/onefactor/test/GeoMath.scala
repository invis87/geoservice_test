package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.data.EarthPoint
import scala.math._

object GeoMath {
  val EarthRadiusInMeters = 6372795

  def degreesToRadians(degrees: Float): Double = {
    degrees * Pi / 180
  }

  def metersBetweenPoints(point1: EarthPoint, point2: EarthPoint): Double = {
    val p1Lat_rad = degreesToRadians(point1.latitude)
    val p1Lon_rad = degreesToRadians(point1.longitude)
    val p2Lat_rad = degreesToRadians(point2.latitude)
    val p2Lon_rad = degreesToRadians(point2.longitude)

    val p1Lat_sin = sin(p1Lat_rad)
    val p1Lat_cos = cos(p1Lat_rad)
    val p2Lat_sin = sin(p2Lat_rad)
    val p2Lat_cos = cos(p2Lat_rad)
    val delta = p2Lon_rad - p1Lon_rad
    val delta_cos = cos(delta)
    val delta_sin = sin(delta)

    val y = sqrt(
      pow(p2Lat_cos * delta_sin, 2) +
      pow(p1Lat_cos * p2Lat_sin - p1Lat_sin * p2Lat_cos * delta_cos, 2)
    )
    val x = p1Lat_sin * p2Lat_sin + p1Lat_cos * p2Lat_cos * delta_cos

    atan2(y, x) * EarthRadiusInMeters
  }
}
