# ADR-006: API Key Authentication

## Status
Accepted

## Context
Server-to-server access requires API keys.

## Decision
Store API key prefix + SHA-256 hash of secret. Authenticate via `X-API-Key` header.

## Consequences
- Secret is never stored in plain text
- Requires secure distribution for key creation
