package com.example

import io.circe.Encoder
import io.circe.generic.semiauto._
import sensitive._
import scala.util.matching.Regex

case class CardData(number: String, expMonth: Int, expYear: Int, cvv: String) extends ToStringMasked[CardData]

object CardData {

  implicit val sensitiveCardData: Sensitive[CardData] = sensitiveOf[CardData]
    .withFieldMasked(_.number)(
      regexp(
        "([0-9]{4})-([0-9]{4})-([0-9]{4})-([0-9]{4})".r,
        replaceAll(replacer = { case Regex.Groups(first, second, _, fourth) =>
          s"$first-${second.take(2)}**-****-$fourth"
        })
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
