spring-h2-experiment
====================

When running the app with `gradlew clean run`, few things happen:

1. The H2 database is started as TCP server listening at localhost:9092
2. The H2 web console is available at [http://localhost:8082](http://localhost:8082). Use connection string like `jdbc:h2:tcp://localhost:9092/mem:test;DB_CLOSE_DELAY=-1` to connect to p.1.
3. The database is initialized with this [script](https://github.com/loki2302/spring-h2-experiment/blob/master/src/main/resources/db/migration/V1__Initial.sql)
4. There's a hello world at [http://localhost:8080](http://localhost:8080)
