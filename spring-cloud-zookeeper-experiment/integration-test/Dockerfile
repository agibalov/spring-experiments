FROM java:8-jre
COPY build/libs/integration-test-1.0-SNAPSHOT.jar /tmp/test.jar
ENTRYPOINT exec java -jar /tmp/test.jar --select-class me.loki2302.DummyTest