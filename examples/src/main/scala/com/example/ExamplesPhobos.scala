package com.example

import ru.tinkoff.phobos.encoding.XmlEncoder

object ExamplesPhobos extends App {

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

  println(XmlEncoder[Person].encode(person))

  import sensitive.Phobos._

  println(XmlEncoder[Person].encode(person))

}
