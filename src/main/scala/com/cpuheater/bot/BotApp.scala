package com.cpuheater.bot

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import com.cpuheater.bot.route.FBRoute
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

object BotApp extends App with FBRoute with LazyLogging {

  val decider: Supervision.Decider = { e =>
    logger.error(s"Exception in stream $e")
    Supervision.Stop
  }

  implicit val actorSystem = ActorSystem("bot", ConfigFactory.load)
  val materializerSettings = ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
  implicit val materializer = ActorMaterializer(materializerSettings)(actorSystem)

  implicit val ec = actorSystem.dispatcher

  val routes = {
    logRequestResult("bot") {
      fbRoute
    }
  }

  implicit val timeout = Timeout(30.seconds)

  val routeLogging = DebuggingDirectives.logRequestResult("RouteLogging", Logging.InfoLevel)(routes)

  Http().bindAndHandle(routeLogging, "localhost", 8080)
  logger.info("Starting")

}
