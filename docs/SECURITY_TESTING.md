# Security Testing

## Automated Tests
```bash
mvn test
```

## What It Covers
- JWT auth flows + refresh rotation
- Workspace RBAC enforcement
- Audit log creation

## Next Steps
- Add ZAP or similar DAST in CI
- Add SAST ruleset in CodeQL
