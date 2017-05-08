package com.cpuheater.bot

package object model {

  case class Payload(url: String)

  case class Attachment(`type`: String,payload: Payload)

  case class FBMessage(mid: Option[String] = None,
                       seq: Option[Long] = None,
                       text: Option[String] = None,
                       metadata: Option[String] = None,
                       attachment: Option[Attachment] = None)

  case class FBSender(id: String)

  case class FBRecipient(id: String)


  case class FBMessageEventIn(sender: FBSender,
                              recipient: FBRecipient,
                              timestamp: Long,
                              message: FBMessage)

  case class FBMessageEventOut(recipient: FBRecipient,
                               message: FBMessage)

  case class FBEntry(id: String, time: Long, messaging: List[FBMessageEventIn])

  case class FBPObject(`object`: String, entry: List[FBEntry])

}
