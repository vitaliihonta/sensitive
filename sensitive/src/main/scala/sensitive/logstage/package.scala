package sensitive

import scala.annotation.implicitAmbiguous

// NOTE: mark import as always used in Intellij!
package object logstage {

  @implicitAmbiguous(
    """
Unable to derive sensitive LogstageCodec[${A}]
In most cases it means that the default LogstageCodec[${A}]
and sensitive.phobos import are both in the lexical scope.
Try moving the default LogstageCodec[${A}] into implicit scope (e.g. companion object)
or defining sensitive encoder explicitly:
implicit val theSensitiveLogstageCodec = sensitive.logstage.sensitiveLogstageCodecDefault[${A}]

"""
  ) @inline final implicit def sensitiveLogstageCodecDefault[A: Sensitive]: SensitiveLogstageCodec[A] =
    new SensitiveLogstageCodec[A]
}
