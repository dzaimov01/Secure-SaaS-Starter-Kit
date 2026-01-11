# Multi-Tenant Isolation Strategy

Current implementation uses a basic workspace model with row-level isolation.

## Hard Isolation (Planned)
- Schema-per-tenant or database-per-tenant
- Tenant discovery during authentication
- Separate connection pools per tenant

## Migration Plan
1. Add tenant resolver in `app/security`
2. Introduce tenant-aware repositories
3. Migrate existing workspaces to dedicated schemas
