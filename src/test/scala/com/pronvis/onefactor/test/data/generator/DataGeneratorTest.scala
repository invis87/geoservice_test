package com.pronvis.onefactor.test.data.generator

import org.specs2.mutable.Specification

class DataGeneratorTest extends Specification{

  "DataGenerator" should {

    "fill entire Earth with GeoTiles" in {
      val generator = new DataGenerator()
      val geoTiles = generator.generateGeoTiles(180 * 360, 1)
      geoTiles.size === 180 * 360
    }
  }
}
