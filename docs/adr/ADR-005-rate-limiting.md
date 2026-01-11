# ADR-005: Rate Limiting Strategy

## Status
Accepted

## Context
Auth and API endpoints need per-IP and per-user throttling.

## Decision
Provide in-memory limiter by default and optional Redis implementation via feature flag.

## Consequences
- Works out of the box
- Horizontal scaling available with Redis
