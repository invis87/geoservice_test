package com.pronvis.onefactor.test

import com.pronvis.onefactor.test.data.generator.DataGenerator
import org.specs2.mutable.Specification

class DataGeneratorTest extends Specification{

  "DataGenerator" should {

    "floatTOInt" in {
      1.1f.toInt === 1
      1.45f.toInt === 1
      1.5f.toInt === 1
      1.99f.toInt === 1
      1.9f.toInt === 1
    }

    "fill entire Earth with GeoTiles" in {
      val generator = new DataGenerator()
      val geoTiles = generator.generateGeoTiles(180 * 360, 1)
      geoTiles.size === 180 * 360
    }
  }
}
