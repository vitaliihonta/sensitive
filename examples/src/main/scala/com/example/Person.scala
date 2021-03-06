package com.example

import io.circe.Encoder
import sensitive._
import io.circe.generic.semiauto._
import ru.tinkoff.phobos.derivation.semiauto.deriveElementEncoder
import ru.tinkoff.phobos.derivation.semiauto.deriveXmlEncoder
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.encoding.XmlEncoder

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

  implicit val cardDataElementEncoder: ElementEncoder[CardData] = deriveElementEncoder[CardData]
}

case class Person(name: String, phone: String, card: CardData)

object Person {

  implicit val sensitivePerson: Sensitive[Person] = sensitiveOf[Person]
    .withFieldMasked(_.phone)(substitute("***"))
    .withFieldSensitive(_.card)
    .build

  implicit val encoder: Encoder[Person] = deriveEncoder[Person]

  implicit val personXmlEncoder: XmlEncoder[Person] = deriveXmlEncoder[Person]("person")
}
