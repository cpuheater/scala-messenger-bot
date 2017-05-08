package com.cpuheater.bot.json

import com.cpuheater.bot.model._
import spray.json.{DeserializationException, JsString, JsValue, JsonFormat, _}


object BotJson extends  DefaultJsonProtocol{

  implicit val payloadFormat = jsonFormat1(Payload)

  implicit val attachmentFormat = jsonFormat2(Attachment)

  implicit val fbMessageFormat = jsonFormat5(FBMessage)

  implicit val fbSenderFormat = jsonFormat1(FBSender)

  implicit val fbRecipientFormat = jsonFormat1(FBRecipient)

  implicit val fbMessageObjectFormat = jsonFormat4(FBMessageEventIn)

  implicit val fbMessageEventOutFormatOut = jsonFormat2(FBMessageEventOut)

  implicit val fbEntryFormat = jsonFormat3(FBEntry)

  implicit val fbPObjectFormat = jsonFormat2(FBPObject)


}
