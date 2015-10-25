############################################################
# Dockerfile to run an OrientDB (Graph) Container
############################################################

FROM java:8-jdk

MAINTAINER Orient Technologies LTD (info@orientdb.com)

ENV ORIENTDB_VERSION 2.1.4

#download distribution tar, untar and delete databases
RUN mkdir /orientdb && \
  wget -O orientdb-community-$ORIENTDB_VERSION.tar.gz "http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-$ORIENTDB_VERSION.tar.gz&os=linux" \
  && tar -xvzf orientdb-community-$ORIENTDB_VERSION.tar.gz -C /orientdb --strip-components=1\
  && rm orientdb-community-$ORIENTDB_VERSION.tar.gz \
  && rm -rf /orientdb/databases/*

ENV PATH /orientdb/bin:$PATH

VOLUME ["/orientdb/backup", "/orientdb/databases", "/orientdb/config"]

WORKDIR /orientdb

#OrientDb binary
EXPOSE 2424

#OrientDb http
EXPOSE 2480

# Default command start the server
CMD ["server.sh"]
