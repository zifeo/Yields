#!/usr/bin/env bash

# Start docker
eval "$(docker-machine env --shell bash default)"

# Stop
docker stop redis

