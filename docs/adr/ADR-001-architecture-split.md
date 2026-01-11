# ADR-001: Clean Architecture Split

## Status
Accepted

## Context
We need a structure that separates API concerns from business logic and infrastructure.

## Decision
Use `api`, `app`, `domain`, `infra`, and `config` packages.

## Consequences
- Clear boundaries and better testability
- Slightly more boilerplate for simple features
