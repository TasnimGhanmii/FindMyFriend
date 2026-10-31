# 📍 FindMyFriend

An Android app that lets a trusted contact retrieve your phone's location by sending it a **trigger message**. When the message arrives, the app replies with the device's position, **without anyone having to open the app or touch the phone**.

Typical situations: a friend running late and not answering, a lost or misplaced phone, a family member who needs to be found quickly, or a meet-up in an unfamiliar area.

> **Status:** Personal project · Early stage (see [Roadmap](#roadmap))

---

## Table of Contents

- [How It Works](#how-it-works)
- [Features](#features)
- [Privacy & Consent](#privacy--consent)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Permissions](#permissions)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Known Limitations](#known-limitations)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Author](#author)

---

## How It Works

```
 Friend's phone                         Your phone (FindMyFriend installed)
 ──────────────                         ───────────────────────────────────
 sends trigger message  ───────────►   background receiver detects the message
                                                │
                                                ▼
                                        validates the sender (allow-list)
                                        and the trigger keyword
                                                │
                                                ▼
                                        requests current location
                                                │
 receives location link  ◄───────────  replies automatically with the position
```

1. A trusted contact sends a predefined trigger message to your phone.
2. A background receiver inside the app picks the message up.
3. The app checks that the sender is authorized and that the message matches the trigger.
4. It fetches the device's current location.
5. It replies to the sender with the coordinates (for example as a map link).

## Features

- **Hands-free location sharing:** no interaction needed on the target phone
- **Trigger-based activation:** only a specific message starts the process
- **Automatic reply** containing the device location
- **Runs in the background** once set up
- **Lightweight native Android app** built with Java and Gradle

## Privacy & Consent

This app shares a person's location automatically, so it must be used responsibly. The project is designed around these principles:

- **Only install it on your own phone**, or on a phone whose owner has explicitly agreed.
- **Only authorized contacts** can trigger it: the app should ignore messages from any number that is not on your allow-list.
- **The trigger keyword is a secret** shared only with people you trust; change it if it leaks.
- **Location is sent only to the person who triggered the request**, never to a server or third party.
- **No location history is stored.**
- The user should always be able to see that the feature is active (persistent notification) and disable it instantly.

> ⚠️ Using this app to track someone without their knowledge or consent is unethical and illegal in many countries. Do not do it.

## Tech Stack

| Layer              | Technology                                           |
|--------------------|------------------------------------------------------|
| Platform           | Android                                              |
| Language           | Java                                                 |
| Build system       | Gradle (Kotlin DSL: `build.gradle.kts`)              |
| IDE                | Android Studio                                       |
| Location           | Android location services *(specify: Fused Location Provider / LocationManager)* |
| Messaging          | *(specify: SMS BroadcastReceiver / push / other)*    |

## Project Structure

```
FindMyFriend/
├── .idea/                  # Android Studio settings
├── app/                    # Android application module (source, resources, manifest)
├── gradle/                 # Gradle wrapper files and version catalog
├── build.gradle.kts        # Root build configuration
├── settings.gradle.kts     # Project and module settings
├── gradle.properties       # Gradle configuration
├── gradlew / gradlew.bat   # Gradle wrapper scripts
└── .gitignore
```

> Expand the `app/` section with your real packages (receiver, location, service, ui, ...) once finalized.

## Permissions

Declared in `AndroidManifest.xml` *(verify against your manifest)*:

| Permission                      | Why it is needed                                   |
|---------------------------------|----------------------------------------------------|
| `RECEIVE_SMS` / `READ_SMS`      | Detect the trigger message                         |
| `SEND_SMS`                      | Reply with the location                            |
| `ACCESS_FINE_LOCATION`          | Get the precise position                           |
| `ACCESS_BACKGROUND_LOCATION`    | Get the position while the app is closed           |
| `FOREGROUND_SERVICE`            | Keep the listener alive reliably                   |

Since Android 6, dangerous permissions must be granted by the user at runtime; the app should explain each one before asking.

## Getting Started

### Prerequisites

- **Android Studio** (latest stable)
- **JDK 17** (bundled with recent Android Studio versions)
- An Android device or emulator *(a real device with a SIM is needed to test SMS)*

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/TasnimGhanmii/FindMyFriend.git
   cd FindMyFriend
   ```

2. **Open the project** in Android Studio and let Gradle sync.

3. **Run the app**
   - Select a device and press **Run ▶**
   - or build from the command line:
     ```bash
     ./gradlew assembleDebug
     ```
   The APK is generated in `app/build/outputs/apk/debug/`.

## Usage

1. Install and open the app on the phone to be located.
2. Grant the requested permissions (messages and location).
3. Configure the trusted contacts and the trigger message *(describe your actual setup)*.
4. From a trusted phone, send the trigger message.
5. Receive the location reply within a few seconds.

## Known Limitations

- Battery-optimization settings on some manufacturers (Xiaomi, Huawei, Samsung, ...) can kill background listeners; the app may need to be excluded from battery optimization.
- Location accuracy depends on GPS signal and device settings; indoors it can be coarse.
- SMS sending may incur carrier charges.
- Google Play restricts SMS permissions, so distribution via the Play Store requires a permissions declaration or a different messaging approach.

## Roadmap

- [ ] Allow-list management screen for trusted contacts
- [ ] Persistent notification and one-tap disable
- [ ] Notify the owner each time their location is shared
- [ ] Configurable trigger keyword
- [ ] Optional confirmation mode (owner approves before reply)
- [ ] Rate limiting to prevent abuse
- [ ] Unit and instrumented tests
- [ ] Remove `.idea/` from version control

## Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the project
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push the branch: `git push origin feature/my-feature`
5. Open a Pull Request

## Author

**Tasnim Ghanmi**
GitHub: [@TasnimGhanmii](https://github.com/TasnimGhanmii)
