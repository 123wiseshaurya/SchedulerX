#!/bin/bash

echo "🛑 Stopping JobScheduler Pro..."

# Stop frontend
if [ -f frontend.pid ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null; then
        echo "🌐 Stopping frontend server..."
        kill $FRONTEND_PID
    fi
    rm frontend.pid
fi

# Stop backend
if [ -f backend.pid ]; then
    BACKEND_PID=$(cat backend.pid)
    if ps -p $BACKEND_PID > /dev/null; then
        echo "🔧 Stopping backend server..."
        kill $BACKEND_PID
    fi
    rm backend.pid
fi

# Stop Docker services
echo "🐳 Stopping Docker services..."
cd backend
docker-compose down
cd ..

echo "✅ JobScheduler Pro stopped successfully!"