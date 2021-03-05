package sensitive

trait Sensitive[A] { self =>
  def masked(value: A): Masked[A]
}

class SensitiveOps[A](private val self: A) extends AnyVal {
  def masked(implicit sensitive: Sensitive[A]): A = sensitive.masked(self)
}
