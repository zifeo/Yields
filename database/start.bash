#!/usr/bin/env bash

# Start docker
docker-machine start default >> /dev/null
eval "$(docker-machine env --shell bash default)"

# Run
docker start redis

