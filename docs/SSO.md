# SSO / SAML (Stub)

This kit includes a stub interface for OAuth-based login (`SsoProvider`) and a Google provider placeholder.

## How to Extend
- Implement `SsoProvider` for your IdP
- Exchange authorization codes for tokens
- Map IdP users to local users
- Add tenant discovery if you serve multiple organizations

## Notes
- The `/auth/oauth/google` endpoint is guarded by `app.security.oauth.google-enabled`
- No token exchange is implemented by default
