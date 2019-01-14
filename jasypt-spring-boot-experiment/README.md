# jasypt-spring-boot-experiment

Illustrates integration between [Jasypt and Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot).

## Basic scenarios

Run `./gradlew clean bootRun` and see it fails:

```
Caused by: java.lang.IllegalStateException: Required Encryption configuration property missing: jasypt.encryptor.password
        at com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor.getRequiredProperty(DefaultLazyEncryptor.java:69) ~[jasypt-spring-boot-2.1.0.jar:na]
        at com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor.createDefault(DefaultLazyEncryptor.java:44) ~[jasypt-spring-boot-2.1.0.jar:na]
        at com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor.lambda$null$2(DefaultLazyEncryptor.java:33) ~[jasypt-spring-boot-2.1.0.jar:na]
```

Run `JASYPT_ENCRYPTOR_PASSWORD=123 ./gradlew clean bootRun` and see it fails:

```
Caused by: org.jasypt.exceptions.EncryptionOperationNotPossibleException: null
        at org.jasypt.encryption.pbe.StandardPBEByteEncryptor.decrypt(StandardPBEByteEncryptor.java:1055) ~[jasypt-1.9.2.jar:na]
        at org.jasypt.encryption.pbe.StandardPBEStringEncryptor.decrypt(StandardPBEStringEncryptor.java:725) ~[jasypt-1.9.2.jar:na]
```

Run `JASYPT_ENCRYPTOR_PASSWORD=password ./gradlew clean bootRun` and see it DOESN'T fail:

```
2019-01-13 22:07:41.482  INFO 18346 --- [           main] io.agibalov.App                          : username is andreya
2019-01-13 22:07:41.482  INFO 18346 --- [           main] io.agibalov.App                          : password is qwerty
```

## Keeping all the passwords in the repository

*Scenario: the team wants to store all the sensitive data in the repository and at the same time it wants to make sure that if someone gets access to the code, they won't be able to access the sensitive data. All team members put the master password to `~/.the-app/secrets.yaml` on their machines and Spring Boot looks up that password before it can decypher the sensitive data.*

Save the password to `~/.the-app/secrets.yaml` by running `./save-password.sh` and then run `./gradlew clean bootRun`:

```
2019-01-13 22:28:25.438  INFO 22204 --- [           main] io.agibalov.App                          : username is andreya
2019-01-13 22:28:25.438  INFO 22204 --- [           main] io.agibalov.App                          : password is qwerty
```

Delete the password from `~/.the-app/secrets.yaml` by running `./delete-password.sh` and then run `./gradlew clean bootRun`:

```
Caused by: java.lang.IllegalStateException: Required Encryption configuration property missing: jasypt.encryptor.password
        at com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor.getRequiredProperty(DefaultLazyEncryptor.java:69) ~[jasypt-spring-boot-2.1.0.jar:na]
        at com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor.createDefault(DefaultLazyEncryptor.java:44) ~[jasypt-spring-boot-2.1.0.jar:na]
```

All the same options apply to `./gradlew clean test` as well.
