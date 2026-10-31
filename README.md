# SkyWatch SG

**Weather and Air Quality at a Glance**

An Android utility app developed for **CP3406 Mobile Computing —
Assessment 1** at James Cook University Singapore.

## Features

- Weather: temperature, feels-like temperature, humidity, wind and conditions.
- Regional 24-hour PSI and one-hour PM2.5 with separate classifications.
- Observation timestamps, independent refresh buttons and error handling.
- Persistent settings: region, °C/°F, and humidity/wind visibility.
- Link to official NEA haze guidance.

## Technology

Kotlin, Jetpack Compose, Material Design 3, ViewModel, Repository pattern,
manual dependency injection, Retrofit/Gson, coroutines and Preferences DataStore.

## Run

1. Open the project in Android Studio.
2. Install Android SDK 36 and sync Gradle.
3. Run on an emulator or device with **API 26+** and Internet access.

The project uses Gradle 8.13 and requests JetBrains Java 21.

## Data sources

- [Open-Meteo](https://open-meteo.com/en/docs): weather.
- [data.gov.sg PSI](https://api-open.data.gov.sg/v2/real-time/api/psi): 24-hour PSI.
- [data.gov.sg PM2.5](https://api-open.data.gov.sg/v2/real-time/api/pm25): one-hour PM2.5.
- [NEA haze guidance](https://www.haze.gov.sg/resources/advisory).

Weather uses fixed Singapore coordinates **1.3521, 103.8198**.
Region selection affects air quality only. Data refreshes at launch
or manually; check observation timestamps for freshness.

## Validation

Run on Windows:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Manual checks include API retrieval, offline recovery, region/unit
changes, display preferences and settings persistence.
API exceptions are logged under `SkyWatchNetwork` in Logcat.

## Remaining work

Verify and implement the planned condition-based activity advisory.
The official NEA guidance link is already available.

## Generative AI usage acknowledgment
I acknowledge using OpenAI ChatGPT/Codex (https://chatgpt.com/) for Kotlin and Jetpack Compose explanations, implementation guidance, debugging and documentation drafting for SkyWatch SG. Prompts included “Generate the code to display the dashboard UI” and “What steps are to take to implement persistent settings?” Generated code and documentation were incorporated into the project, with revisions during development and testing.
