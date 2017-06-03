#!/bin/bash
[ -d ~/Desktop/ChrisCoin/wallet ] || mkdir -p ~/Desktop/ChrisCoin/wallet
[ -d ~/Desktop/ChrisCoin/trusted-certificates ] || mkdir -p ~/Desktop/ChrisCoin/trusted-certificates
openssl req -x509 -newkey rsa:2048 -nodes -keyout ~/Desktop/ChrisCoin/wallet/private-key.pem -out ~/Desktop/ChrisCoin/wallet/certificate.pem -days 365 -subj "/C=NG/ST=Anon/L=./O=./OU=./CN=$1"
cp certificate.pem $1-certificate.pem
mv $1-certificate.pem ../trusted-certificates
