#!/usr/bin/env bash

# Start docker
docker-machine start default >> /dev/null
eval "$(docker-machine env --shell bash default)"

# Cli
docker exec -i -t redis /usr/local/bin/redis-cli -a "secret"

