# Bus Time

Provides Android application.

## Building

You will need JDK 1.6, Android SDK 22 and Gradle 1.10 installed.

1. Install required Android components.

  ```
  $ android update sdk --no-ui --force --all --filter build-tools-19.0.1
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
  $ gradle clean assembleDebug
  ```
