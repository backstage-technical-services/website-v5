#!/usr/bin/env sh
docker build -f src/main/docker/Dockerfile.jvm -t backstage-api:latest .
