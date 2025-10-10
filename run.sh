#!/usr/bin/env bash
# Helper script to run the TimeVolume JavaFX application

echo "Building TimeVolume application..."

if mvn clean package -q; then
    echo "Build successful! Starting application..."
    mvn javafx:run
else
    echo "Build failed. Please check for errors."
    exit 1
fi
