// Safari/WebKit can be stricter about WebAuthn option shapes than Chromium.
// This shim normalizes server-provided JSON into a PublicKeyCredentialCreationOptions
// object that WebKit accepts.
//
// It is intentionally defensive:
// - challenge may be a string OR { value: "..." }
// - user.id may be base64 or base64url
// - challenge may be base64 or base64url
// - pubKeyCredParams must be an array

(function (global) {
  function base64UrlToUint8Array(base64Url) {
    const padding = '='.repeat((4 - (base64Url.length % 4)) % 4);
    const base64 = (base64Url + padding).replace(/-/g, '+').replace(/_/g, '/');
    return base64ToUint8Array(base64);
  }

  function base64ToUint8Array(base64) {
    const raw = atob(base64);
    const out = new Uint8Array(raw.length);
    for (let i = 0; i < raw.length; i++) out[i] = raw.charCodeAt(i);
    return out;
  }

  function decodeMaybeBase64(s) {
    if (!s || typeof s !== 'string') return null;
    const isBase64Url = s.includes('-') || s.includes('_');
    return isBase64Url ? base64UrlToUint8Array(s) : base64ToUint8Array(s);
  }

  function pickStringOrValue(o) {
    if (!o) return null;
    if (typeof o === 'string') return o;
    if (typeof o === 'object' && typeof o.value === 'string') return o.value;
    return null;
  }

  function normalizeCreationOptions(opts) {
    const normalized = Object.assign({}, opts);

    // Required fields
    normalized.rp = normalized.rp || { id: 'localhost', name: 'zero pass demo' };

    // pubKeyCredParams is required and must be an array
    if (!Array.isArray(normalized.pubKeyCredParams) || normalized.pubKeyCredParams.length === 0) {
      normalized.pubKeyCredParams = [
        { type: 'public-key', alg: -7 },
        { type: 'public-key', alg: -257 }
      ];
    }

    // challenge must be Uint8Array
    const challengeStr = pickStringOrValue(normalized.challenge);
    const challengeBytes = decodeMaybeBase64(challengeStr);
    if (challengeBytes) normalized.challenge = challengeBytes;

    // user and user.id must be present for create()
    if (!normalized.user) normalized.user = { name: '', displayName: '', id: new Uint8Array() };
    if (typeof normalized.user.id === 'string') {
      const userIdBytes = decodeMaybeBase64(normalized.user.id);
      if (userIdBytes) normalized.user = Object.assign({}, normalized.user, { id: userIdBytes });
    }

    // excludeCredentials[].id must be Uint8Array
    if (Array.isArray(normalized.excludeCredentials)) {
      normalized.excludeCredentials = normalized.excludeCredentials.map((c) => {
        if (c && typeof c.id === 'string') {
          const decoded = decodeMaybeBase64(c.id);
          return Object.assign({}, c, { id: decoded || c.id });
        }
        return c;
      });
    }

    // Safari can choke on explicit nulls for optional objects.
    // Normalize null to undefined by deleting keys.
    for (const k of ['timeout', 'authenticatorSelection', 'attestation', 'extensions']) {
      if (normalized[k] === null) {
        delete normalized[k];
      }
    }

    return normalized;
  }

  global.ZeroPassWebAuthn = global.ZeroPassWebAuthn || {};
  global.ZeroPassWebAuthn.normalizeCreationOptions = normalizeCreationOptions;
})(window);

