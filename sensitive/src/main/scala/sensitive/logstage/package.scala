package sensitive

// NOTE: mark import as always used in Intellij!
package object logstage {

  @inline final implicit def sensitiveLogstageCodecDefault[A: Sensitive]: SensitiveLogstageCodec[A] =
    new SensitiveLogstageCodec[A]
}
