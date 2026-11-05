#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
: "${ANDROID_JAR:?Set ANDROID_JAR}"
: "${JSON_TEST_JAR:?Set JSON_TEST_JAR to JSON-java 20240303 (host tests only)}"
: "${WEATHER_FIXTURE:?Set WEATHER_FIXTURE to an Open-Meteo current/hourly/daily JSON response}"
mkdir -p build/host-tests
find tests/host-stubs tests/com -name '*.java' > build/test-sources.txt
for source in LastPlayedStore AudioSelectionPolicy AudioRestorePolicy DictationPolicy PopupBoundsPolicy DockArrows DockStartupPolicy DockUtilities DockRoutingPolicy HvacSelectionPolicy HvacLaunchReport HvacEndpointPolicy DockRecoveryPolicy DockDragPolicy WeatherIndicator WorkspacePolicy ShortcutRepository AutoCollapsePolicy DrivingStateProvider LaunchSafety DiagnosticIdentity DiagnosticEnvelope Diagnostics WeatherProvider PhoneWeatherProvider DirectOpenMeteoProvider WeatherData; do printf '%s\n' "app/src/main/java/com/jmadsen/dashhop/$source.java" >> build/test-sources.txt; done
java -m jdk.compiler/com.sun.tools.javac.Main -cp "$JSON_TEST_JAR:$ANDROID_JAR" -d build/host-tests @build/test-sources.txt
java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.RepositoryPolicyTest
java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.Increment03Test "$WEATHER_FIXTURE"

java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.Dock201Test



java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.DashHopperContractTest "$WEATHER_FIXTURE"

java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.HvacHookTest

java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.StartupUtilities202Test

python3 tests/check-settings-visibility.py

java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.Feature210Test

python3 tests/check-feature-wiring.py

python3 tests/check-keyboard-notes.py

java -cp "build/host-tests:$JSON_TEST_JAR:$ANDROID_JAR" com.jmadsen.dashhop.LastPlayedStoreTest
