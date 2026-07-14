# open-scheduler

An open-source scheduling app (Calendly clone) built with Spring Boot and PostgreSQL.

## Project layout

```
backend/scheduler/   Spring Boot app (Java 17, Maven)
scripts/             Windows helper scripts
docker-compose.yml   Local PostgreSQL
```

## Prerequisites

- JDK 17 (scripts auto-detect a portable JDK in `%USERPROFILE%\.jdks`, or set `JAVA_HOME`)
- Docker (for local PostgreSQL)

## Getting started

```cmd
scripts\db-up.cmd     :: start PostgreSQL (localhost:5432, db/user/pass: openscheduler)
scripts\build.cmd     :: compile + run tests
scripts\run.cmd       :: run the app on http://localhost:8080
scripts\db-down.cmd   :: stop PostgreSQL (data is kept)
```

Database connection can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` env vars.

## Domain model

- **User** – owner of calendars and event types
- **EventType** – a bookable meeting definition (name, slug, duration)
- **AvailabilityRule** – weekly recurring working hours (ISO day-of-week + time range)
- **Booking** – a booked slot against an event type

Schema is managed by Flyway migrations in `backend/scheduler/src/main/resources/db/migration`.
