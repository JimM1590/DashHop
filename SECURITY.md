# Security and privacy

DashHop is experimental software for Android aftermarket head units.

## Reporting a security issue

Please avoid publishing exploitation steps, credentials, personal logs, or unique device identifiers in public issues. Use GitHub's private vulnerability reporting feature if enabled on this repository, or contact the maintainer privately through a channel they have published.

## Diagnostic data

Diagnostic logs may include an app-generated DashHop Device ID for report correlation. This identifier should be random and installation-scoped and should not be derived from a serial number, IMEI, MAC address, VIN or Android ID. Users should be able to view/reset it and opt in before sharing diagnostic logs.

Debug capture must avoid phone notifications, personal calendar details, GPS routes, tokens and passwords by default.

## Development safeguards

Never commit signing keys, passwords, private firmware dumps, local machine settings, personal logs, or third-party code without checking redistribution rights.

Keep stock reverse camera and call handling intact; the launcher must not claim to provide safety guarantees that Android permissions cannot enforce.
