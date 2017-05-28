#!/bin/bash
# openssl req -newkey rsa:2048 -nodes -keyout private.key -x509 -days 365 -subj "/C=NG/ST=Anon/L=./O=./OU=./CN=." -out certificate.key

openssl req -x509 -newkey rsa:2048 -nodes -keyout ./src/data/wallet/private-key.pem -out ./src/data/wallet/certificate.pem -days 365 -subj "/C=NG/ST=Anon/L=./O=./OU=./CN=."
