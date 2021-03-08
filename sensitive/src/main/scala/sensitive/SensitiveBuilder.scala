package sensitive

import scala.language.experimental.macros

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
