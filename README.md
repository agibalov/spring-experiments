# apache-spark-experiment

On Ubuntu 15.04:

#### Install Docker

```bash
sudo apt-get install docker.io
```
and reboot.

#### Install Docker Compose

```bash
curl -L https://github.com/docker/compose/releases/download/1.2.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

#### Install Weave

```bash
sudo wget -O /usr/local/bin/weave \
  https://github.com/weaveworks/weave/releases/download/latest_release/weave
sudo chmod a+x /usr/local/bin/weave
```

#### Build images

1. Go to `docker/images/sparkbase` and run `build.sh`.
2. Go to `docker/images/sparkmaster` and run `build.sh`.
3. Go to `docker/images/sparkworker` and run `build.sh`.
4. Run `sudo ./gradlew clean dockerContainer`.

#### Launch Weave

```bash
sudo weave launch
sudo weave launch-dns 10.2.1.254/24 --domain="weave.lan"
sudo weave launch-proxy --with-dns
```

#### Expose host OS to Weave network

```bash
sudo weave expose 10.2.1.23/24 -h the-ubuntu.weave.lan
```

#### Add Weave DNS to ethwe

```bash
nmcli con mod ethwe ipv4.dns "10.2.1.254"
```

#### Launch the app

Go to `spark-streaming-consumer` and
```bash
sudo DOCKER_HOST=tcp://localhost:12375 docker-compose up
```

http://sparkmaster.weave.lan:8080 should be available.
