# Bus Time

Provides Android application.

## Building

You will need JDK 1.7+ installed.
Gradle, Android SDK and project dependencies will be downloaded in the process.

1. Set keys.

  ```
  $ cp src/main/res/xml/keys_template.xml src/main/res/values/keys.xml
  $ vi src/main/res/values/keys.xml
  ```

2. Set URLs.

  ```
  $ cp src/main/res/xml/urls_template.xml src/main/res/values/urls.xml
  $ vi src/main/res/values/urls.xml
  ```

3. Set database.

  ```
  $ mkdir src/main/assets
  $ cp bustime.db src/main/assets/bustime.db
  ```

5. Build application.

  ```
  $ ./gradlew clean assembleBetaDebug
  ```
