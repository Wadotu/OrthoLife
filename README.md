[README.md](https://github.com/user-attachments/files/28794703/README.md)
# OrthoLife: The Orthodox Liturgical Companion

[![Android API](https://img.shields.io/badge/API-31%2B-brightgreen.svg)](https://android-arsenal.com/api?level=31)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-blue.svg)](https://developer.android.com)

**OrthoLife** is a comprehensive spiritual tool designed for Orthodox Christians. It provides a real-time liturgical calendar, daily scripture readings, fasting guidelines, and traditional prayer resources in a modern, localized Android environment.

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_round.png" width="120" alt="OrthoLife Logo">
</p>

---

## 🌟 Key Features

### 📅 Liturgical Calendar
*   **Dual Calendar Support**: Toggle between Revised Julian (New) and Julian (Old) calendars.
*   **Detailed Feast Tracking**: Identification of Great Feasts, saints of the day, and liturgical commemorations.
*   **Dynamic Fasting Indicators**: Real-time fasting status (Fast Day, Fish Allowed, Wine & Oil, etc.) based on the liturgical season.

### 📖 Daily Scripture Readings
*   **Liturgical Order**: Automatically fetches and displays daily readings (Epistle, Gospel, Acts).
*   **Traditional Priority**: Readings are sorted according to the Orthodox Divine Liturgy tradition (Epistle first, then Gospel).
*   **Multilingual Support**: Scripture names are localized (e.g., "Romans" becomes "로마서" in Korean).

### 🛠 Home Screen Widget
*   **Real-time Updates**: Displays the current date, feast, and fasting info directly on the home screen.
*   **Manual Refresh**: Integrated manual refresh button using **WorkManager** to ensure data accuracy.
*   **Process Stability**: Implements `Expedited Work` and `goAsync` to guarantee updates even if the app is closed.

### ☦️ Spiritual Resources
*   **Prayer Rope (Komboskini)**: A digital haptic-feedback prayer rope for the Jesus Prayer.
*   **Prayer Book**: A collection of essential Orthodox prayers.
*   **Holy Icons**: A curated gallery of icons for veneration and meditation.

---

## 🛠 Tech Stack

*   **Language**: Java (JDK 11)
*   **Network**: [Retrofit 2](https://square.github.io/retrofit/) & GSON for REST API integration ([OrthoCal API](https://orthocal.info/)).
*   **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for reliable background synchronization and widget updates.
*   **UI/UX**: 
    *   Customized Material Design components.
    *   **Edge-to-Edge** display support for modern immersive experiences.
    *   Predictive Back gesture integration.
*   **Architecture**: Singleton pattern for network clients and optimized BroadcastReceivers for widget communication.

---

## 🌍 Localization

The app supports multiple languages used in the Orthodox world:
*   **Korean (한국어)**: Fully localized with traditional Orthodox terminology.
*   **English**: Default global support.
*   **Greek (Ελληνικά)**: Liturgical terms and calendar names.
*   **Church Slavonic (Церковнославянский)**: Supporting the Slavic tradition.

---

## 🏗 Engineering Challenges & Solutions

### 1. Android 16 (SDK 37) Migration
The app was recently migrated to target **Android 16 (API 37)**. This involved:
*   Implementing **Predictive Back** support by migrating from `onBackPressed()` to `OnBackPressedCallback`.
*   Ensuring **Edge-to-Edge** compliance by handling system bar insets dynamically using `ViewCompat.setOnApplyWindowInsetsListener`.

### 2. Reliable Widget Synchronization
Traditional `AppWidgetProvider` updates are often delayed by the system's battery optimization.
*   **Solution**: Integrated **WorkManager with Expedited Policy**. This ensures that when a user clicks the "Refresh" button on the widget, a high-priority background job is enqueued, providing near-instant visual feedback even if the main process is killed.

### 3. Liturgical Logic Sorting
The API provides scripture readings in an arbitrary order.
*   **Solution**: Implemented a custom sorting algorithm based on liturgical priority (`Epistle` -> `Apostle` -> `Acts` -> `Gospel`) to mirror the sequence of the Divine Liturgy.

---

## 🚀 Getting Started

1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/OrthoLife.git
    ```
2.  Open the project in **Android Studio Ladybug (2024.2.1) or higher**.
3.  Ensure you have **SDK 37** installed via the SDK Manager.
4.  Build and run the app on an emulator or physical device.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

### 👨‍💻 Author
**Wadotu Applications** - *Make modern solutions for spiritual traditions.*
---
*Disclaimer: Liturgical data is provided by the OrthoCal API. Users should consult their local parish calendar for specific jurisdictional variations.*
