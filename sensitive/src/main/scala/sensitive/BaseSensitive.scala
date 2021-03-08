package sensitive

trait BaseSensitive[A] {
  def maskBase(value: A): A

  def maskedStringBase(value: A): String

  @internalApi final def asAny: BaseSensitive[Any] = this.asInstanceOf[BaseSensitive[Any]]
}
