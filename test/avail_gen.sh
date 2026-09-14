#!/bin/sh

if [ $# -lt 1 ];
then
    echo "USAGE: $0 {user}:{password} payload_file"
    echo "Sample Payload File:"
    echo "\t1. avail_gen.json"
    exit 1
fi

source ./admin_token.sh $1

export CERT_CONFIG="--cert ../src/main/resources/client_cert/folks-client.crt --key ../src/main/resources/client_cert/folks-client.key --cacert ../src/main/resources/ca/ca_javalabs.crt"

curl --cert ../src/main/resources/client_cert/folks-client.crt --key ../src/main/resources/client_cert/folks-client.key --cacert ../src/main/resources/ca/ca_javalabs.crt -X POST --data-binary @$2 -H "Content-Type:application/json" -H "Authorization: Bearer $token" https://localhost:9443/api/v1/availabilities/gen
