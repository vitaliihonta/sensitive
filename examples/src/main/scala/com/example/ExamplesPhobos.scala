package com.example

import ru.tinkoff.phobos.derivation.semiauto.deriveElementEncoder
import ru.tinkoff.phobos.derivation.semiauto.deriveXmlEncoder
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.encoding.XmlEncoder

object PhobosCodecs {
  private implicit val cardDataElementEncoder: ElementEncoder[CardData] = deriveElementEncoder[CardData]
  implicit val personXmlEncoder: XmlEncoder[Person]                     = deriveXmlEncoder[Person]("person")
}

object PhobosSensitiveCodecs {
  import PhobosCodecs._

  implicit val theSensitiveXmlEncoder = sensitive.phobos.sensitiveXmlEncoder[Person]
}

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

  import PhobosCodecs._
  println(XmlEncoder[Person].encode(person))

//  NOTE: this won't work because of ambigous implicits.
//  Try to uncomment to see the compiler error message
//  import sensitive.phobos._
//  println(XmlEncoder[Person].encode(person))

//  NOTE: a workaround is to explicitly define a concrete implicit in the lexical scope.
//  It will have precedence over the default one.
  import PhobosSensitiveCodecs._
  println(XmlEncoder[Person].encode(person))

}
