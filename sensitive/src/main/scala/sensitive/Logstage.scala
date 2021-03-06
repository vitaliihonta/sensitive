package sensitive

import izumi.logstage.api.rendering.LogstageWriter
import logstage.LogstageCodec
import sensitive.derivation.LowPriorityDerived

object Logstage extends LowPriorityDerived {

  @inline final implicit def sensitiveLogstageCodecDefault[A: Sensitive]: LogstageCodec[A] =
    (writer: LogstageWriter, value: A) => writer.write(value.masked.toString)
}
