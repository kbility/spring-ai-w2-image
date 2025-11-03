#!/bin/bash

cd "$(dirname "$0")"

echo "Starting W-2 Vision Extractor..."

# Start backend
echo "Starting backend..."
cd backend-w2-image
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew bootRun > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Start frontend
echo "Starting frontend..."
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for services
echo "Waiting for services to start..."
sleep 5

# Open browser
echo "Opening browser..."
open http://localhost:3000

echo "Application started!"
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "To stop the application, run: kill $BACKEND_PID $FRONTEND_PID"
echo "Or press Ctrl+C and run: pkill -f 'bootRun|vite'"
