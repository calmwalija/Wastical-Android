## Wastical Android — app module

The `app` module is the main Android application for Wastical. It provides client and company experiences for account setup, payments, notifications, and related workflows. This document covers only the `app` module: setup, architecture, tech stack, build, testing, and operations.

### At a glance
- **Language**: Kotlin (JDK 17)
- **Min/Target/Compile SDK**: 27 / 35 / 35
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose
- **DI**: Hilt
- **Data**: Room, DataStore (Preferences)
- **Networking**: Retrofit + OkHttp (logging interceptor, Gson converter)
- **Background**: WorkManager (with Hilt WorkerFactory)
- **Auth/Identity**: Keycloak + Google Play Services Auth + Android AccountManager
- **Images**: Coil, Accompanist (drawable painter, system UI controller)
- **Paging**: Paging 3 (runtime + compose)
- **Notifications**: Firebase Cloud Messaging (FCM) + in-app channels
- **Analytics**: Firebase Analytics


## Project structure (selected)

```
app/src/main/java/net/techandgraphics/wastical/
  AppKlass.kt                  // @HiltAndroidApp + WorkManager Configuration.Provider
  AppUrl.kt, EnvConfig.kt      // Environment selection & URLs
  di/                          // Hilt modules (network, repositories, image cache, etc.)
  data/
    local/                     // Room database, DAOs, entities, DataStore
    remote/                    // Retrofit APIs, DTOs, mappers
  domain/                      // UI models + mappers
  ui/
    activity/MainActivity.kt   // Compose entry, theme, AppNavHost
    screen/                    // App, Auth, Client, Company screens (Compose)
    theme/                     // Compose theme
  worker/                      // WorkManager workers and custom WorkerFactory
  services/                    // FCM service and event models
  notification/                // Notification builder + channels
  account/                     // Android AccountManager authenticator
```


## Architecture

- **Presentation**: Jetpack Compose screens with ViewModels (state flows), navigation via `ui/screen/app/AppNavHost.kt` and route definitions in `ui/Route.kt`.
- **DI**: Hilt modules in `di/` provide Retrofit, OkHttp, Room, DataStore, repositories, and feature-specific services.
- **Data**:
  - Local persistence: Room (`data/local/database/AppDatabase.kt`) with DAOs and typed models.
  - Preferences: DataStore (`data/local/Preferences.kt`).
  - Remote APIs: Retrofit interfaces under `data/remote/**` (e.g., Account, Company, Payment, Notification, LastUpdated, Keycloak).
- **Domain**: Lightweight UI models and mappers in `domain/` to decouple DTOs/entities from UI.
- **Background work**: WorkManager with a custom `WorkerFactory` injected via `AppKlass` to support Hilt-injected workers (notifications, payments, account sync, etc.).
- **Accounts/Identity**: Integration with Android `AccountManager` (custom authenticator), Keycloak for tokens/JWT, and Google Sign-In.


## Environment configuration

The base URL is chosen at runtime based on the build type:

- `DEBUG` builds → `BuildConfig.DEV_API_DOMAIN`
- `RELEASE` builds → `BuildConfig.PROD_API_DOMAIN`

These come from `local.properties` and are exposed via BuildConfig fields in `app/build.gradle.kts`. They are consumed in `AppUrl.kt` and `di/NetworkModule.kt`.

1) Add the following to your project-level `local.properties` (quoted and with trailing slash, as required by Retrofit):

```
DEV_API_DOMAIN="https://dev.api.example.com/"
PROD_API_DOMAIN="https://api.example.com/"
```

Notes:
- The value must be quoted (as above) because it is written as a string literal into `BuildConfig`.
- The URL must end with `/` to satisfy Retrofit's `baseUrl(..)` requirement.

2) File URLs are derived as needed (e.g., `${apiDomain}file/`).


## Firebase setup

- `google-services.json` is checked in under `app/`. Ensure it matches your Firebase project for messaging and analytics.
- FCM service is registered in the manifest (`.services.FcmService`) and notifications channels are created at app startup (`notification/NotificationBuilder.kt`).
- On Android 13+ (`Tiramisu`), the app requests `POST_NOTIFICATIONS` permission at first launch.


## Build and run

Prerequisites:
- Android Studio (Koala+ recommended)
- Android SDK 35
- JDK 17

Android Studio:
- Open the project, select the `app` run configuration, and Run/Debug.

CLI (from project root):
```
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```


## Testing and code coverage

- Unit tests: `./gradlew :app:testDebugUnitTest`
- Instrumented tests: `./gradlew :app:connectedDebugAndroidTest`
- Coverage (JaCoCo report): `./gradlew :app:jacocoTestReport` → reports under `app/build/reports/jacoco/`
- Hilt test runner: `net.techandgraphics.wastical.HiltTestRunner` (already configured in `defaultConfig.testInstrumentationRunner`).


## Code style & quality

- Spotless (Kotlin/XML):
```
./gradlew :app:spotlessApply
./gradlew :app:spotlessCheck
```


## Notable integrations

- **DI Modules** (`di/`):
  - `NetworkModule.kt` / `NetworkApiModule.kt`: OkHttp (logging, auth), Retrofit (Gson), API providers.
  - `AppModule.kt`: Room, DataStore, and common singletons.
  - `RepositoryModule.kt`: Repository bindings.
  - `ImageCacheModule.kt`: Image loader configuration for Coil.
  - `KeycloakModule.kt`: Keycloak-related providers.

- **Networking** (`data/remote/**`):
  - APIs: Account, Company, Payment (methods, plans, collection), Notification, LastUpdated, Keycloak.
  - Parsing: Gson converter; errors mapped via `data/remote/MapApiError.kt`.

- **Persistence** (`data/local/**`):
  - Room database `AppDatabase` with DAOs and entity relations.
  - DataStore `Preferences` for lightweight key-value settings.

- **WorkManager** (`worker/**`):
  - Payment, notification, account session/sync workers.
  - Configured in `AppKlass` with custom `WorkerFactory` for Hilt injection.

- **UI** (`ui/**`):
  - Compose screens for auth, client, and company flows.
  - Navigation via `ui/screen/app/AppNavHost.kt` and routes in `ui/Route.kt`.
  - Theming via `ui/theme/` and M3 components.

- **Accounts & Auth** (`account/**`, `keycloak/**`):
  - Android `AccountManager` authenticator and helpers.
  - Keycloak API + JWT utilities.
  - Google Sign-In via Play Services.


## Android Manifest highlights

- Permissions: `INTERNET`, `POST_NOTIFICATIONS`, `MANAGE_ACCOUNTS`, `USE_CREDENTIALS`.
- Custom `Application`: `.AppKlass` (Hilt + WorkManager configuration + notification channels).
- `MainActivity` launches Compose UI; splash screen and edge-to-edge enabled.
- `FileProvider` declared for secure file sharing; paths configured under `res/xml/file_paths.xml`.
- `InitializationProvider` override removes default WorkManager initializer (using custom configuration in `AppKlass`).
- FCM service and SMS Retriever receiver for OTP flows.


## Common tasks (CLI)

```
# Lint/format
./gradlew :app:spotlessApply :app:spotlessCheck

# Unit tests and coverage
./gradlew :app:testDebugUnitTest :app:jacocoTestReport

# Build & install debug
./gradlew :app:assembleDebug :app:installDebug
```


## Troubleshooting

- BuildConfig URL fields are invalid
  - Ensure `DEV_API_DOMAIN` and `PROD_API_DOMAIN` in `local.properties` are QUOTED and end with `/`.

- Retrofit base URL exception
  - The base URL must end with `/`. See environment configuration section.

- FCM notifications not received
  - Verify `google-services.json` matches your Firebase project and the device has Google Play Services.
  - On Android 13+, ensure the app has `POST_NOTIFICATIONS` permission (requested on first launch).

- Room schema issues
  - Room schemas are output under `app/schemas/`. Clean/rebuild if schema mismatch arises.


## License

Internal project documentation for the Wastical `app` module.


