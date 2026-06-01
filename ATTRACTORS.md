# Semantic Attractors & Project Grounding

This document defines the core semantic attractors that anchor all pattern inferences for the CaraBase Admin Mobile project. All future development must align with and be justified by these attractors.

## 1. Epistemic Boundaries
All data flow must respect strict epistemic boundaries. 
- **The UI Layer** must remain ignorant of network implementation details, focusing solely on the reactive representation of state.
- **The Repository Layer** acts as the epistemic gatekeeper, transforming raw network data into domain-specific results and managing the lifecycle of API configurations.

## 2. Stateless Security Invariant
The system relies on a stateless authentication model. 
- Authentication logic must transform user credentials into a stateless session token.
- This token is then injected into the networking pipeline via an automated interceptor, ensuring the security invariant is maintained across all requests without manual token management in the business logic.

## 3. Encrypted Local Grounding
Sensitive configuration and identity data must never exist in plaintext within the device's standard storage.
- All persistent secrets (tokens, endpoint URLs) must be anchored in the `SecureIdentityVault`, utilizing hardware-backed encryption (AES256_GCM).

## 4. Reactive State Pattern
The UI state is anchored in ViewModels using `StateFlow`.
- Pattern inference for UI changes must follow the unidirectional data flow (UDF) model.
- Loading, Error, and Success states must be explicitly managed within the `UiState` objects.

## 5. Centralized Networking Ground
All API interactions must be routed through the `CaraBaseClient` factory.
- This ensures consistency in timeout policies, logging, and interception.
- New API services must be added to the `CaraBaseApi` interface to maintain a single source of truth for backend interaction.

---
*Grounding enforced by CrustAgent©™*
