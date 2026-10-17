# Azkari Revival (أذكاري — النسخة الأصلية المطوّرة)

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%E2%80%9336)-brightgreen.svg)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36%20(Android%2016)-blue.svg)](https://developer.android.com)
[![Build Tool](https://img.shields.io/badge/AGP-8.8.2-blueviolet.svg)](https://developer.android.com/studio/releases/gradle-plugin)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline-success.svg)]()
[![GitHub Release](https://img.shields.io/github/v/release/HeshamNoaman/Azkari-Revival?color=blue&logo=github)](https://github.com/HeshamNoaman/Azkari-Revival/releases/latest)
[![Download APK](https://img.shields.io/badge/Download-Release%20APK-success?logo=android)](https://github.com/HeshamNoaman/Azkari-Revival/releases/latest/download/Azkari-v1.7.apk)

**Azkari Revival (أذكاري)** is an open-source, privacy-first Islamic supplication and daily prayer companion application for Android (`com.dot.azkari`). 

Originally developed by **Dot Information Technology** (شركة دوت لتكنولوجيا المعلومات) and last updated on **June 28, 2014**, the application remained dormant for over a decade (~10 years) and was delisted from the Google Play Store and Apple App Store. This project resurrects the beloved original Azkari companion—modernized for **Android 8.0 (API 26) through Android 16 (API 36)**, with all Ramadan features completely unlocked, zero ads, zero tracking, and a 100% offline pure native Java architecture.

---

## 📥 Download & Install

You can download the production APK directly from the latest release:

- 📦 **[Download Azkari v1.7 Production APK](https://github.com/HeshamNoaman/Azkari-Revival/releases/latest/download/Azkari-v1.7.apk)** *(Direct APK Download, Signed & Verified)*
- 🏷️ **[View All GitHub Releases](https://github.com/HeshamNoaman/Azkari-Revival/releases)**

> **Installation Guide:** Since this APK is installed directly (sideloaded), Android may prompt you to *"Allow from this source"* in your browser or file manager. Azkari is 100% offline, requires **zero internet permissions**, and contains no telemetry or advertisements.

---

## 📜 Heritage & Revival

- **Original Creator Credit:** Sincere credit and appreciation to the original creators (**Dot Information Technology** / **Art Technologies**) who initially built the Azkari app.
- **A Decade of Dormancy:** The original project was last updated on **June 28, 2014** (v1.7) and was left unmaintained for over 10 years, leading to its eventual delisting from the Google Play Store and Apple App Store.
- **Brought Back to Life:** This project revives the application for today's devices:
  - **Modern OS Support:** Re-architected for contemporary Android releases up to **Android 16 (API 36)** with modern Notification Channels and exact alarm scheduling.
  - **Fully Unlocked Ramadan Tracker:** The Ramadan planner, checklists, and daily deed logs are accessible to everyone completely free with zero paywalls.
  - **Zero Telemetry & 100% Offline:** All legacy ad networks, trackers, and telemetry were completely removed, ensuring the app runs entirely offline and respects user privacy.

---

## 🔍 Looking for the Original Azkari App? (عن تطبيق أذكاري الأصلي)

If you arrived here searching for the original **Azkari (أذكاري)** Android application previously published on Google Play by **Dot Information Technology** (`com.dot.azkari`), you have found its modernized, open-source continuation.

| Information / البيان | Details / التفاصيل |
| :--- | :--- |
| **App Name (اسم التطبيق)** | **أذكاري — Azkari** |
| **Original Developer (المطور الأصلي)** | **Dot Information Technology** (دوت لتكنولوجيا المعلومات) / **Art Technologies** |
| **Application ID (معرّف الحزمة)** | `com.dot.azkari` *(Preserved for full compatibility)* |
| **Historical Release (آخر تحديث أصلي)** | Version 1.7 (June 28, 2014) |
| **Store Availability (حالة المتجر)** | Delisted from Google Play Store & Apple App Store after 2014 |
| **Revival License & Status (حالة المشروع)** | 100% Free & Open Source, 100% Offline, Zero Tracking, Zero Ads |

### 🔎 Common Search Queries & Keywords (كلمات البحث والدلالات)
* **بالعربية:** تحميل تطبيق أذكاري القديم الأصلي apk، برنامج أذكاري دوت انفورميشن تكنولوجي، تطبيق أذكاري النسخة الأصلية `com.dot.azkari`، أذكار الصباح والمساء بدون نت، أذكاري مهام رمضان كاملة بدون إعلانات وبدون اشتراك، مسبحة إلكترونية بدون إعلانات، حصن المسلم تطبيق أذكاري، تنزيل أذكاري للاندرويد الحديث.
* **In English:** Azkari Android app, Azkari apk download, com.dot.azkari apk, Dot Information Technology Azkari, original Azkari app, Azkari revival, offline Islamic prayer and supplication app, Daily Azkar without ads, Ramadan deed tracker apk.

---

## 🌟 Key Highlights & Architecture

- **Modern Android Architecture (minSdk 26, targetSdk 36):** Designed and built to support Android 8.0 Oreo through Android 16 (API 36).
- **100% Offline & Zero Telemetry:** Privacy-first design with zero network calls, zero tracking, and zero advertising. The application operates entirely locally and does not request or require the `INTERNET` permission.
- **Comprehensive Ramadan Tasks & Deed Tracker:** Built-in Ramadan planner and daily deed tracker backed by an offline SQLite database (`assets/RamadanTasks.db`), available free and offline for all users.
- **Modern Notification System:** Uses Android 8.0+ `NotificationChannel` (`azkari_reminders`), Android 13+ `POST_NOTIFICATIONS` runtime permission handling, and `FLAG_IMMUTABLE` PendingIntents for secure and exact reminder alarms.
- **Home Screen AppWidget:** Integrated home screen widget utilizing broadcast-based click dispatch, fully compliant with modern background execution limits.
- **Pure Native Java + XML:** Built entirely in clean, readable native Java with standard Android layout XML, without bloated third-party dependencies or runtime overhead.
- **Robust Test Coverage:** Includes unit tests covering cron reminder prediction, time conversion logic, Arabic text reshaping, and font size parsing.

---

## 📱 Features

1. **Daily Azkar & Supplications (أذكار المسلم اليومية):**
   - Morning & Evening Azkar (أذكار الصباح والمساء)
   - Sleep & Wakeup Supplications (أذكار النوم والاستيقاظ)
   - Post-Prayer Azkar (أذكار بعد الصلاة)
   - Food, Travel, and Daily Occasions Duas
2. **Digital Tasbeeh Counter (المسبحة الإلكترونية):**
   - Interactive tap counter with haptic vibration feedback.
   - Multiple preset Tasbeeh phrases with reset and loop counters.
3. **Ramadan Tasks & Deed Tracker (مهام رمضان):**
   - Daily Ramadan checklist and spiritual goal tracker categorized by day and category.
   - Pre-populated SQLite database with offline progress persistence.
4. **Customizable Scheduled Reminders:**
   - Accurate reminder notifications using exact alarms.
   - Configurable alert sound, vibration, and repetition schedules.
5. **Home Screen Widget:**
   - Quick access to daily Azkar directly from your Android launcher.
6. **Arabic Typography & Readability:**
   - Embedded traditional Arabic fonts with dynamic text resizing support for comfortable reading.

---

## 📁 Project Structure

```
Azkari-Revival/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml      # Native Android manifest with API 36 configuration
│   │   │   ├── assets/
│   │   │   │   ├── fonts/               # Embedded Arabic typography
│   │   │   │   └── RamadanTasks.db      # SQLite database for Ramadan tracker
│   │   │   ├── java/
│   │   │   │   ├── com/dot/azkari/      # Main application activities and UI logic
│   │   │   │   ├── com/buzzbox/         # Scheduler and cron prediction engine
│   │   │   │   └── com/dot/arabic/      # Arabic string shaping and formatting utilities
│   │   │   └── res/                     # Layouts, drawables, values, and localized strings
│   │   └── test/
│   │       └── java/com/dot/azkari/     # Unit test suite (AzkariLogicTest.java)
│   ├── build.gradle                     # App-level build config (compileSdk 36, Java 17)
│   └── proguard-rules.pro               # ProGuard / R8 preservation rules
├── gradle/
│   └── wrapper/                         # Gradle 8.10.2 wrapper binaries and configuration
├── .gitignore                           # Android Studio & Gradle exclusions
├── build.gradle                         # Root Gradle script (AGP 8.8.2)
├── gradle.properties                    # JVM arguments and AndroidX flags
├── gradlew / gradlew.bat                # Gradle wrapper launch scripts
├── settings.gradle                      # Root project definition ('Azkari-Revival')
└── README.md                            # Project documentation
```

---

## 🛠️ Building the Project

### Prerequisites

- **Java Development Kit (JDK):** OpenJDK 17 or higher
- **Android SDK:** Installed with `platforms;android-36` and `build-tools;35.0.0` or higher
- Set environment variables (if building via CLI):
  ```powershell
  $env:ANDROID_HOME = "C:\Users\<user>\AppData\Local\Android\Sdk"
  $env:ANDROID_SDK_ROOT = "C:\Users\<user>\AppData\Local\Android\Sdk"
  ```

### Build Commands

Run the following commands using the Gradle wrapper from the project root:

```powershell
# 1. Compile Java source code
.\gradlew.bat :app:compileDebugJavaWithJavac

# 2. Run unit tests
.\gradlew.bat test

# 3. Assemble Debug APK
.\gradlew.bat :app:assembleDebug

# 4. Assemble Release APK
.\gradlew.bat :app:assembleRelease
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Testing

Execute the comprehensive unit test suite:

```powershell
.\gradlew.bat testDebugUnitTest
```

All unit tests verify core business logic:
- Exact 12h/24h time format conversions
- Cron alarm scheduling prediction calculations
- Arabic typography reshaping
- Resilient font size preference parsing
