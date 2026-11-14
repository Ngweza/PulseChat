#!/bin/bash
set -e

if [ ! -s "$PGDATA/PG_VERSION" ]; then
  echo "A preparar a cópia inicial do PostgreSQL principal..."

  until gosu postgres pg_basebackup \
    -h "$PGHOST" \
    -p "$PGPORT" \
    -U "$PGUSER" \
    -D "$PGDATA" \
    -Fp \
    -Xs \
    -P \
    -R
  do
    echo "Não foi possível ligar ao principal. Nova tentativa em 5 segundos..."
    sleep 5
  done

  echo "Cópia inicial concluída."
fi

exec /usr/local/bin/docker-entrypoint.sh postgres
