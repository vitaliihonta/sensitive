package sensitive

import scala.language.experimental.macros

@internalApi
sealed trait SensitiveBuilderTransformation[A] {
  def transformField(value: A): A
  def maskField(value:      A): String

  final def asAny: SensitiveBuilderTransformation[Any] = this.asInstanceOf[SensitiveBuilderTransformation[Any]]
}

@internalApi
object SensitiveBuilderTransformation {

  final case class MaskTransform[A](underlying: ParameterMasking[A]) extends SensitiveBuilderTransformation[A] {
    override def transformField(value: A): A = underlying.apply(value)

    override def maskField(value: A): String = transformField(value).toString
  }

  final case class SensitiveTransform[A](underlying: Sensitive[A]) extends SensitiveBuilderTransformation[A] {
    override def transformField(value: A): A = underlying.masked(value)

    override def maskField(value: A): String = underlying.asMaskedString(value)
  }
}

class SensitiveBuilder[A](
  @internalApi val valNames:        Set[String],
  @internalApi val transformations: Map[String, BaseSensitive[Any]]) {

  def withFieldMasked[B](f: A => B)(masking: ParameterMasking[B]): SensitiveBuilder[A] =
    macro impl.MaskingMacros.maskImpl[A, B]

  def withFieldSensitive[B](f: A => B): SensitiveBuilder[A] =
    macro impl.MaskingMacros.sensitiveFieldImpl[A, B]

  def build: Sensitive[A] =
    macro impl.MaskingMacros.buildImpl[A]
}

@internalApi
abstract class ProductSensitive[A] extends Sensitive[A]
