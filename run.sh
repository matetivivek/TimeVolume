#!/bin/bash
# Helper script to run the TimeVolume JavaFX application

echo "Building TimeVolume application..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "Build successful! Starting application..."
    mvn javafx:run
else
    echo "Build failed. Please check for errors."
    exit 1
fi
