package com.example

import izumi.logstage.api.Log
import izumi.logstage.api.rendering.LogstageCodec
import izumi.logstage.api.rendering.json.LogstageCirceRenderingPolicy
import izumi.logstage.sink.ConsoleSink
import logstage.IzLogger
import logstage.circe.LogstageCirceCodec

object ExamplesLogging extends App {

  val person = Person(
    name = "John",
    phone = "123-456-789",
    card = CardData(
      number = "4242-4242-4242-4242",
      expMonth = 1,
      expYear = 22,
      cvv = "123"
    )
  )

  val logger     = IzLogger()
  val jsonLogger = IzLogger(Log.Level.Info, List(ConsoleSink(LogstageCirceRenderingPolicy())))

  logger.info(s"Plain logging $person")
  locally {
    implicit val personLogstageCodec: LogstageCodec[Person] = LogstageCirceCodec.derived[Person]
    jsonLogger.info(s"Json logging $person")
  }

  locally {
    import sensitive.logstage._
    logger.info(s"Plain logging masked $person")
  }

  locally {
    import sensitive.circe._
    implicit val personLogstageCodec: LogstageCodec[Person] = LogstageCirceCodec.derived[Person]
    jsonLogger.info(s"Json logging masked $person")

  }
}
