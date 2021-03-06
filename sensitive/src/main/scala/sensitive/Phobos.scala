package sensitive

import ru.tinkoff.phobos.encoding.AttributeEncoder
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.encoding.XmlEncoder
import sensitive.derivation.Derived
import sensitive.derivation.LowPriorityDerived

object Phobos extends LowPriorityDerived {

  @inline final implicit def sensitiveElementEncoder[A: Sensitive](
    implicit ev: Derived[ElementEncoder[A]]
  ): ElementEncoder[A] =
    ev.value.contramap[A](_.masked)

  @inline final implicit def sensitiveAttributeEncoder[A: Sensitive](
    implicit ev: Derived[AttributeEncoder[A]]
  ): AttributeEncoder[A] =
    ev.value.contramap[A](_.masked)

  @inline final implicit def sensitiveXmlEncoder[A: Sensitive](implicit ev: Derived[XmlEncoder[A]]): XmlEncoder[A] =
    new XmlEncoder[A] {
      private val underlying = ev.value

      override val localname: String            = underlying.localname
      override val namespaceuri: Option[String] = underlying.namespaceuri

      override val elementencoder: ElementEncoder[A] =
        sensitiveElementEncoder(implicitly[Sensitive[A]], Derived(underlying.elementencoder))
    }
}
