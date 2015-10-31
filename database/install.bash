#!/usr/bin/env bash

# Start docker
docker-machine start default >> /dev/null
eval "$(docker-machine env --shell bash default)"

# Build docker
docker build -t yields/redis dock

# Create bind folders
data="$(pwd)/data"
mkdir -p "$data"
touch "$data/appendonly.aof" "$data/dump.rdb"
chmod -R 777 "$data"

# Create container
docker create --name redis -v "$data:/data" -p 6379:6379 yields/redis

