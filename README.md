# GratisFare

GratisFare is a comprehensive ride-sharing application designed to provide seamless and efficient transportation services. It includes features for user registration, login, ride request, driver tracking, and role-based access for drivers and riders.

## Features

- User Registration and Login
- Password Reset
- Role-Based Access Control
- Ride Request
- Driver Matching and Tracking
- Integration with Firebase Authentication and Firestore
- Animations and Transitions for Enhanced User Experience

## Tech Stack

- Kotlin
- Android Jetpack (ViewModel, LiveData, Data Binding)
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Retrofit
- Onfido SDK for KYC Verification
- Google Maps API

## Setup and Installation

### Prerequisites

- Android Studio Arctic Fox or later
- Gradle 7.0+
- A Firebase project with Authentication and Firestore enabled
- Google Maps API Key
- Onfido SDK Key

### Installation

1. **Clone the Repository**

    ```sh
    git clone https://github.com/your-username/GratisFare.git
    cd GratisFare
    ```

2. **Open the Project in Android Studio**

    - Open Android Studio.
    - Select `Open an existing project`.
    - Navigate to the cloned repository and select the `GratisFare` folder.

3. **Set Up Firebase**

    - Add your `google-services.json` file to the `app` directory.
    - Ensure your Firebase project has Authentication and Firestore enabled.

4. **Configure Google Maps**

    - Replace `YOUR_API_KEY_HERE` in `AndroidManifest.xml` with your actual Google Maps API key.

    ```xml
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_API_KEY_HERE" />
    ```

5. **Configure Onfido SDK**

    - Replace `YOUR_ONFIDO_SDK_TOKEN` and `YOUR_APPLICANT_ID` with your actual Onfido SDK token and applicant ID in `RegistrationActivity`.

### Dependencies

Ensure the following dependencies are included in your `build.gradle` files:

#### `build.gradle (Project)`

```groovy
buildscript {
    val agp_version by extra("8.3.2")
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:$agp_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    }
}
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
