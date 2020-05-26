<img width="100" height="100" src="../icon/dtbazar-icon.png" alt="dtbazar-icon">

# DT Bazar Android [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](../LICENSE)
The DT Bazar **Android** is developed using **Native-Android** framework using **Kotlin** language.

* Latest [releases](https://github.com/Roaim/DTBazar/releases) can be found [here](https://github.com/Roaim/DTBazar/releases)

##### App Preview
![Android Preview](../preview/android/scs.png)

### Technology Stack
##### Language: `Kotlin`
##### Build Tool: `Gradle`
##### Framework: `Native Android`
##### Architecture: `MVVM`
* Jetpack Android
* Dagger2
* Coroutines
* LiveData
* ViewModel
* Room Database
* Material Design
* Navigation Component
* Paging Library
* Kapt processor
* Retrofit
* Glide
* Facebook Login
* Leakcanary

### Application Architecture
**MVVM** architecture with **Repository** pattern is followed. The inner layers don't know anything about the outer layers and the outer layers have reference to the immediate inner layers only.
![android-architecture](dtbazar-android-architecture.png)