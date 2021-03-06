package com.example

import io.circe.syntax._

object ExamplesCirce extends App {

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

  println("Json vs masked json")
  println(person.asJson.noSpaces)

  import sensitive.Circe._

  println(person.asJson.noSpaces)
}
