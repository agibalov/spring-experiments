spring-jpa-and-sql-experiment
=============================

Only expected to work on Ubuntu 14.10.

1. (Once per machine) install Docker: `sudo apt-get install -y docker.io`
2. (Once per machine) go to `postgres` folder and run `build-image.sh`. This will create a Docker image named `loki2302/pg:v1` based on Ubuntu with Postgres installed.
3. Before running the tests, launch a container: `postgres/start.sh` (and then eventually stop it with `postgres/stop.sh`). While running, Postgress will be available at `localhost:5432` (username: `postgres`, password: `qwerty`).
