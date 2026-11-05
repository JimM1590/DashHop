# Install and configure DashHopper

Install the signed APK from the approved GitHub release on a compatible aftermarket Android head unit. Compare its SHA-256 against the release before installing. If prompted, allow APK installation for the file manager or installer you selected.

For upgrades, install over the existing DashHopper app. The package ID is `com.jmadsen.dashhop`. Android checks the signature, and uninstalling or clearing app data removes local notes and settings. If Android reports an incompatible signature or refuses the update, keep the current installation and report the exact error.

Open DashHopper while parked, grant **Display over other apps**, and enable the dock in Settings. Open Settings again from the dock gear or its foreground notification.

| Access | When used |
| --- | --- |
| Display over other apps | Required for the persistent dock and floating toolboxes. |
| Foreground service and boot completion | Keep the enabled dock available and attempt recovery after boot or an app update, subject to firmware and your recovery settings. |
| Notification access | Optional. Audio Mini needs it to see and control eligible local media sessions. The listener uses Android's system-protected binding permission. |
| Internet and network state | Weather requests, city search and connectivity status. |
| Approximate/precise location | Optional. Used if you select device location for Weather. Manual city/coordinates work without it. |
| Calendar read and write | Optional. Read for events. Write to add events to a writable Android calendar. Local date browsing works without them. |

Notepad uses the selected keyboard. If it offers dictation, its own permissions and provider apply. DashHopper requests no microphone permission.

## Troubleshooting

- **Dock missing**. Check overlay permission and dock enabled status, expand the collapsed handle, and reopen DashHopper after boot if the firmware stopped the service.
- **Audio Mini has no live source**. Check notification access and start playback in a compatible Android app on the unit. Last Played is a saved record. Open the app to resume when the session is gone. There is no autoplay.
- **Weather stale**. Check the head unit's internet connection. Wireless Android Auto can affect Wi-Fi on some firmware. Weather labels cached data and depends on the configured Open-Meteo access. The free endpoint is for noncommercial use under [the provider's terms](https://open-meteo.com/en/terms).
- **Calendar events unavailable**. Check the relevant Android permissions and the installed calendar provider or synced account.
- **HVAC quick controls unavailable**. Select a compatible installed provider. DashHopper can try that application's normal launcher if quick controls are unavailable.

Test while safely parked and keep a working launcher installed. Android 8.0 is the declared minimum. Android 11 on a Qualcomm SM4250 unit at 1280 × 720 is the primary field test environment. Compatibility varies by firmware, display and app.
