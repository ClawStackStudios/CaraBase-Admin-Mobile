# CaraBase Admin Mobile Security Invariants

This document outlines the security invariants enforced by the CaraBase Admin Mobile client. These invariants ensure a robust, resilient, and secure handshake between the mobile application and the CaraBase Engine.

## 1. Handshake Integrity (The "Double-Lock" Handshake)
- **Invariant**: A successful authentication MUST be immediately followed by a session verification call.
- **Reasoning**: To prevent "Ghost Sessions" where an auth token is granted but the network path (e.g., Cloudflare Tunnel) is unstable or the token is rejected by the system's middleware context.
- **Enforcement**: `CaraBaseSystemRepository#authenticate` calls `verifySession()` before returning success.

## 2. Stateless Volatility
- **Invariant**: The admin session token MUST NOT persist beyond its server-side TTL (20 minutes) or survival of the server process.
- **Enforcement**: 
    - Server: Sessions are kept in a volatile `Map`.
    - Client: `AuthInterceptor` detects `401 Unauthorized` and immediately purges the `SecureIdentityVault`.

## 3. Cryptographic Grounding
- **Invariant**: Plaintext Admin Tokens MUST NEVER be stored or transmitted.
- **Enforcement**: 
    - Transmission: Tokens are SHA-256 hashed on the client before being sent via HTTPS.
    - Storage: Only the resulting `x-admin-session` token is stored locally, anchored in `EncryptedSharedPreferences`.

## 4. Network Boundary Control
- **Invariant**: Cleartext traffic is STRICTLY FORBIDDEN for production domains.
- **Enforcement**: `network_security_config.xml` explicitly disables `cleartextTrafficPermitted` for the base configuration, only allowing it for specific local/emulator IP ranges.

## 5. DoS & Brute Force Resilience
- **Invariant**: The client MUST respect and propagate server-side rate-limiting events.
- **Enforcement**: `CaraBaseClient` integrity interceptor translates `429 Too Many Requests` into a high-signal exception to prevent automated hammering from a compromised or looping UI state.

---
*Security posture maintained by CrustAgent©™*
