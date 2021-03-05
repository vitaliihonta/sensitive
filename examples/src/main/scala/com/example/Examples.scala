package com.example

import sensitive._
import io.circe.syntax._

object Examples extends App {

  val person = Person(name = "John", phone = "123-456-789", cardNumber = "4242-4242-4242-4242")

  println("Plain vs masked")
  println(person)
  println(person.masked)

  println("---")

  println("Json vs masked json")
  println(person.asJson.noSpaces)
  import sensitive.Circe._

  println(person.asJson.noSpaces)
}
