[![sensitive Scala version support](https://index.scala-lang.org/vitaliihonta/sensitive/sensitive/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/vitaliihonta/sensitive/sensitive)
![Build status](https://github.com/vitaliihonta/sensitive/actions/workflows/publish.yaml/badge.svg)
[![codecov](https://codecov.io/gh/vitaliihonta/sensitive/branch/main/graph/badge.svg?token=T8NBC4R360)](https://codecov.io/gh/vitaliihonta/sensitive)


# Sensitive
**Sensitive** is a library which allows you to mask sensitive data in **case class** fields.  
It provides Typeclasses for masking and a concise DSL for building them (kinda similar to `chimney` library).  

## Use cases

- Masking sensitive data in class instances
- Masking data in serializers (like JSON encoders)
- Masking data in logs (using LogStage)

## Install

```sbt
// Core
libraryDependencies += "com.github.vitaliihonta" %% "sensitive" % "<VERSION>"
```

NOTE: dependencies on `circe` for JSON and `logtage` are optional!
So adding **Sensitive** as a dependency doesn't bring them to your classpath  
until you add them explicitly.

## Example

For instance, you can define the following masking logic with **Sensitive**:

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

Then you could use this **Sensitive** instance as follows:
```scala
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

person.asMaskedString
```

which will produce the following output:
```
Person(John,***,CardData(4242-42**-****-4242,1,22,***))
```

Additionally, you may extend `ToStringMasked` class, so that `.toString` will produce the String representation with masked fields: 

```scala
case class CardData(number: String, expMonth: Int, expYear: Int, cvv: String) 
  // This will catch up the Sensitive instance
  extends ToStringMasked[CardData]
```

More examples could be found in [the examples module](./examples)

### Generated code

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