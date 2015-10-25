#!/usr/bin/env bash

# Start docker
docker-machine start default
eval "$(docker-machine env --shell bash default)"

# Build docker
docker build -t yields/orientdb-2.1 .

# Create bind folders
path=$(pwd)
echo "Binds in $path"
config=$path/config
database=$path/databases
mkdir -p $config
mkdir -p $database

# Create container
docker create --name orientdb -v $config:/orientdb/config -v $database:/orientdb/databases -p 2424:2424 -p 2480:2480 yields/orientdb-2.1

