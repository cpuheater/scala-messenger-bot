package com.cpuheater.bot.util

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object HttpClient extends LazyLogging {

  def post(uri: String, body: Array[Byte])(implicit ec: ExecutionContext, system: ActorSystem, materializer :ActorMaterializer): Future[Unit] = {
    val entity = HttpEntity(MediaTypes.`application/json`, body)
    val response: Future[HttpResponse] =  Http().singleRequest(HttpRequest(HttpMethods.POST, Uri(uri), entity = entity))

    val result = response.flatMap{
      response =>
        response.status match {
          case status if status.isSuccess =>
            Future.successful()
          case status =>
            Future.successful(throw new IOException(s"Token request failed with status ${response.status} and error ${response.entity}"))
        }
    }
    result.onComplete{
      case Success(response) =>
        logger.info(s"Success after sending response $response")
      case Failure(ex) =>
        logger.info(s"Failure after sending response to: $uri $ex" )
    }
    result

  }

}

