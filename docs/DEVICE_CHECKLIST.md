# Parked beta smoke test

Record the APK SHA-256, Android version, firmware, SoC, resolution, orientation and previous DashHopper version. Keep a working launcher available.

1. On an existing developer-signed installation, verify a dock setting and a saved test note. Install the candidate APK as an update. Check the same dock setting and note afterward. Record the installer result and whether app data survived.
2. Test first launch on a separate clean test installation if available. Grant overlay access and enable the dock. Open Settings via the gear, return to the prior app and verify subsequent starts are silent.
3. Check all four dock edges, app strip scroll, drag, collapse and reveal. Verify popup bounds survive closing and reopening. Repeat in portrait if the unit supports it.
4. Grant Audio Mini notification access. Verify a supported live source's artwork and available playback controls. Stop iHeartRadio or another app that releases its session. Verify Last Played, Open Last Played app, and the return to live media metadata. Check that DashHopper sends no playback command on startup.
5. While parked, type and save a new Notepad note. Open and edit it, check Cancel, then delete the test note. Try keyboard dictation if offered by the installed keyboard.
6. Test Weather online, then offline with cached data. Test Calendar local browsing without permissions, then events with a test calendar if available.
7. Confirm the dock can launch a compatible external HVAC mini interface and the app's normal launch fallback works if supported. Test camera and call screen priority under controlled parked conditions.
8. Verify the dock and Last Played after ACC sleep/wake and a cold boot. Smoke-test overlay behavior over Android Auto and Waze while parked. Report Bluetooth behavior only if tested.

Report pass, fail or skipped for each item. Attach sanitized diagnostics only when useful.
