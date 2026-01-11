# Security Checklist

## Authentication
- [ ] Enforce strong password policy
- [ ] Configure JWT signing key rotation
- [ ] Enable OAuth2 login if needed
- [ ] Limit refresh token lifetime

## Authorization
- [ ] Validate workspace membership on every resource access
- [ ] Review role-permission mapping
- [ ] Add tenant isolation if required

## Data Protection
- [ ] Use TLS everywhere
- [ ] Encrypt secrets in CI/CD
- [ ] Avoid logging PII and tokens

## CSRF
- [ ] CSRF tokens are enabled for browser sessions; API routes are excluded by default.

## Monitoring
- [ ] Export audit logs to SIEM
- [ ] Set alerts on auth anomalies
- [ ] Track rate limit violations

## Operational
- [ ] Run dependency scans
- [ ] Patch base images regularly
- [ ] Perform periodic pen tests
