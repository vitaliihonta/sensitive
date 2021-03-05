package sensitive

import scala.language.experimental.macros

class SensitiveBuilder[A](private val transform: A => A) {
  def this(base: SensitiveBuilder[A], transform: A => A) =
    this(base.transform andThen transform)

  def withTransformation(f: A => A): SensitiveBuilder[A] =
    new SensitiveBuilder[A](transform andThen f)

  def withFieldMasked[B](f: A => B)(masking: ParameterMasking[B]): SensitiveBuilder[A] =
    macro impl.MaskingMacros.maskImpl[A, B]

  def build: Sensitive[A] = (value: A) => Masked(transform(value))
}
