#!/bin/bash

echo "🚀 Setting up JobScheduler Pro..."

# Make scripts executable
chmod +x start.sh
chmod +x restart.sh
chmod +x stop.sh

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
npm install

# Install backend dependencies and build
echo "📦 Installing backend dependencies..."
cd backend
mvn clean install -DskipTests
cd ..

# Start Docker services
echo "🐳 Starting Docker services..."
cd backend
docker-compose up -d
cd ..

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check if services are running
echo "🔍 Checking service status..."
cd backend
docker-compose ps
cd ..

echo "✅ Setup complete!"
echo ""
echo "📧 To enable email functionality:"
echo "   1. cd backend"
echo "   2. cp .env.example .env"
echo "   3. Edit .env with your email credentials"
echo "   4. Run ./restart.sh"
echo ""
echo "🎯 Start the application with: ./start.sh"
echo "🌐 Frontend will be available at: http://localhost:5173"
echo "🔧 Backend API will be available at: http://localhost:8080"