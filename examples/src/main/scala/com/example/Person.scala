package com.example

import io.circe.Encoder
import sensitive._
import io.circe.generic.semiauto._

case class Person(name: String, phone: String, cardNumber: String)

object Person {

  implicit val sensitivePerson: Sensitive[Person] = sensitiveOf[Person]
    .withFieldMasked(_.phone)(substitute("***"))
    .withFieldMasked(_.cardNumber)(
      regexp(
        "[0-9]{4}".r,
        replacement = "****"
      )
    )
    .build

  implicit val encoder: Encoder[Person] = deriveEncoder[Person]
}
