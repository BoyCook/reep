#!/bin/sh

BASE_URL=http://localhost:9000
MODEL_URL=$BASE_URL/_model

curl -X POST -H "Content-Type: application/json" --data @types/domain.json $MODEL_URL
curl -X POST -H "Content-Type: application/json" --data @ypes/subdomain.json $MODEL_URL
