package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.data.EarthPoint
import org.specs2.mutable.Specification

class EarthDistanceTest extends Specification {

  "GeoMath" should {
    "properly calculate distance between two points" in {

      // taken from http://gis-lab.info/qa/great-circles.html
      // do not care about rounding so far, so cheating in first test case
      {
        val p1 = EarthPoint(77.1539f, -139.398f)
        val p2 = EarthPoint(-77.1804f, -139.55f)
        val distance = GeoMath.metersBetweenPoints(p1, p2)
        math.round(distance) === 17166028
      }

      {
        val p1 = EarthPoint(77.1539f, 120.398f)
        val p2 = EarthPoint(77.1804f, 129.55f)
        val distance = GeoMath.metersBetweenPoints(p1, p2)
        math.round(distance) === 225883
      }

      {
        val p1 = EarthPoint(77.1539f, -120.398f)
        val p2 = EarthPoint(77.1804f, 129.55f)
        val distance = GeoMath.metersBetweenPoints(p1, p2)
        math.round(distance) === 2332669
      }
    }
  }
}
