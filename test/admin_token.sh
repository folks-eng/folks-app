#!/bin/sh

f=jwt

if [ $# -eq 2 ];
then
    f="$2"
fi;

token=`cat $f`

export CERT_CONFIG="--cert ../src/main/resources/client_cert/folks-client.crt --key ../src/main/resources/client_cert/folks-client.key --cacert ../src/main/resources/ca/ca_javalabs.crt"

if [ -z $token ];
then
    echo "Auth token not found. Setting it up ..."
    export token=$(curl --cert ../src/main/resources/client_cert/folks-client.crt --key ../src/main/resources/client_cert/folks-client.key --cacert ../src/main/resources/ca/ca_javalabs.crt -su "$1" -X POST -d 'grant_type=client_credentials' https://localhost:9443/api/v1/mgmt/login | awk '/access_token/ {print $3}')
    echo "Bearer $token" | tee $f
fi
