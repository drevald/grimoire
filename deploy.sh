#!/bin/bash
set -e

docker network create web 2>/dev/null || true

docker compose up --build --force-recreate -d web
