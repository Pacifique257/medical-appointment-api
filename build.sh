#!/bin/bash

# Checkout
/usr/bin/git fetch --all
/usr/bin/git checkout main
/usr/bin/git pull origin main

# Build Maven
/usr/bin/docker run --rm -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn clean package -DskipTests=false

# Run Tests
/usr/bin/docker run --rm -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn test

# Build Docker Image
DOCKER_IMAGE="pacifiquedev/medical-appointment"
/usr/bin/docker build -t "$DOCKER_IMAGE":latest .
commit=$(/usr/bin/git rev-parse --short HEAD)
/usr/bin/docker tag "$DOCKER_IMAGE":latest "$DOCKER_IMAGE":$commit

# Push Docker Image
echo "$DOCKER_PASSWORD" | /usr/bin/docker login -u "$DOCKER_USERNAME" --password-stdin
/usr/bin/docker push "$DOCKER_IMAGE":latest
/usr/bin/docker push "$DOCKER_IMAGE":$commit
