FROM debezium/postgres:16-alpine
ENV  WAL2JSON_TAG=wal2json_2_5
ENV  PGVECTOR_TAG=v0.7.4
RUN    apk add --no-cache --virtual .debezium-build-deps gcc clang15 llvm15 git make musl-dev pkgconf \
         && git clone https://github.com/eulerto/wal2json -b master --single-branch \
         && (cd /wal2json && git checkout tags/$WAL2JSON_TAG -b $WAL2JSON_TAG && make && make install) \
         && (rm -rf wal2json && [ ! -d "wal2json" ] && echo "wal2json removed successfully") \
         && git clone https://github.com/pgvector/pgvector.git -b master --single-branch \
         && (cd /pgvector && git checkout tags/$PGVECTOR_TAG -b $PGVECTOR_TAG && make && make install) \
         && (rm -rf pgvector && [ ! -d "pgvector" ] && echo "pgvector removed successfully") \
         && apk del .debezium-build-deps