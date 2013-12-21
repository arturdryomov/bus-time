# Bus Time

Provides Android application.

## Building

You will need JDK 1.6, Android SDK 22 and Gradle 1.8 installed.

1. Install required Android components.

  ```
  $ android update sdk --no-ui --force --all --filter build-tools-19.0.0
  $ android update sdk --no-ui --force --all --filter android-19
  $ android update sdk --no-ui --force --all --filter extra-android-m2repository
  $ android update sdk --no-ui --force --all --filter extra-google-m2repository
  ```

2. Set API keys.

  ```
  $ cp src/main/res/xml/keys.template.xml src/main/res/values/keys.xml
  $ vi src/main/res/values/keys.xml
  ```

3. Build application.

  ```
  $ gradle clean assembleDebug
  ```
