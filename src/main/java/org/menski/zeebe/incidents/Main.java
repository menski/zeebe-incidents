package org.menski.zeebe.incidents;

import io.zeebe.db.ColumnFamily;
import io.zeebe.db.ZeebeDb;
import io.zeebe.db.impl.DbLong;
import io.zeebe.engine.state.DefaultZeebeDbFactory;
import io.zeebe.engine.state.ZbColumnFamilies;
import io.zeebe.engine.state.instance.IncidentState;
import io.zeebe.protocol.impl.record.value.incident.IncidentRecord;
import java.io.File;
import java.lang.reflect.Field;

public class Main {

  public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    if (args.length < 1) {
      System.err.println("Please specify state dir as argument");
      System.err.println("zeebe-incidents PATH_TO_DB");
      System.exit(1);
    }

    String stateDir = args[0];
    System.out.println("Reading incidents for partition from state " + stateDir);

    ZeebeDb<ZbColumnFamilies> db = DefaultZeebeDbFactory.defaultFactory(ZbColumnFamilies.class)
        .createDb(new File(stateDir));

    Field workflowInstanceIncidentColumnFamilyField = IncidentState.class
        .getDeclaredField("workflowInstanceIncidentColumnFamily");
    workflowInstanceIncidentColumnFamilyField.setAccessible(true);
    Field jobIncidentColumnFamilyField = IncidentState.class.getDeclaredField("jobIncidentColumnFamily");
    jobIncidentColumnFamilyField.setAccessible(true);

    final IncidentState state = new IncidentState(db, db.createContext(), 0);
    final ColumnFamily<DbLong, DbLong> workflowInstanceIncidentColumnFamily = (ColumnFamily<DbLong, DbLong>) workflowInstanceIncidentColumnFamilyField.get(state);
    ColumnFamily<DbLong, DbLong> jobIncidentColumnFamily = (ColumnFamily<DbLong, DbLong>) jobIncidentColumnFamilyField.get(state);
    System.out.println("Workflow instance incidents");
    workflowInstanceIncidentColumnFamily.whileTrue(
        (key, value) -> {
          IncidentRecord incidentRecord = state.getIncidentRecord(value.getValue());
          System.out.println(incidentRecord.toJson());
          return true;
        });
    System.out.println("Job incidents for partitions");
    jobIncidentColumnFamily.whileTrue(
        (key, value) -> {
          IncidentRecord incidentRecord = state.getIncidentRecord(value.getValue());
          System.out.println(incidentRecord.toJson());
          return true;
        });

  }

}