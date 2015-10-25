#!/usr/bin/env bash

# Start docker
docker-machine start default
eval "$(docker-machine env --shell bash default)"

# Run
docker start orientdb

