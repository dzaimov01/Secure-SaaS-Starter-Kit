# ADR-004: Audit Logging via Filter

## Status
Accepted

## Context
Requests must be auditable with actor, requestId, IP, and outcome.

## Decision
Use a request filter to persist audit log rows post-request.

## Consequences
- Consistent audit coverage
- Minimal coupling to business logic
