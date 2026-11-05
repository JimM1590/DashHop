# Build DashHopper 2.1.2

The release revision uses application ID `com.jmadsen.dashhop`, versionName `2.1.2` and versionCode `20102`. Minimum Android API is 26, target is 30 and compile SDK is 35.

The verified command-line build uses a JDK (21 was tested), Android SDK Platform 35, Android Build Tools 35.0.0, Bash, Python 3 and zip. Set these paths to your SDK installation.

```sh
export ANDROID_JAR=/path/to/android-sdk/platforms/android-35/android.jar
export BUILD_TOOLS=/path/to/android-sdk/build-tools/35.0.0
bash build-local.sh
```

The result is `build/DashHopper_2.1.2-unsigned.apk`. It is aligned and requires signing to install. Local Android Studio/Gradle development is also configured for Android Gradle Plugin 8.7.3 and SDK 35. Use the Android Gradle Plugin's compatible Gradle 8.9 and JDK 17 baseline for that route. Gradle's ordinary debug signing uses your local developer key. A locally signed build with the same package cannot update the maintainer-signed installation.

The official APK keeps its existing signer. The maintainer signs release builds using a private key stored outside the public checkout. The public source build needs no private credentials. The release audit verified the unsigned build payload against the signed APK. Archive timestamps and signing data can differ in a rebuild.

## Host tests

Get `org.json:json:20240303` from Maven Central for the host tests only. The Android APK uses the platform's `org.json` implementation.

```sh
export JSON_TEST_JAR=/path/to/json-20240303.jar
export WEATHER_FIXTURE="$PWD/tests/fixtures/openmeteo-example.json"
bash tests/run-host-tests.sh
```

The fixture is bundled and the host suite requires no live weather request. The optional LiveWeatherSmoke class makes a real provider request when invoked directly. Host assertions cover policy, persistence and static Android wiring. Use the [device checklist](DEVICE_CHECKLIST.md) to record firmware-specific results.
