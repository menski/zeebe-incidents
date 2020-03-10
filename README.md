# Read incidents from Zeebe snapshots

## Build

```
mvn clean package
```

## Run

```
java -jar target/zeebe-incidents-1.0-SNAPSHOT-jar-with-dependencies.jar zeebe-broker-0.20.1/data/partition-2/state/snapshots/8589938784/
```
