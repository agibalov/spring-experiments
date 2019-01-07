# spark-core-app

A Spark hello world aimed to check what it looks like when there's a Spark cluster and one deploys an application bundle.

### Initial setup

Make sure you have a local copy of Apache Spark 1.3.1 (Hadoop 2.6).

Build an application bundle: `./gradlew clean shadowJar`. This will create an uber jar at `/build/libs/`.

Add these to `/etc/hosts`:
```
127.0.0.1	sparkmaster
127.0.0.2	sparkworker1
127.0.0.3	sparkworker2
```

Build the Docker images:
* `docker/sparkbase/build.sh`
* `docker/sparkmaster/build.sh`
* `docker/sparkworker/build.sh`

### Launching the whole thing

Launch the Spark cluster: go to `/docker` and execute `sudo docker-compose up`:

* Spark Master UI URL: http://sparkmaster:8080
* Spark Master URL: spark://sparkmaster:7077
* Spark Worker #1 UI: http://sparkworker1:8081
* Spark Worker #2 UI: http://sparkworker2:8082

Go to `/build/libs/` and execute `~/spark-1.3.1-bin-hadoop2.6/bin/spark-submit --class me.loki2302.App --master spark://sparkmaster:7077 apache-spark-bundle-experiment-1.0-all.jar` (this assumes that Spark is at your home). You should see it does something *impressive* for quite a while and then displays a final computation result - `231` (11+22+33+44+55+66).

