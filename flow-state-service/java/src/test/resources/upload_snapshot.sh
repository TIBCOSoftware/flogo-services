#!/bin/bash

curl -H "Content-Type: application/json" http://localhost:9190/snapshots -d @snapshot.json

