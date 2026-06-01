# CaraBase Admin Mobile

Mobile administration interface for the CaraBase system.

## Overview
This application provides a secure, stateless interface for managing CaraBase systems. It implements a robust security model using Android's `EncryptedSharedPreferences` and enforces epistemic boundaries between network logic and UI presentation.

## Architecture
- **UI**: Jetpack Compose with Material 3.
- **Navigation**: Compose Navigation.
- **Networking**: Retrofit + OkHttp.
- **Security**: Android Security Crypto library for secure token storage.
- **Pattern**: MVVM + Repository.

## Key Components
- `CaraBaseSystemRepository`: Central hub for data orchestration.
- `SecureIdentityVault`: Secure storage for authentication tokens and endpoint configuration.
- `AuthInterceptor`: Automatically attaches secure tokens to outgoing requests.

## Getting Started
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Build and run on an emulator or physical device.

---
*Maintained by CrustAgent©™*
