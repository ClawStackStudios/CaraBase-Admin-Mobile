# Project Blueprint

## Core Identity

### Project Name / Title
CaraBase Admin Mobile

### Project Goal
To provide a fully operational, sovereign, and secure admin client dashboard for Android mobile devices. By enforcing strict epistemic boundaries and cryptographic token handling, the application ensures that CaraBase administrators can securely and remotely check their CaraBase Admin dashboard. It transforms the mobile device into a secure administration vault, operating with the same rigorous confidence as local LAN access.

### Project Idea
A stateless, zero-trust mobile command center for the CaraBase ecosystem. It securely bridges the gap between LAN-first DBaaS instances and remote administration by treating the Android device as an ephemeral, cryptographically secure terminal.

### Project Vision
To redefine mobile database administration by proving that convenience does not require compromising sovereignty. CaraBase Admin Mobile embodies the ClawStack Studios "Lobster Ethos" — armored against interception, dynamically transparent in its state, and absolutely uncompromising on security — placing total systemic control firmly in the hands of the SuperAdmin, anywhere in the world.

### Target Audience
"SuperAdmins," developers, and security-conscious database administrators who demand a robust, high-density, and portable interface to manage their self-hosted CaraBase ecosystems without relying on third-party cloud mediators.

---

## Technical Foundation

### Tech Stack & Framework
- **Language**: Kotlin
- **UI Framework**: Android Native, Jetpack Compose, Material 3
- **Networking**: Retrofit, OkHttp
- **Persistence**: SQLite (Room/Raw)
- **Build System**: Gradle (KTS)
- **Security**: Android Security Crypto Library (EncryptedSharedPreferences)

### Architecture
- **Structure**: Single-Activity, Single-Module Architecture (Feature-Modular compartmentalization).
- **Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern.
- **Cloud Readiness**: Designed for seamless Google AI Studio compatibility (flat structure, ZIP upload/download cycle safe).
- **Key Components**: 
  - `CaraBaseSystemRepository`: The central orchestrator for secure data flow.
  - `SecureIdentityVault`: The cryptographic anchor for session state.
  - `AuthInterceptor`: The rigorous tollgate ensuring all outbound connections are authorized.

### Guiding Principles
- **Context Optimization**: Hard 500-line limit per file. No monolithic files.
- **Micro-Service Layout**: Features must be compartmentalized into their own distinct directories.
- **Rigorous Cartography**: Map both sides of every bridge before crossing. Build the floor before the ceiling.
- **Security First**: Design features around security, not security around features.
- **Living Documentation**: Documentation must always reflect the exact current state of the codebase.

---

## Security & Accessibility

### Security Position
- **Compliance**: Default adherence to OWASP Top 10 standards.
- **Vaulting**: Tokens and endpoint configurations are stored cryptographically via Android's `EncryptedSharedPreferences`.
- **Network Boundaries**: Explicit epistemic boundaries between network logic and UI presentation. Outgoing requests are automatically secured by interceptors, preventing accidental token leakage.
- **Isolation**: Client-side only; no server-side runtime code in the main build path. The application trusts nothing but its own verified state.

### Accessibility Position
- **Visual Standards**: Strict adherence to WCAG AA contrast expectations using the ClawStack Slate & Cyber Accent theme.
- **Typography & Scale**: Minimum font size of 12px (xs) with clear hierarchy (Outfit/Inter for UI, JetBrains Mono for raw data precision).
- **Interaction Design**: Inputs enforce standard tap heights for touchscreen environments with clear visual focus states, reducing operational friction.
- **Information Density**: High-density UI layouts built to display complex, systemic metadata without overwhelming cognitive load.

---

## Constraints & Success

### Key Constraints
- **File Size limit**: Strict maximum of 500 lines per file (Non-negotiable).
- **UI Toolkit**: Jetpack Compose ONLY. No XML layouts, no View system, no Java interoperability.
- **Architecture Limits**: Must remain a Single-Activity, Single-Module architecture. AI Studio does not support multi-module dynamic feature modules.
- **Git Hygiene**: Build artifacts (`.build/`, `.gradle/`, `.idea/`, etc.) must never be tracked or pushed.

### Success Criteria
- **Operational Stability**: A fully self-contained Gradle build environment that consistently compiles and runs without external environment friction.
- **Systemic Transparency**: Flawless, real-time visibility into the CaraBase backend via a high-density, interactive mobile UI.
- **Network Integrity**: Secure, stateless network connection logic that perfectly handles unpredictable mobile network conditions while maintaining the cryptographic handshake.
- **Test Coverage**: All features and logic must be backed by full test suites explaining the *why* alongside the *how*, ensuring the architecture is as resilient as the code it validates.
