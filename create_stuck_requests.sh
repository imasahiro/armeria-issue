#!/bin/sh

# Run `./gradlew bootRun` before execute this script.

# Creates a request to fill stucked requests to event-loop.
curl -v http://localhost:8080/my-buggy-service/ &
