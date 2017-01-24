package com.pronvis.onefactor.test

import akka.actor.{Actor, ActorRefFactory}
import akka.util.Timeout
import com.pronvis.onefactor.test.api.Responses.TestConnection
import com.typesafe.scalalogging.LazyLogging
import spray.routing.HttpService

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

//json part
import com.pronvis.onefactor.test.serialization.JsonProtocol._
import spray.httpx.SprayJsonSupport._

class RouteActor extends Actor with GeoserviceService with LazyLogging {

  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory: ActorRefFactory = context

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    logger.debug("in testConnection")
    TestConnection("all fine!")
  }
}

trait GeoserviceService extends HttpService {

  implicit val timeout = Timeout(5.seconds) //todo: move to config

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def testConnection: TestConnection

  val route = {
    (get & path("testConnection")) {
      complete {
        testConnection
      }
    }
  }
}
