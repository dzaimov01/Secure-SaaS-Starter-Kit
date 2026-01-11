# ADR-003: RBAC with Workspace Membership

## Status
Accepted

## Context
Authorization must be workspace-aware with roles and permissions.

## Decision
Define roles + permissions and enforce access through method-level checks with a workspace authorization service.

## Consequences
- Centralized permission mapping
- Easy to extend for multi-tenant isolation
