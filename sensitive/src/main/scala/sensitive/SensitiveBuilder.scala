package sensitive

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros

case class SensitiveBuilderTransformation[A](transformField: A => A, maskField: A => String)

//@compileTimeOnly("Should be only compile time")
case class SensitiveBuilder[A <: Product](@internalApi transformations: Map[String, SensitiveBuilderTransformation[Any]]) {

//  @internalApi
//  def this(base: SensitiveBuilder[A], transform: A => A, maskedName: (String, Any => String)) =
//    this(base.transform andThen transform, base.maskedNames + maskedName)

  def withFieldMasked[B](f: A => B)(masking: ParameterMasking[B]): SensitiveBuilder[A] =
    macro impl.MaskingMacros.maskImpl[A, B]

  def withFieldSensitive[B](f: A => B): SensitiveBuilder[A] =
    macro impl.MaskingMacros.sensitiveFieldImpl[A, B]

  def build: Sensitive[A] =
    macro impl.MaskingMacros.buildImpl[A]
}

@internalApi
class ProductSensitive[A <: Product](transform: A => A, maskedStrings: Map[Int, Any => String]) extends Sensitive[A] {

  override def masked(value: A): Masked[A] = Masked(transform(value))

  override def asMaskedString(value: A): AsMaskedString[A] = {
    val sc = new StringBuilder
    sc.append(value.productPrefix)
    sc.append('(')
    value.productIterator.zipWithIndex.foreach {
      case (value, idx) =>
        val shown = maskedStrings.get(idx).fold(ifEmpty = value.toString)(_.apply(value))
        if (idx != 0) {
          sc.append(',')
        }
        sc.append(shown)
    }
    sc.append(')')
    AsMaskedString[A](sc.toString)
  }
}
