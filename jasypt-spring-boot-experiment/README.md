# jasypt-spring-boot-experiment

Illustrates integration between Jasypt and Spring Boot.

1. Download [Jasypt command line utility](http://www.jasypt.org/index.html)
2. Encrypt your password with master password: `./encrypt input="preved" password="s3cr3t"`. The output will contain an encrypted version of "preved": "7ZFDMCgFdeQY9PlaiJgFyQ==" (not guaranteed to be exactly this text).
3. Update `application.properties`: `password=ENC(7ZFDMCgFdeQY9PlaiJgFyQ==)`
4. Build the app using `./gradlew bootRepackage`
5. Run the app using `java -jar jasypt-spring-boot-experiment-1.0-SNAPSHOT.jar --jasypt.encryptor.password=s3cr3t`
6. Run the app with any other encryptor password and see it fails

Note that `jasypt.encryptor.password` is 100% Spring Boot-compliant, so there's a million ways to supply it to the app.

Also see: https://github.com/ulisesbocchio/jasypt-spring-boot
