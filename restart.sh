#!/bin/bash

echo "🔄 Restarting JobScheduler Pro..."

# Stop everything first
./stop.sh

# Wait a moment
sleep 5

# Start everything again
./start.sh