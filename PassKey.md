  # PassKey Authentication Patterns

## Pattern 1: Okta owns WebAuthn (recommended for enterprise)

Your app delegates authentication entirely to Okta via OIDC/SAML. Okta handles the passkey ceremony directly.

```
User → Your App → Redirect to Okta login page
                       ↓
                  Okta runs WebAuthn ceremony in browser
                       ↓
                  navigator.credentials.create() / .get()
                       ↓
                  OS authenticator (Touch ID / Windows Hello)
                       ↓
                  Okta validates & stores public key
                       ↓
                  Okta issues OIDC id_token / SAML assertion
                       ↓
               Redirect back to Your App with token
                       ↓
               Your App validates token, creates session
```

**Where public key is stored:** In Okta's cloud infrastructure, tied to the user's Okta profile. Okta manages the credential lifecycle (enrollment, revocation, recovery).

**Your app stores:** Nothing WebAuthn-related. You only receive and validate OIDC tokens/SAML assertions. Your app doesn't see the public key at all.

**What changes in your code:**
- Remove all `/webauthn/*` endpoints, controllers, and WebAuthn4J dependency
- Replace with Spring Security OAuth2 Client (`spring-boot-starter-oauth2-client`)
- Configure Okta as the OIDC provider
- The `/hello` endpoint reads the username from the OIDC token's claims

```
Your App                          Okta                        Device
┌─────────┐                 ┌──────────────┐           ┌──────────────┐
│ No keys │                 │ Public key   │           │ Private key  │
│ stored  │                 │ stored per   │           │ in OS        │
│         │                 │ user in Okta │           │ Keychain/    │
│ Only    │  ←── OIDC ───→  │ Universal    │←WebAuthn→ │ TPM/TEE      │
│ tokens  │     tokens      │ Directory    │           │              │
└─────────┘                 └──────────────┘           └──────────────┘
```

---

## Pattern 2: Your app owns WebAuthn, Okta owns identity

Your app runs the WebAuthn ceremony itself (like the current demo), but uses Okta as the user directory/identity source. Okta provisions users; your app manages passkeys.

```
User → Your App login page
            ↓
       Your App runs WebAuthn ceremony (current code)
            ↓
       navigator.credentials.create() / .get()
            ↓
       Your App validates & stores public key
            ↓
       Your App verifies user exists in Okta (via SCIM/API)
            ↓
       Session created
```

**Where public key is stored:** In your app's database (replacing the current `InMemoryWebAuthnCredentialRecordManager` with a persistent store like PostgreSQL, DynamoDB, etc.).

**What changes in your code:**
- Replace `InMemoryWebAuthnCredentialRecordManager` with a DB-backed implementation
- Add Okta user provisioning (SCIM) or Okta SDK to verify user identity
- Registration flow: user authenticates via Okta first, then enrolls passkey in your app
- Login flow: passkey validated by your app, user identity cross-referenced with Okta

```
Your App                          Okta                        Device
┌─────────────┐             ┌──────────────┐           ┌──────────────┐
│ Public key  │             │ User         │           │ Private key  │
│ stored in   │             │ directory    │           │ in OS        │
│ YOUR DB     │  ←── SCIM/  │ (identity    │           │ Keychain/    │
│ (PostgreSQL,│     API ──→ │  source)     │No WebAuthn│ TPM/TEE      │
│  DynamoDB)  │             │              │ with Okta │              │
└─────────────┘             └──────────────┘           └──────────────┘
```

---

## Pattern 3: Hybrid — Okta + synced passkeys

If your users use synced passkeys (iCloud Keychain, Google Password Manager), the private key is synced across devices via the cloud provider. Okta still stores the public key.

```
                                                    Apple/Google Cloud
                                                   ┌─────────────────┐
                                                   │ Private key     │
Device A (MacBook)                                 │ synced via      │
┌──────────────┐          Okta                     │ iCloud Keychain │
│ Private key  │    ┌──────────────┐               │ or Google       │
│ (cached      │←──→│ Public key   │               │ Password Mgr    │
│  locally)    │    │ stored in    │               └────────┬────────┘
└──────────────┘    │ Okta per user│                        │
                    └──────────────┘                        │
Device B (iPhone)                                           │
┌──────────────┐                                            │
│ Private key  │←───────────────────────────────────────────┘
│ (synced from │     Same passkey works on both devices
│  cloud)      │     without re-registering
└──────────────┘
```

---

## Which pattern should you use?

| Factor | Pattern 1 (Okta owns WebAuthn) | Pattern 2 (App owns WebAuthn) |
|--------|--------------------------------|-------------------------------|
| **Public key location** | Okta's cloud | Your database |
| **Complexity for your app** | Low — just OIDC integration | High — full WebAuthn stack |
| **User management** | Okta admin console | You build it |
| **MFA/policy enforcement** | Okta handles it | You implement it |
| **Credential revocation** | Okta admin revokes | You build revocation |
| **Compliance/audit** | Okta provides audit logs | You build audit logging |
| **Offline support** | No (needs Okta) | Yes (your server validates) |
| **Multi-app SSO** | Yes — one passkey for all Okta apps | No — per-app passkeys |