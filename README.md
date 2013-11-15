# Bus Time

This repository contains the source code for the Bus Time Android app.

## Keys

Retrieve your own.

* [Google Maps API][Google Maps API link]
* [BugSense][BugSense link]

And apply them.

```console
$ cp src/main/res/xml/keys.template.xml src/main/res/values/keys.xml
$ vim src/main/res/values/keys.xml
```

## Building

The build requires JDK, Gradle and Android SDK (build-tools, SDK platform,
Android support repository, Google repository).

```console
$ gradle clean assembleRelease
```

  [BugSense link]: https://www.bugsense.com
  [Google Maps API link]: https://developers.google.com/maps/documentation/android/start
