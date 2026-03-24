#!/bin/bash

echo "Loading environment variables from .env file..."

if [ ! -f .env ]; then
    echo "ERROR: .env file not found!"
    echo "Please copy .env.example to .env and configure your settings."
    echo "Example: cp .env.example .env"
    exit 1
fi

# Load environment variables from .env file
export $(grep -v '^#' .env | grep -v '^[[:space:]]*$' | xargs)

echo ""
echo "Starting Grimoire application..."
echo ""

./gradlew bootRun
