spring-jpa-and-sql-experiment
=============================

Only expected to work on Ubuntu 14.10.

1. (Once per machine) install Docker: `sudo apt-get install -y docker.io`
2. (Once per machine) go to `postgres` folder and run `build-image.sh`. This will create a Docker image named `loki2302/pg:v1` based on Ubuntu with Postgres installed.
3. Before running the tests, launch a container: `postgres/start.sh` (and then eventually stop it with `postgres/stop.sh`). While running, Postgress will be available at `localhost:5432` (username: `postgres`, password: `qwerty`).

### Results
```
app.random-events=10000
app.find-random-user-repetitions=10000
app.find-random-post-repetitions=10000
```

#### My dev machine (Ubuntu Desktop 14.10 x64 under VMWare, 4GB RAM, 4 CPU cores)
```
find random post
             count = 9924
         mean rate = 216.81 calls/second
     1-minute rate = 219.09 calls/second
     5-minute rate = 220.44 calls/second
    15-minute rate = 220.80 calls/second
               min = 1.54 milliseconds
               max = 9.88 milliseconds
              mean = 2.86 milliseconds
            stddev = 0.84 milliseconds
            median = 2.65 milliseconds
              75% <= 3.35 milliseconds
              95% <= 4.44 milliseconds
              98% <= 4.79 milliseconds
              99% <= 5.12 milliseconds
            99.9% <= 9.83 milliseconds
find random user
             count = 10000
         mean rate = 99.02 calls/second
     1-minute rate = 77.79 calls/second
     5-minute rate = 120.52 calls/second
    15-minute rate = 127.85 calls/second
               min = 2.33 milliseconds
               max = 15.44 milliseconds
              mean = 4.04 milliseconds
            stddev = 1.15 milliseconds
            median = 3.85 milliseconds
              75% <= 4.22 milliseconds
              95% <= 5.70 milliseconds
              98% <= 7.48 milliseconds
              99% <= 9.19 milliseconds
            99.9% <= 15.42 milliseconds
```

#### Hosting90.eu - VPS basic (Ubuntu Server 14.04 x64, 2GB RAM, 1 CPU core)
```
find random post
             count = 9995
         mean rate = 142.66 calls/second
     1-minute rate = 143.21 calls/second
     5-minute rate = 144.36 calls/second
    15-minute rate = 144.64 calls/second
               min = 1.05 milliseconds
               max = 64.73 milliseconds
              mean = 4.72 milliseconds
            stddev = 11.39 milliseconds
            median = 1.99 milliseconds
              75% <= 2.79 milliseconds
              95% <= 6.32 milliseconds
              98% <= 55.05 milliseconds
              99% <= 56.23 milliseconds
            99.9% <= 64.70 milliseconds
find random user
             count = 10000
         mean rate = 66.21 calls/second
     1-minute rate = 36.43 calls/second
     5-minute rate = 72.61 calls/second
    15-minute rate = 78.60 calls/second
               min = 1.44 milliseconds
               max = 81.95 milliseconds
              mean = 6.80 milliseconds
            stddev = 14.20 milliseconds
            median = 2.77 milliseconds
              75% <= 3.16 milliseconds
              95% <= 55.29 milliseconds
              98% <= 56.30 milliseconds
              99% <= 59.31 milliseconds
            99.9% <= 81.57 milliseconds
```

#### DigitalOcean - Ubuntu Server 14.10 x64, 1GB RAM, 1 CPU core
```
find random post
             count = 9777
         mean rate = 184.54 calls/second
     1-minute rate = 169.70 calls/second
     5-minute rate = 149.99 calls/second
    15-minute rate = 146.17 calls/second
               min = 1.44 milliseconds
               max = 16.62 milliseconds
              mean = 3.38 milliseconds
            stddev = 2.00 milliseconds
            median = 2.72 milliseconds
              75% <= 4.04 milliseconds
              95% <= 7.45 milliseconds
              98% <= 9.35 milliseconds
              99% <= 10.97 milliseconds
            99.9% <= 16.52 milliseconds
find random user
             count = 10000
         mean rate = 91.76 calls/second
     1-minute rate = 72.21 calls/second
     5-minute rate = 119.97 calls/second
    15-minute rate = 128.93 calls/second
               min = 2.01 milliseconds
               max = 20.76 milliseconds
              mean = 4.30 milliseconds
            stddev = 2.14 milliseconds
            median = 3.47 milliseconds
              75% <= 4.22 milliseconds
              95% <= 8.46 milliseconds
              98% <= 9.90 milliseconds
              99% <= 11.06 milliseconds
            99.9% <= 20.73 milliseconds
```
