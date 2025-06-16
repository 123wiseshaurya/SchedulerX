#!/bin/bash

echo "🚀 Starting JobScheduler Pro..."

# Start Docker services if not running
echo "🐳 Ensuring Docker services are running..."
cd backend
docker-compose up -d
cd ..

# Wait a moment for services to be ready
sleep 10

# Start backend in background
echo "🔧 Starting backend server..."
cd backend
nohup mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > backend.pid
cd ..

# Wait for backend to be ready with health check
echo "⏳ Waiting for backend to be ready..."
BACKEND_URL="http://localhost:8080"
HEALTH_ENDPOINT="$BACKEND_URL/api/health/simple"
MAX_ATTEMPTS=60
ATTEMPT=1

while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
    echo "Attempt $ATTEMPT/$MAX_ATTEMPTS: Checking backend health..."
    
    # Try to reach the health endpoint
    if curl -s -f "$HEALTH_ENDPOINT" > /dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    
    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo "❌ Backend failed to start after $MAX_ATTEMPTS attempts"
        echo "Check backend.log for errors:"
        tail -20 backend/backend.log
        exit 1
    fi
    
    echo "Backend not ready yet, waiting 5 seconds..."
    sleep 5
    ATTEMPT=$((ATTEMPT + 1))
done

# Start frontend
echo "🌐 Starting frontend server..."
npm run dev &
FRONTEND_PID=$!
echo $FRONTEND_PID > frontend.pid

echo "✅ JobScheduler Pro is starting up!"
echo ""
echo "🌐 Frontend: http://localhost:5173"
echo "🔧 Backend API: http://localhost:8080"
echo "💾 H2 Database Console: http://localhost:8080/h2-console"
echo "📁 MinIO Console: http://localhost:9001 (admin/minioadmin)"
echo ""
echo "📧 Configure email in the 'Email Test' tab for full functionality"
echo "🛑 Stop with: ./stop.sh"

# Keep script running
wait