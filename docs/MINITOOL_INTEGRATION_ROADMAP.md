# Future MiniTool integrations — proposed public hooks

> **Roadmap / design proposal — not implemented in DashHopper 2.1.2.**
>
> This document describes a possible future developer contract. Names, actions, metadata, IPC definitions and supported behavior are subject to change until a separately tested API specification is released. No developer should rely on the proposed generic action in a production build yet.

## Goal

Let other Android developers offer small tools that users can discover and launch from DashHopper's configurable dock, without hardcoding vendor packages, copying their business logic or requiring the app to become a full launcher/window manager.

Two integration models are being considered:

1. **Provider-owned Mini Activity** — DashHopper finds and explicitly launches an exported Activity. The provider owns its window, permissions, functions and closing behavior. This is the model already field-tested with the existing HVAC quick-control contract.
2. **Provider-fed DashHopper Mini** — DashHopper renders a native floating window using data obtained through a documented, authorized API of another app. Adapters may be provider-specific. This is a separate research track, not a generic data API available today.

Neither model grants a MiniTool access to DashHopper's private application data or vehicle communications.

## Existing HVAC quick-control integration (available)

The established action is:

```text
org.dashhop.intent.action.HVAC_QUICK_CONTROL
```

A compatible HVAC application can advertise an **enabled, exported, non-launcher Activity** that handles this action with `android.intent.category.DEFAULT`. DashHopper discovers the compatible Activity in the selected provider package, resolves an explicit launch target, and launches that provider-owned interface.

The external app remains independently installable and owns HVAC data, controls, and its vehicle backend. DashHopper does not implement HVAC or CANBUS communication. The contract was tested with a separate frog-test application and TempTango Mini on the development head unit. Device-specific UI behavior cannot be guaranteed universally.

**Backward compatibility:** A future generic MiniTool hook must not rename or remove the working HVAC action.

## Proposed generic provider-owned Mini Activity

A possible future discoverable action is:

```text
org.dashhop.intent.action.MINITOOL_OPEN
```

An application could declare one or more compatible Activities. Early design ideas include metadata for an integration API version, human-readable category/type, and optional features. For example:

```xml
<activity
    android:name=".ExampleMiniActivity"
    android:exported="true"
    android:enabled="true"
    android:excludeFromRecents="true">
    <intent-filter>
        <action android:name="org.dashhop.intent.action.MINITOOL_OPEN" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
    <meta-data android:name="org.dashhop.MINITOOL_API_VERSION" android:value="1" />
    <meta-data android:name="org.dashhop.MINITOOL_TYPE" android:value="com.example.tool.status" />
</activity>
```

**Illustrative only.** No generic action, metadata schema, SDK, compatibility promise or registration mechanism has been shipped.

Design goals for discovery and launching:

- Use Android PackageManager, targeted package-visibility queries, and the caller's selected provider rather than hardcoded vendor names.
- Check that an Activity is available, enabled and exported; explicitly resolve and launch it.
- Let the user choose, enable, disable and reorder eligible MiniTools in Settings.
- Handle missing, disabled, replaced and uninstalled providers gracefully.
- Preserve existing dock state and the underlying application whenever Android permits.
- Treat external Intent extras as untrusted input; avoid exchanging personal information or private identifiers without a clear need and user authorization.

Window presentation stays with the provider. For consistency we recommend compact layouts, an upper-right Close button and predictable Back behavior. **An Android Activity is not automatically movable or resizable.** Those behaviors are the external app's responsibility.

## Future data-provider Mini model

Some tools are better rendered by DashHopper itself, while an external app supplies the data.

A proposed example is **OBD Mini**: DashHopper would display configurable gauges and diagnostic trouble codes obtained from compatible third-party diagnostic applications via their documented interfaces. The diagnostic app—not DashHopper—would own the adapter connection, PID polling, protocol handling and vehicle compatibility. The intended DashHopper view is **read-only**: no clearing codes, ECU programming, raw OBD commands or vehicle CANBUS access.

Because APIs differ by provider, initial support could require separate adapters/connectors; installation of an OBD app alone is not evidence that it exposes an external telemetry API. A common future protocol may include capability discovery, values/units, timestamps, connection status, fault codes when exposed, stale-data handling and bounded update rates.

This model will require its own security review (IPC permissions, authentication and data trust boundaries) and licensing assessment. Nothing in this document promises a universal OBD integration today.

## Developer experience and future milestones

Before inviting third-party developers to adopt a public generic hook, the project intends to:

1. Finalize and version the external-Activity Intent/metadata contract independently of the app version.
2. Build an independent sample MiniTool APK, exercising discovery, selection, launching, dismissal and uninstall/reinstall.
3. Verify behavior with Android 11 package visibility, overlay/Activity restrictions, and multiple installed providers.
4. Publish a small SDK/reference manifest and security guidance only after the contract stabilizes.
5. Separately prototype a simulated data-provider connector before considering live OBD or other telemetry support.
6. Preserve the existing HVAC integration and run regression tests throughout.

## Not part of the current beta

The 2.1.2 beta candidate offers its existing HVAC quick-control integration and native Weather, Calendar, Audio Mini and Notepad Mini toolboxes. It **does not** offer the proposed generic MiniTool API, an OBD Mini, universal third-party window embedding, full HOME launcher replacement, or automatic compatibility with arbitrary installed apps.

See the [main README](../README.md) for the currently documented feature set and beta status.
