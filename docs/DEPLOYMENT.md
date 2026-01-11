# Deployment

## Local
```bash
cp .env.example .env

docker-compose up -d db
mvn spring-boot:run
```

## Production Notes
- Use managed Postgres and TLS
- Set `JWT_SIGNING_KEY` via a secret manager
- Set `CORS_ALLOWED_ORIGINS` to your domains
- Enable Redis rate limiting if needed: `RATE_LIMIT_REDIS=true`
- Run `mvn test` in CI before deploy

## Docker
```bash
docker-compose up --build
```

## Redis Rate Limiting (Optional)
```bash
docker-compose --profile redis up -d redis
```
Set `RATE_LIMIT_REDIS=true` in your environment.
