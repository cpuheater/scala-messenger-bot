package com.cpuheater.bot

import com.typesafe.config.ConfigFactory

object BotConfig {

  private val config = ConfigFactory.load()
  private val botConfig = config.getConfig("bot")

  object fb {
    private val fb = botConfig.getConfig("fb")

    val appSecret = fb.getString("appSecret")
    val pageAccessToken = fb.getString("pageAccessToken")
    val verifyToken = fb.getString("verifyToken")
    val responseUri = fb.getString("responseUri")
  }

}
