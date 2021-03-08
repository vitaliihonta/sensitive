# sensitive
Case class fields masking

## Example

```scala
import sensitive._
import scala.util.matching.Regex

case class CardData(number: String, expMonth: Int, expYear: Int, cvv: String)

object CardData {

  implicit val sensitiveCardData: Sensitive[CardData] = sensitiveOf[CardData]
    .withFieldMasked(_.number)(
      regexp(
        "([0-9]{4})-([0-9]{4})-([0-9]{4})-([0-9]{4})".r,
        replaceAll(replacer = {
          case Regex.Groups(first, second, _, fourth) =>
            s"$first-${second.take(2)}**-****-$fourth"
        })
      )
    )
    .withFieldMasked(_.cvv)(substitute("***"))
    .build

}

case class Person(name: String, phone: String, card: CardData)

object Person {

  implicit val sensitivePerson: Sensitive[Person] = sensitiveOf[Person]
    .withFieldMasked(_.phone)(substitute("***"))
    .withFieldSensitive(_.card)
    .build

}
```

Generated code for CardData:
```scala
  final class $anon extends sensitive.ProductSensitive[com.example.CardData] {
    def <init>() = {
      super.<init>();
      ()
    };
    private val transformations$macro$6 = Predef.Map.empty[String, sensitive.BaseSensitive[Any]].+[sensitive.BaseSensitive[Any]](scala.Predef.ArrowAssoc[String]("number").->[sensitive.BaseSensitive[Any]](sensitive.`package`.regexp(scala.Predef.augmentString("([0-9]{4})-([0-9]{4})-([0-9]{4})-([0-9]{4})").r, sensitive.`package`.replaceAll(((x0$1: scala.util.matching.Regex.Match) => x0$1 match {
  case scala.util.matching.Regex.Groups.unapplySeq(<unapply-selector>) <unapply> ((first @ _), (second @ _), _, (fourth @ _)) => ("".+(first).+("-").+(scala.Predef.augmentString(second).take(2)).+("**-****-").+(fourth): String)
}))).asAny)).+[sensitive.BaseSensitive[Any]](scala.Predef.ArrowAssoc[String]("cvv").->[sensitive.BaseSensitive[Any]](sensitive.`package`.substitute[String]("***").asAny));
    override def masked(value$macro$5: com.example.CardData): _root_.sensitive.Masked[com.example.CardData] = _root_.sensitive.Masked(value$macro$5.copy(number = transformations$macro$6("number").maskBase(value$macro$5.number).asInstanceOf[String], cvv = transformations$macro$6("cvv").maskBase(value$macro$5.cvv).asInstanceOf[String]));
    override def asMaskedString(value$macro$5: com.example.CardData): _root_.sensitive.AsMaskedString[com.example.CardData] = _root_.sensitive.AsMaskedString({
      val sc$macro$7 = new StringBuilder();
      sc$macro$7.append("CardData");
      sc$macro$7.append('(');
      sc$macro$7.append(transformations$macro$6("number").maskedStringBase(value$macro$5.number));
      {
        sc$macro$7.append(',');
        sc$macro$7.append(value$macro$5.expMonth.toString)
      };
      {
        sc$macro$7.append(',');
        sc$macro$7.append(value$macro$5.expYear.toString)
      };
      {
        sc$macro$7.append(',');
        sc$macro$7.append(transformations$macro$6("cvv").maskedStringBase(value$macro$5.cvv))
      };
      sc$macro$7.append(')');
      sc$macro$7.toString
    })
  };
  new $anon()
```
Generated code for Person

```scala
  final class $anon extends sensitive.ProductSensitive[com.example.Person] {
    def <init>() = {
      super.<init>();
      ()
    };
    private val transformations$macro$5 = Predef.Map.empty[String, sensitive.BaseSensitive[Any]].+[sensitive.BaseSensitive[Any]](scala.Predef.ArrowAssoc[String]("phone").->[sensitive.BaseSensitive[Any]](sensitive.`package`.substitute[String]("***").asAny)).+[sensitive.BaseSensitive[Any]](scala.Predef.ArrowAssoc[String]("card").->[sensitive.BaseSensitive[Any]](example.this.CardData.sensitiveCardData.asAny));
    override def masked(value$macro$4: com.example.Person): _root_.sensitive.Masked[com.example.Person] = _root_.sensitive.Masked(value$macro$4.copy(phone = transformations$macro$5("phone").maskBase(value$macro$4.phone).asInstanceOf[String], card = transformations$macro$5("card").maskBase(value$macro$4.card).asInstanceOf[com.example.CardData]));
    override def asMaskedString(value$macro$4: com.example.Person): _root_.sensitive.AsMaskedString[com.example.Person] = _root_.sensitive.AsMaskedString({
      val sc$macro$6 = new StringBuilder();
      sc$macro$6.append("Person");
      sc$macro$6.append('(');
      sc$macro$6.append(value$macro$4.name.toString);
      {
        sc$macro$6.append(',');
        sc$macro$6.append(transformations$macro$5("phone").maskedStringBase(value$macro$4.phone))
      };
      {
        sc$macro$6.append(',');
        sc$macro$6.append(transformations$macro$5("card").maskedStringBase(value$macro$4.card))
      };
      sc$macro$6.append(')');
      sc$macro$6.toString
    })
  };
  new $anon()
```

**Note**: currently the generated code allocates just the typeclass implementation itself plus a single Map with captured typeclass instances (probably can be optimized in future)