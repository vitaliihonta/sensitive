package com.example

import io.circe.Encoder
import io.circe.generic.semiauto._
import sensitive._

case class CardData(number: String, expMonth: Int, expYear: Int, cvv: String)

object CardData {

  implicit val sensitiveCardData: Sensitive[CardData] = sensitiveOf[CardData]
    .withFieldMasked(_.number)(
      regexp(
        "[0-9]{4}".r,
        replacement = "****"
      )
    )
    .withFieldMasked(_.cvv)(substitute("***"))
    .build

  implicit val encoder: Encoder[CardData] = deriveEncoder[CardData]

}

case class Person(name: String, phone: String, card: CardData)

object Person {

  implicit val sensitivePerson: Sensitive[Person] = sensitiveOf[Person]
    .withFieldMasked(_.phone)(substitute("***"))
    .withFieldSensitive(_.card)
    .build

  implicit val encoder: Encoder[Person] = deriveEncoder[Person]

}
