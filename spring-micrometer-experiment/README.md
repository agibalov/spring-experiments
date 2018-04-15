# spring-micrometer-experiment

Learning to use Micrometer.

#### Running it

* Build the app with `./gradlew bootJar`
* Run with `docker-compose up`
* The app is running at http://localhost:8080/
* Prometeus is at: http://localhost:9090/
* Grafana is at: http://localhost:3000 (log in with admin/qwerty)

#### Updating the Grafana dashboard

To modify the Grafana dashboard, make change in Grafana, export and save locally as `dashboard.json`. THEN MAKE SURE TO replace all `${DS_PROMETHEUS1}` with `Prometeus1` (looks like it's this issue: https://github.com/grafana/grafana/issues/10786) (`fix-dashboard-json-after-export.sh` does this fix)
