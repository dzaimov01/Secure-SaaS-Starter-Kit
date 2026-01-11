# Threat Model (High-Level)

## Assets
- User identities and credentials
- Access/refresh tokens
- Workspace/project data
- API keys
- Audit logs

## Primary Threats
- Credential stuffing and brute force
- Token theft and replay
- Privilege escalation across workspaces
- Injection and deserialization attacks
- Sensitive data leakage in logs

## Controls in This Kit
- Account lockout + rate limiting
- Refresh token rotation + storage of hashed tokens
- Workspace-level RBAC checks
- Input validation + global error handler
- Security headers and CORS defaults
- Structured audit logging

## Residual Risks
- Secrets leakage if environment vars are mismanaged
- API key misuse if not rotated
- Insider misuse without SIEM alerting

## Recommended Next Steps
- Add MFA/SSO
- Add SIEM export + alerting pipeline
- Add tenant isolation strategy if required
