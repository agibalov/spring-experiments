# consul-experiment

A very minimal [Consul](https://www.consul.io/) / Spring hello world.

* Build the app with `./gradlew clean bootJar`
* Run the stack with `docker-compose up`
* Go to `http://localhost:8080` and see it says "default". While the app is configured the way that it loads configuration from Consul, there's nothing there, so it sticks to the default value.
* Run `./consul-put-message1.sh` and refresh the page. Instead of "default" it should start saying "hello message one".
* Run `./consul-get-message.sh` to make sure that it's indeed what Consul has.
* Run `./consul-put-message2.sh` and refresh the page to see that it now says "hello message two".
* Run `./consul-delete-message.sh` and refresh the page. *This part is a little weird*, because the app keeps saying "hello message two".
* Also go to `http://localhost:8500/ui` for Consul's UI.

Relevant notes:

* `docker-compose.yml` runs a single Consul agent in development mode. In reality they run a few agents in server mode (to achieve HA) and then one client mode agent per physical host. In case of Docker they run one client agent container per Docker host.
