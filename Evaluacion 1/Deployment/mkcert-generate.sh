#!/usr/bin/env bash
set -e
DOM=toolrent
CERT_DIR="$(dirname "$0")/certs"
mkdir -p "$CERT_DIR"
mkcert -install
mkcert -cert-file "$CERT_DIR/${DOM}.pem" -key-file "$CERT_DIR/${DOM}-key.pem" "$DOM" "auth.${DOM}"
chown root:root "$CERT_DIR/${DOM}.pem" "$CERT_DIR/${DOM}-key.pem" || true
echo "Certs created in $CERT_DIR"