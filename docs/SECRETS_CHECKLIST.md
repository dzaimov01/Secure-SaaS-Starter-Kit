# Secrets Checklist

- [ ] Replace `JWT_SIGNING_KEY` with a strong, random value
- [ ] Store secrets in a vault (AWS Secrets Manager, GCP Secret Manager, etc.)
- [ ] Rotate API keys on a schedule
- [ ] Never commit `.env` or credentials to git
- [ ] Scope database credentials per environment
- [ ] Audit who can read production secrets
