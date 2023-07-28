FROM debezium/postgres:15-alpine
ENV  WAL2JSON_TAG=wal2json_2_5
RUN    apk add --no-cache --virtual .debezium-build-deps gcc clang15 llvm15 git make musl-dev pkgconf \
         && git clone https://github.com/eulerto/wal2json -b master --single-branch \
         && (cd /wal2json && git checkout tags/$WAL2JSON_TAG -b $WAL2JSON_TAG && make && make install) \
         && rm -rf wal2json \
         && apk del .debezium-build-deps