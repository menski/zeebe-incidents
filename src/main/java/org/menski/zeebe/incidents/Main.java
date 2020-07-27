package org.menski.zeebe.incidents;

import io.zeebe.db.ColumnFamily;
import io.zeebe.db.KeyValuePairVisitor;
import io.zeebe.db.ZeebeDb;
import io.zeebe.db.impl.DbLong;
import io.zeebe.engine.state.DefaultZeebeDbFactory;
import io.zeebe.engine.state.ZbColumnFamilies;
import io.zeebe.engine.state.instance.IncidentKey;
import io.zeebe.engine.state.instance.IncidentState;
import io.zeebe.engine.state.instance.VariablesState;
import io.zeebe.protocol.impl.record.value.incident.IncidentRecord;
import java.io.File;
import java.lang.reflect.Field;
import org.agrona.DirectBuffer;

public class Main {

  private static boolean firstElement = true;

  public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    if (args.length < 1) {
      System.err.println("Please specify state dir as argument");
      System.err.println("zeebe-incidents PATH_TO_DB");
      System.exit(1);
    }

    String stateDir = args[0];
    System.err.println("Reading incidents for partition from state " + stateDir);

    ZeebeDb<ZbColumnFamilies> db = DefaultZeebeDbFactory.defaultFactory(ZbColumnFamilies.class)
        .createDb(new File(stateDir));

    Field workflowInstanceIncidentColumnFamilyField = IncidentState.class
        .getDeclaredField("workflowInstanceIncidentColumnFamily");
    workflowInstanceIncidentColumnFamilyField.setAccessible(true);
    Field jobIncidentColumnFamilyField = IncidentState.class.getDeclaredField("jobIncidentColumnFamily");
    jobIncidentColumnFamilyField.setAccessible(true);

    final IncidentState state = new IncidentState(db, db.createContext(), 0);
    final VariablesState variablesState = new VariablesState(db, db.createContext(), () -> 0);
    final ColumnFamily<DbLong, IncidentKey> workflowInstanceIncidentColumnFamily = (ColumnFamily<DbLong, IncidentKey>) workflowInstanceIncidentColumnFamilyField.get(state);
    ColumnFamily<DbLong, IncidentKey> jobIncidentColumnFamily = (ColumnFamily<DbLong, IncidentKey>) jobIncidentColumnFamilyField.get(state);
    System.out.println("{\"workflowIncidents\": [");
    firstElement = true;
    workflowInstanceIncidentColumnFamily.whileTrue(printIncident(state, variablesState));
    System.out.println("], \"jobIncidents\": [");
    firstElement = true;
    jobIncidentColumnFamily.whileTrue(printIncident(state, variablesState));
    System.out.println("]}");
  }

  private static KeyValuePairVisitor<DbLong, IncidentKey> printIncident(final IncidentState state,
      final VariablesState variablesState) {
    return (key, value) -> {
      IncidentRecord incidentRecord = state.getIncidentRecord(value.get());
      final DirectBuffer variablesAsDocument =
          variablesState.getVariablesAsDocument(incidentRecord.getVariableScopeKey());
      if (!firstElement) {
        System.out.println(",");
      }
      else {
        firstElement = false;
      }
      System.out.println(new Incident(key.getValue(), incidentRecord, variablesAsDocument));
      return true;
    };
  }

}
