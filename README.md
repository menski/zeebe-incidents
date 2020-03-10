# Read incidents from Zeebe snapshots

## Build

```
mvn clean package
```

## Run

```
java -jar target/zeebe-incidents-1.0-SNAPSHOT.jar PATH_TO_BROKER/zeebe-broker-0.20.1/data/partition-2/state/snapshots/8589938784/

Reading incidents for partition from state PATH_TO_BROKER/zeebe-broker-0.20.1/data/partition-2/state/snapshots/8589938784/
Workflow instance incidents
Job incidents for partitions
4503599627370502 : {"errorMessage":"findme2","bpmnProcessId":"process","workflowKey":2251799813685249,"elementId":"ServiceTask_0kt6c5i","variableScopeKey":4503599627370501,"workflowInstanceKey":4503599627370497,"errorType":"JOB_NO_RETRIES","jobKey":4503599627370502,"elementInstanceKey":4503599627370501}
```
