# DashHop

**An experimental Android automotive dock and configurable launcher for aftermarket head units.**

DashHop is designed to let drivers quickly access installed Android apps through a persistent, movable dock, with an optional configurable multi-window home experience.

> **Status: early public testing.** DashHop is not a finished universal launcher. Availability of multi-window app hosting and certain system integrations depends on the head unit, Android version, permissions, and firmware.

## Working in the current test environment

The following behaviors have been observed on the primary Android 11 test head unit; they are **not guaranteed on other devices**:

- Persistent, collapsible app dock with a draggable reveal control.
- Launching external applications, including the installed Android Auto host and TempTango.
- Switching between Android Auto and TempTango and returning to Android Auto.
- Configurable launcher panel layouts and native calendar content.
- Launching assigned apps full-screen when in-panel hosting is unavailable.

## In development / experimental

- **Dock refinement:** edge placement, custom appearance, shortcut selection, pinned Home and clock, and configurable auto-minimize (3-second default).
- **Home workspace:** user-selectable Main, Main + Side, and Main + Two layouts, including reversible secondary placement.
- **App windows:** user-preferred per-app panel assignments and genuine interactive in-panel hosting *only where supported*. At present, some apps appear as launch placeholders with full-screen fallback.
- **Widgets:** clock-first calendar and weather (Open-Meteo is being evaluated).
- **Safety behavior:** planned parked-only messages and driving-state handling. Current builds must **not** be relied upon to prevent distracted driving.
- **OEM interoperability:** preserve existing reverse-camera and phone-call priority. Future camera/call placement inside DashHop windows is experimental and may not be possible on ordinary Android installations.
- **Phone companion:** future Android-first notification/data sync and optional connectivity features; no automatic internet sharing is promised.

## Primary test environment

- Android 11 aftermarket head unit
- Qualcomm SM4250 / bengal platform
- 1280 × 720 landscape display
- XYAuto-style firmware
- Existing launcher retained as fallback

DashHop is **not specific to Jeep or TempTango**. Compatibility with other head units requires validation.

## Testing safely

Only configure, troubleshoot, record logs, or test window behavior **while parked safely**. Do not watch video or operate distracting applications while driving. The launcher cannot guarantee control over every external app or protected system window.

Keep your current working launcher installed until DashHop is stable on your hardware. Do not replace the default HOME app during initial compatibility testing.

## Getting test builds

Visit [GitHub Releases](../../releases). Not every development iteration will have a downloadable APK. Only install builds published through this repository or another verified official distribution channel.

Verify the provided SHA-256 hash for each release before installing. Preview releases may contain bugs, lose settings, or behave differently across head units.

## Reporting issues

Use [GitHub Issues](../../issues/new/choose), preferably the **Head-unit compatibility / bug report** form. Include your DashHop version, operating environment, panel layout, reproducing steps, and a sanitized log where useful.

Each DashHop diagnostic log should include a **persistent random DashHop Device ID** for correlating repeat reports from the same installation. This is an app-generated diagnostic identifier, **not** an IMEI, Android ID, serial number, MAC address, or VIN. It may still allow reports to be linked, so avoid sharing it publicly unless you choose to. Do not post call history, contacts, GPS traces, personal notifications, phone numbers, authentication tokens, or raw private logs.

A reported capability is not considered confirmed until tested on the relevant head unit.

## Project separation

**DashHop** is the automotive dock / launcher / window-management application.

**TempTango** is a separate HVAC application. DashHop must not require TempTango to work.

Both projects are developed within **JIM — Jim's Infotainment Management**, but have separate source, release cycles and issues.

## Licensing

Repository license: [MIT](LICENSE) for original material to which the repository owner can grant MIT rights. Any third-party code or assets included later must be reviewed for licensing compatibility and retain required notices. In particular, do not import GPL-covered Dashwheel implementation code into this repository without a deliberate licensing decision.

## Project priority

1. Reliable persistent dock
2. Configurable window layouts
3. App launching and compatible interactive windows
4. HOME launcher replacement
5. Camera/call interoperability
6. Phone notifications and connected features
7. Cross-device reliability, security and polish
