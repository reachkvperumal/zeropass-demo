# zeropass-demo
Spring Boot Demo - ZeroPass with Passkey &amp; WebAuthn

## Enterprise Integration

When integrating passkeys into an enterprise environment, there are two options:

1. **IDP owns WebAuthn** — Delegate the passkey ceremony entirely to your Identity Provider (e.g. Okta, Azure AD). Your app only consumes OIDC/SAML tokens. The public key is stored in the IDP's cloud. Best for SSO, centralized policy, and compliance.

2. **App owns WebAuthn** — Your app runs the passkey ceremony itself and stores the public key in its own database. The IDP serves as the user directory. Best when you need offline support or full control over the credential lifecycle.

See [PassKey.md](PassKey.md) for detailed integration guidance.
