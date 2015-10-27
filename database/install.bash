#!/usr/bin/env bash

# Start docker
docker-machine start default
eval "$(docker-machine env --shell bash default)"

# Build docker
docker build -t yields/redis .

# Create bind folders
#data="$(pwd)/data"
#mkdir -p $data

# Create container
docker create --name redis -p 6379:6379 yields/redis #-v "$data:/data" yields/redis

