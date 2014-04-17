#!/bin/sh

BASE_URL=http://localhost:9000
TYPE_URL=$BASE_URL/types

curl -X POST -H "Content-Type: application/json" --data @types/HRTS1.json $TYPE_URL
curl -X POST -H "Content-Type: application/json" --data @ypes/HRMT1.json $TYPE_URL
curl -X POST -H "Content-Type: application/json" --data @ypes/HRMT2.json $TYPE_URL
