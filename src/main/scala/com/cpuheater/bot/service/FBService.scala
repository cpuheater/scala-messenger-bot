package com.cpuheater.bot.service

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.model.{HttpHeader, StatusCode, StatusCodes}
import akka.stream.ActorMaterializer
import com.cpuheater.bot.BotConfig
import com.cpuheater.bot.model._
import com.cpuheater.bot.util.HttpClient
import com.typesafe.scalalogging.LazyLogging
import com.cpuheater.bot.json.BotJson._

import scala.concurrent.{ExecutionContext, Future}
import spray.json._

object FBService extends LazyLogging  {

  def verifyToken(token: String, mode: String, challenge: String)
                 (implicit ec: ExecutionContext):
  (StatusCode, List[HttpHeader], Option[Either[String, String]]) = {

    if(mode == "subscribe" && token == BotConfig.fb.verifyToken){
      logger.info(s"Verify webhook token: ${token}, mode ${mode}")
      (StatusCodes.OK, List.empty[HttpHeader], Some(Left(challenge)))
    }
    else {
      logger.error(s"Invalid webhook token: ${token}, mode ${mode}")
      (StatusCodes.Forbidden, List.empty[HttpHeader], None)
    }
  }

  def handleMessage(fbObject: FBPObject)
                   (implicit ec: ExecutionContext, system: ActorSystem,
                    materializer :ActorMaterializer):
  (StatusCode, List[HttpHeader], Option[Either[String, String]]) = {

    logger.info(s"Receive fbObject: $fbObject")
    fbObject.entry.foreach{
      entry =>
        entry.messaging.foreach{ me =>
          val senderId = me.sender.id
          val message = me.message
          message.text match {
            case Some(text) =>
              val fbMessage = FBMessageEventOut(recipient = FBRecipient(senderId),
                message = FBMessage(text = Some(s"Scala messenger bot: $text"),
                  metadata = Some("DEVELOPER_DEFINED_METADATA"))).toJson.toString().getBytes
              HttpClient
                .post(s"${BotConfig.fb.responseUri}?access_token=${BotConfig.fb.pageAccessToken}", fbMessage)
                .map(_ => ())
            case None =>
              logger.info("Receive image")
              Future.successful(())
          }
        }
    }
    (StatusCodes.OK, List.empty[HttpHeader], None)
  }

}
