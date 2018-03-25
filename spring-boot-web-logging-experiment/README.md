# spring-boot-web-logging-experiment

1. Using Spring Boot to configure app logging (Logback)
2. Using [logback-access-spring-boot-starter](https://github.com/akihyro/logback-access-spring-boot-starter) to use Spring Boot to configure Tomcat's access logging (Logback)

#### Building and running it

* Build with `./gradlew clean bootJar`
* Run with `./run-dev.sh` to run with console-only logging.
* Run with `./run-prod.sh` to run with console and file logging.
* After running it, try `./grep-logs-demo.sh`. This illustrates how to use [jq](https://stedolan.github.io/jq/) to grep JSON logs.
