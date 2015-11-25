#!/usr/bin/env bash

CONF="./src/main/resources/application.conf"
CONTAINER=$(ssh $1 "docker ps -qf label=io.rancher.stack_service.name=Yields/server")
CONTAINER_IP=$(ssh $1 "docker ps -f label=io.rancher.stack_service.name=Yields/server \
               --format '{{ .Label \"io.rancher.container.ip\" }}'")

sed -i.bak "s#addr = \"localhost\"#addr = \"${CONTAINER_IP%???}\"#" ${CONF}
sbt assembly
mv "$CONF.bak" ${CONF}

scp ~/Documents/Yields/server/target/scala-2.11/yields.jar zifeo@avalan.ch:/var/dock/yields/yields.jar
ssh $1 "docker restart $CONTAINER"

