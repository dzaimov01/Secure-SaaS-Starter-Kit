# ADR-002: JWT Access + Rotating Refresh Tokens

## Status
Accepted

## Context
Stateless access with revocable refresh tokens is required.

## Decision
Use JWT access tokens and store hashed refresh tokens in Postgres. Rotate on refresh.

## Consequences
- Access tokens remain stateless
- Refresh tokens can be revoked and audited
