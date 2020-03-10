package org.menski.zeebe.incidents;

import io.zeebe.protocol.impl.encoding.MsgPackConverter;
import io.zeebe.protocol.impl.record.value.incident.IncidentRecord;
import org.agrona.DirectBuffer;

public class Incident {

  final long key;
  final IncidentRecord incidentRecord;
  final String variables;

  public Incident(
      final long key, final IncidentRecord incidentRecord, final DirectBuffer variables) {
    this.key = key;
    this.incidentRecord = incidentRecord;
    this.variables = MsgPackConverter.convertToJson(variables);
  }

  @Override
  public String toString() {
    return "Incident{"
        + "key="
        + key
        + ", incidentRecord="
        + incidentRecord
        + ", variables='"
        + variables
        + '\''
        + '}';
  }
}
