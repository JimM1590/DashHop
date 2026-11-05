# Third-party notices

## Android and build tools

DashHopper uses Android SDK APIs and platform components, including platform fonts, emoji, SQLite and `org.json`. It does not bundle the SDK, a third-party runtime JAR or native libraries. The Android Gradle Plugin, Gradle and SDK tools are build tools obtained separately. JSON-java 20240303 is fetched separately for host tests. Its upstream license applies to that dependency, not the project code. [JSON-java upstream](https://github.com/stleary/JSON-java/tree/20240303)

## Weather

Weather data by [Open-Meteo](https://open-meteo.com/) under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). DashHopper displays selected forecast values and converts units for display. The checked-in `tests/fixtures/openmeteo-example.json` is a saved Berlin API response reformatted as a test fixture. Its data are subject to the same attribution. Location search uses GeoNames data through Open-Meteo. Open-Meteo's free hosted API has separate noncommercial terms. Commercial use requires appropriately licensed API access. DashHopper makes HTTP requests to the service. It does not include Open-Meteo server code. [Data license and sources](https://open-meteo.com/en/licence) · [API terms](https://open-meteo.com/en/terms)

## Artwork and screenshots

The DashHopper frog artwork was created specifically for this project. The project owner authorizes distribution of the original artwork and its derivatives in the public source, APK resources and GitHub releases.

Third-party app names, icons, map surfaces and media imagery appearing in any screenshots belong to their respective owners. A screenshot does not license those assets under MIT or imply endorsement. Sample media is supplied by external apps at runtime.
