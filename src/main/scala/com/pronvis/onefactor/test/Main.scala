package com.pronvis.onefactor.test

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

object Main {

  def main(args: Array[String]) {
    startApplication()
  }

  private def startApplication() = {
    implicit val system = ActorSystem("geoservice")

    val service = system.actorOf(Props[RouteActor], "main-actor")

    implicit val timeout = Timeout(5.seconds) //todo: move to config

    val config = ConfigFactory.load()
    val interface = config.getString("spray.interface")
    val port = config.getInt("spray.port")
    IO(Http) ? Http.Bind(service, interface, port)
  }
}
