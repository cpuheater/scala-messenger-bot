package com.cpuheater.bot.util

import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.http.scaladsl.model.{HttpHeader, HttpRequest, StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directive0, Directives}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import akka.stream.Materializer
import akka.util.ByteString
import com.cpuheater.bot.BotConfig
import org.apache.commons.codec.binary.Hex
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._



trait RouteSupport extends LazyLogging with Directives {

  def verifyPayload(req: HttpRequest)
                   (implicit materializer: Materializer, ec: ExecutionContext): Directive0 = {

    def isValid(payload: Array[Byte], secret: String, expected: String): Boolean = {
      val secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1")
      val mac = Mac.getInstance("HmacSHA1")
      mac.init(secretKeySpec)
      val result = mac.doFinal(payload)

      val computedHash = Hex.encodeHex(result).mkString
      logger.info(s"Computed hash: $computedHash")

      computedHash == expected
    }

    req.headers.find(_.name == "X-Hub-Signature").map(_.value()) match {
      case Some(token) =>
        val payload =
          Await.result(req.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")), 5 second)
        logger.info(s"Receive token ${token} and payload ${payload}")
        val elements = token.split("=")
        val method = elements(0)
        val signaturedHash = elements(1)
        if(isValid(payload.getBytes, BotConfig.fb.appSecret, signaturedHash))
          pass
        else {
          logger.error(s"Tokens are different, expected ${signaturedHash}")
          complete(StatusCodes.Forbidden)
        }

      case None =>
        logger.error(s"X-Hub-Signature is not defined")
        complete(StatusCodes.Forbidden)
    }

  }

}

