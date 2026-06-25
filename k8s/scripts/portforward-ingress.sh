#!/usr/bin/env bash
set -u
NS=ingress-nginx
SVC=svc/ingress-nginx-controller
PORT=18080
while true; do
  echo "[$(date '+%F %T')] start port-forward ${PORT}->80"
  kubectl port-forward -n "$NS" --address 0.0.0.0 "$SVC" "${PORT}:80"
  echo "[$(date '+%F %T')] exited ($?). restart sau 3s..."
  sleep 3
done
