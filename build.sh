#!/bin/bash

# Checkout
git fetch --all
git checkout main
git pull origin main

# Build Maven
docker run --rm -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn clean package -DskipTests=false

# Run Tests
docker run --rm -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn test

# Build Docker Image
DOCKER_IMAGE="pacifiquedev/medical-appointment"
docker build -t "$DOCKER_IMAGE":latest .
commit=$(git rev-parse --short HEAD)
docker tag "$DOCKER_IMAGE":latest "$DOCKER_IMAGE":$commit

# Push Docker Image
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push "$DOCKER_IMAGE":latest
docker push "$DOCKER_IMAGE":$commit
