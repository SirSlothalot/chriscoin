#!/bin/bash

openssl req -x509 -newkey rsa:2048 -nodes -keyout ./src/data/miner/private-key.pem -out ./src/data/miner/certificate.pem -days 365 -subj "/C=NG/ST=Anon/L=./O=./OU=./CN=."
