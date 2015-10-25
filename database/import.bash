#!/usr/bin/env bash

# Start docker
eval "$(docker-machine env --shell bash default)"

# Import database
docker exec -i -t orientdb /orientdb/bin/console.sh "create database remote:localhost/Yields root secret plocal graph; import database /orientdb/config/yields-schema.json.gz"

