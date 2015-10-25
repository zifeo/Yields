#!/usr/bin/env bash

docker build -t yields/orientdb-2.1 .

mkdir -p config
mkdir -p databases

docker create --name orientdb -d -v config:/opt/orientdb/config -v databases:/opt/orientdb/databases -p 2424:2424 -p 2480:2480 yields/orientdb-2.1


