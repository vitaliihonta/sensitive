package sensitive.phobos

import ru.tinkoff.phobos.encoding.AttributeEncoder
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.encoding.PhobosStreamWriter
import ru.tinkoff.phobos.encoding.XmlEncoder
import sensitive.Sensitive
import sensitive.derivation.Derived

class SensitiveElementEncoder[A: Sensitive](implicit ev: Derived[ElementEncoder[A]]) extends ElementEncoder[A] {

  override def encodeAsElement(a: A, sw: PhobosStreamWriter, localName: String, namespaceUri: Option[String]): Unit =
    ev.value.encodeAsElement(a.masked, sw, localName, namespaceUri)
}

class SensitiveAttributeEncoder[A: Sensitive](implicit ev: Derived[AttributeEncoder[A]]) extends AttributeEncoder[A] {

  private val impl = ev.value.contramap[A](_.masked)

  override def encodeAsAttribute(a: A, sw: PhobosStreamWriter, localName: String, namespaceUri: Option[String]): Unit =
    ev.value.encodeAsAttribute(a.masked, sw, localName, namespaceUri)
}

class SensitiveXmlEncoder[A: Sensitive](implicit ev: Derived[XmlEncoder[A]]) extends XmlEncoder[A] {

  override val localname: String            = ev.value.localname
  override val namespaceuri: Option[String] = ev.value.namespaceuri

  override val elementencoder: ElementEncoder[A] = {
    implicit val elementEncoder: Derived[ElementEncoder[A]] = Derived(ev.value.elementencoder)
    new SensitiveElementEncoder[A]
  }
}
