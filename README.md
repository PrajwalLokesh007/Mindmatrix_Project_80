# EcoHero 🌿 - Waste Management & Community Action App

**EcoHero** is a modern Android application built with Jetpack Compose that empowers citizens to take charge of their environment. Users can report waste, track community cleanup efforts, and earn rewards for their contributions to a cleaner city.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## ✨ Key Features

- **🚀 Instant Reporting**: Capture and report waste hotspots with photos, descriptions, and GPS locations.
- **🗺️ Community Map**: Real-time visualization of reported waste using Google Maps API.
- **🎮 Gamification**: Earn **Eco Score** points for every report and cleanup. Level up your rank from "Eco Beginner" to "Eco Hero".
- **🤝 Volunteer Integration**: Join local cleanup events and connect with other environmental activists.
- **📊 Real-time Dashboard**: Track your impact and see recent community reports at a glance.
- **🔐 Secure Auth**: Seamless login and data synchronization powered by Firebase.

---

## 🛠️ Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Language**: Kotlin
- **Backend**: [Firebase](https://firebase.google.com/)
    - Authentication
    - Firestore (NoSQL Database)
    - Cloud Storage (for images)
- **Maps**: [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview)
- **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/compose/)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Compose Navigation

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala or newer.
- JDK 11 or higher.
- A Firebase Project.
- A Google Cloud Project with Maps SDK enabled.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/PrajwalLokesh007/Mindmatrix_Project_80.git
   ```

2. **Firebase Setup**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `com.example.myapplication`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable **Anonymous Auth** (or Email/Google) and **Firestore**.

3. **Google Maps Setup**:
   - Get an API Key from the [Google Cloud Console](https://console.cloud.google.com/).
   - Add your API key to `local.properties`:
     ```properties
     MAPS_API_KEY=YOUR_API_KEY_HERE
     ```

4. **Build & Run**:
   - Open the project in Android Studio.
   - Sync Gradle and run the app on an emulator or physical device.

---

## 📸 Screenshots

| Dashboard | Reporting | Map View |
| :---: | :---: | :---: |
| ![Dashboard](https://via.placeholder.com/200x400?text=Dashboard) | ![Reporting](https://via.placeholder.com/200x400?text=Reporting) | ![Map](https://via.placeholder.com/200x400?text=Map) |

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 📬 Contact

**Prajwal Lokesh** - [@PrajwalLokesh007](https://github.com/PrajwalLokesh007)

Project Link: [https://github.com/PrajwalLokesh007/Mindmatrix_Project_80](https://github.com/PrajwalLokesh007/Mindmatrix_Project_80)

---
*Built with ❤️ for a cleaner planet.*
