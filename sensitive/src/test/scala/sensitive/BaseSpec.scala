package sensitive

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class BaseSpec extends AnyWordSpecLike with Matchers {
  "sensitiveOf" should {
    "build flat instance correctly" in {
      case class Person(name: String, card: String)

      implicit val sensitivePerson: Sensitive[Person] =
        sensitiveOf[Person].withFieldMasked(_.card)(substitute("***")).build

      Person(
        name = "John",
        card = "4242424242424242"
      ).masked shouldEqual Person(
        name = "John",
        card = "***"
      )
    }

    "build instance with sensitive field correctly" in {
      case class Card(number: String, cvv:      String)
      case class Person(name: String, password: String, card: Card)

      implicit val sensitiveCard: Sensitive[Card] =
        sensitiveOf[Card]
          .withFieldMasked(_.number)(substitute("****-****-****-****"))
          .withFieldMasked(_.cvv)(substitute("***"))
          .build

      implicit val sensitivePerson: Sensitive[Person] =
        sensitiveOf[Person]
          .withFieldMasked(_.password)(substitute("x-pass-x"))
          .withFieldSensitive(_.card)
          .build

      Person(
        name = "John",
        password = "123",
        card = Card(
          number = "4242424242424242",
          cvv = "123"
        )
      ).masked shouldEqual Person(
        name = "John",
        password = "x-pass-x",
        card = Card(
          number = "****-****-****-****",
          cvv = "***"
        )
      )
    }

    "not take sensitive field into account when it's not provided explicitly" in {
      case class Card(number: String, cvv:      String)
      case class Person(name: String, password: String, card: Card)

      implicit val sensitiveCard: Sensitive[Card] =
        sensitiveOf[Card]
          .withFieldMasked(_.number)(substitute("****-****-****-****"))
          .withFieldMasked(_.cvv)(substitute("***"))
          .build

      implicit val sensitivePerson: Sensitive[Person] =
        sensitiveOf[Person]
          .withFieldMasked(_.password)(substitute("x-pass-x"))
          .build

      Person(
        name = "John",
        password = "123",
        card = Card(
          number = "4242424242424242",
          cvv = "123"
        )
      ).masked shouldEqual Person(
        name = "John",
        password = "x-pass-x",
        card = Card(
          number = "4242424242424242",
          cvv = "123"
        )
      )
    }
  }
}
