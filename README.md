# Bus Time

Provides Android application.

## Building

You will need JDK 1.6 and Android SDK 22 installed.

1. Install required Android components.

  ```
  $ android update sdk --no-ui --force --all --filter build-tools-20.0.0
  $ android update sdk --no-ui --force --all --filter android-19
  $ android update sdk --no-ui --force --all --filter extra-android-m2repository
  $ android update sdk --no-ui --force --all --filter extra-google-m2repository
  ```

2. Set keys.

  ```
  $ cp src/main/res/xml/keys.template.xml src/main/res/values/keys.xml
  $ vi src/main/res/values/keys.xml
  ```

3. Set URLs.

  ```
  $ cp src/main/res/xml/urls.template.xml src/main/res/values/urls.xml
  $ vi src/main/res/values/urls.xml
  ```

4. Set database.

  ```
  $ mkdir src/main/assets
  $ cp bustime.db src/main/assets/bustime.db
  ```

5. Build application.

  ```
  $ ./gradlew clean assembleDebug
  ```
