#!/bin/bash
[ -d ~/Desktop/ChrisCoin/miner ] || mkdir -p ~/Desktop/ChrisCoin/miner
[ -d ~/Desktop/ChrisCoin/trusted-certificates ] || mkdir -p ~/Desktop/ChrisCoin/trusted-certificates
openssl req -x509 -newkey rsa:2048 -nodes -keyout ~/Desktop/ChrisCoin/miner/private-key.pem -out ~/Desktop/ChrisCoin/miner/certificate.pem -days 365 -subj "/C=NG/ST=Anon/L=./O=./OU=./CN=miner"
cp certificate.pem peer-certificate.pem
