package com.example

import sensitive._

object ExamplesBase extends App {

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

  println("Plain vs masked")
  println(person)
  println(person.masked)

  println("As masked string")
  println(person.asMaskedString)

  println("Card data has toString masked")
  println(person.card)
}
