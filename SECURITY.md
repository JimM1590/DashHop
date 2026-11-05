# Security and privacy

Use GitHub's private vulnerability reporting if enabled, or contact the maintainer privately through a published channel. Public issues are suitable for ordinary compatibility reports. Remove identifiers, private location or route details, credentials and personal content before attaching logs or screenshots.

DashHopper stores notes, settings, Last Played metadata and artwork, weather cache and diagnostic records in app-private storage. Android backup is disabled in the manifest. DashHopper's note feature uses the installed keyboard, which may have its own dictation or sync policies. The Calendar utility uses Android's configured calendar provider, which may sync events through the account selected on the device.

Audio Mini requests Android notification access to enumerate compatible local media sessions. DashHopper's listener does not read notification text. Live media information and retained artwork are used for local display. The media diagnostics can record source package names.

Weather sends a city/postal search or selected coordinates to Open-Meteo over HTTPS. Licensed API mode also sends the user's key to the corresponding Open-Meteo endpoint. Android location permission is optional when the user enters a city or coordinates manually. The provider's retention policy is described in its [Terms & Privacy](https://open-meteo.com/en/terms).

Diagnostics rotate local files and export only when the user selects a destination. Logs can contain a persistent, random installation ID, timestamps, firmware details, package names and feature state. The ID is not a vehicle or hardware serial. Resetting dock preferences keeps it. Export is a manual support workflow. Review the output before sharing.

Keep developer signing keys, passwords, device logs, vendor firmware and private test data out of public commits and releases.
