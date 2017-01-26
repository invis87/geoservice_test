package com.pronvis.onefactor.test

import akka.actor.{Actor, ActorRefFactory, Props}
import com.pronvis.onefactor.test.data.dao.{IGeoTilesDao, IUserMarksDao}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor

object GeoServiceActor {
  def props(userMarksDao: IUserMarksDao, geoTilesDao: IGeoTilesDao): Props = Props(new GeoServiceActor(userMarksDao, geoTilesDao))
}

class GeoServiceActor(val userMarksDao: IUserMarksDao, val geoTilesDao: IGeoTilesDao) extends Actor with GeoService with LazyLogging {

  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory: ActorRefFactory = context

  def receive = runRoute(route)
}