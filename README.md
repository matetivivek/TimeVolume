# TimeVolume

A JavaFX application with system tray support that demonstrates the following features:

## Features

1. **System Tray Integration**: The application minimizes to the system tray instead of closing when the window close button is clicked
2. **Single Instance**: Only one instance of the application can run at a time
3. **Restore from Tray**: Click the system tray icon to restore the application window
4. **Proper Exit**: Use the "Window → Exit" menu to completely exit the application

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building the Application

```bash
mvn clean package
```

## Running the Application

### Option 1: Using the helper script (Linux/Mac)
```bash
./run.sh
```

### Option 2: Using Maven directly
```bash
mvn javafx:run
```

### Option 3: Run the JAR file (requires JavaFX runtime)
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/TimeVolume-1.0-SNAPSHOT.jar
```

## Usage

1. **Launch the Application**: Run the application using the command above
2. **Minimize to Tray**: Click the window close button (X) or the "Minimize to Tray" button
3. **Restore Window**: Click the system tray icon once to restore the window
4. **Exit Application**: Use "Window → Exit" menu to completely close the application

## How It Works

- **System Tray**: The application creates a system tray icon with a popup menu
- **Single Instance**: Uses file locking mechanism to ensure only one instance can run
- **Window Behavior**: Overrides the close request to hide the window instead of exiting
- **Exit Control**: Only the "Window → Exit" menu will truly close the application

## Project Structure

```
TimeVolume/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── timevolume/
│                   └── TimeVolumeApp.java
├── pom.xml
└── README.md
```

## Technical Details

- **Framework**: JavaFX 17.0.2
- **Build Tool**: Maven
- **Java Version**: 11
- **Main Class**: com.timevolume.TimeVolumeApp

