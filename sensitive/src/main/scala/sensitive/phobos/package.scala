package sensitive

import ru.tinkoff.phobos.encoding.AttributeEncoder
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.encoding.XmlEncoder
import sensitive.derivation.Derived
import scala.annotation.implicitAmbiguous

package object phobos {

  @implicitAmbiguous(
    """
Unable to derive sensitive ElementEncoder[${A}]
In most cases it means that the default ElementEncoder[${A}]
and sensitive.phobos import are both in the lexical scope.
Try moving the default ElementEncoder[${A}] into implicit scope (e.g. companion object)
or defining sensitive encoder explicitly:
implicit val theSensitiveElementEncoder = sensitive.phobos.sensitiveElementEncoder[${A}]

"""
  ) @inline final implicit def sensitiveElementEncoder[A: Sensitive](
    implicit ev: Derived[ElementEncoder[A]]
  ): SensitiveElementEncoder[A] = new SensitiveElementEncoder[A]

  @implicitAmbiguous(
    """
Unable to derive sensitive AttributeEncoder[${A}]
In most cases it means that the default AttributeEncoder[${A}]
and sensitive.phobos import are both in the lexical scope.
Try moving the default AttributeEncoder[${A}] into implicit scope (e.g. companion object)
or defining sensitive encoder explicitly:
implicit val theSensitiveAttributeEncoder = sensitive.phobos.sensitiveAttributeEncoder[${A}]

"""
  ) @inline final implicit def sensitiveAttributeEncoder[A: Sensitive](
    implicit ev: Derived[AttributeEncoder[A]]
  ): SensitiveAttributeEncoder[A] = new SensitiveAttributeEncoder[A]

  @implicitAmbiguous(
    """
Unable to derive sensitive XmlEncoder[${A}]
In most cases it means that the default XmlEncoder[${A}]
and sensitive.phobos import are both in the lexical scope.
Try moving the default XmlEncoder[${A}] into implicit scope (e.g. companion object)
or defining sensitive encoder explicitly:
implicit val theSensitiveXmlEncoder = sensitive.phobos.sensitiveXmlEncoder[${A}]

"""
  ) @inline final implicit def sensitiveXmlEncoder[A: Sensitive](
    implicit ev: Derived[XmlEncoder[A]]
  ): SensitiveXmlEncoder[A] = new SensitiveXmlEncoder[A]

}
