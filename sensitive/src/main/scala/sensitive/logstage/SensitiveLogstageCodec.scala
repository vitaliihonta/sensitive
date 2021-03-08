package sensitive.logstage

import izumi.logstage.api.rendering.LogstageWriter
import logstage.LogstageCodec
import sensitive.Sensitive

class SensitiveLogstageCodec[A: Sensitive] extends LogstageCodec[A] {
  override def write(writer: LogstageWriter, value: A): Unit = writer.write(value.asMaskedString)
}
