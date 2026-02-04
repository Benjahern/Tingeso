#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="$(dirname "$0")/../docker-compose.prod.yml"
SERVICES=(toolrent-backend-1 toolrent-frontend-1)

if [ -z "${DOCKERHUB_USER:-}" ] || [ -z "${DOCKERHUB_PASS:-}" ]; then
  echo "ERROR: Set DOCKERHUB_USER and DOCKERHUB_PASS environment variables." >&2
  exit 1
fi

echo "Logging in to Docker Hub as $DOCKERHUB_USER..."
echo "$DOCKERHUB_PASS" | docker login --username "$DOCKERHUB_USER" --password-stdin

echo "Building services with docker compose..."
docker compose -f "$COMPOSE_FILE" build "${SERVICES[@]}"

echo "Pushing images to Docker Hub..."
docker compose -f "$COMPOSE_FILE" push "${SERVICES[@]}"

echo "Done."
