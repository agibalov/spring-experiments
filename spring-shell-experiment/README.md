# spring-shell-experiment

Run `./gradlew clean tool` to build the `tool` CLI app and put it into root of this project.

* `./tool` for interactive shell.
* `./tool help` for help.
* `./tool help add` for help for `add` command.
* `./tool add --a 2 --b 3` to add 2 numbers.
* `./tool add --a -1 --b 3` to see how validation works.
* `./tool say --what hello` to say hello. (**BUG:** `./tool say --what "hello world"` won't work - need to take a look at `me.loki2302.App.MyApplicationRunner`)
