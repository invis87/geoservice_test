package com.pronvis.onefactor.test

import java.io.File

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao, InMemoryGeoTilesDao, InMemoryUserMarksDao}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Main extends LazyLogging {

  def main(args: Array[String]) {
    val daos = Try {
      val userMarkFile = new File("/tmp/geoservice/data/UserMarks.csv")
      val userMarksDao = InMemoryUserMarksDao(userMarkFile)

      val geoTileFile = new File("/tmp/geoservice/data/GeoTiles.csv")
      val geoTilesDao = InMemoryGeoTilesDao(geoTileFile)

      (userMarksDao, geoTilesDao)
    }

    daos match {
      case Failure(e) => logger.error("Fail to create DAOs", e)
      case Success(daos) => startApplication(daos._1, daos._2)
    }
  }

  private def startApplication(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao) = {
    implicit val system = ActorSystem("geoservice")

    val service = system.actorOf(GeoServiceActor.props(userMarksDao, geoTilesDao), "main-actor")

    implicit val timeout = Timeout(5.seconds) //todo: move to config

    val config = ConfigFactory.load()
    val interface = config.getString("spray.interface")
    val port = config.getInt("spray.port")
    logger.debug("Application started!")
    IO(Http) ? Http.Bind(service, interface, port)
  }
}
