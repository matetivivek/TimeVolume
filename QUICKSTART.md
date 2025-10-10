# Quick Start Guide

## Prerequisites

Before running the TimeVolume application, ensure you have:

1. **Java 11 or higher** installed
   ```bash
   java -version
   ```

2. **Maven 3.6 or higher** installed
   ```bash
   mvn -version
   ```

3. **A graphical desktop environment** with system tray support

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/matetivivek/TimeVolume.git
cd TimeVolume
```

### 2. Run the Application

**On Linux/Mac:**
```bash
./run.sh
```

**On Windows or any platform:**
```bash
mvn javafx:run
```

### 3. Using the Application

Once the application starts, you'll see:
- A window with title "TimeVolume Application"
- A menu bar with "Window" menu
- Information about system tray functionality
- A "Minimize to Tray" button

#### To Minimize to System Tray:
- Click the window close button (X), OR
- Click the "Minimize to Tray" button

The application will hide and appear in your system tray with a blue circle icon.

#### To Restore the Window:
- Click once on the blue circle icon in the system tray, OR
- Right-click the tray icon and select "Open"

#### To Completely Exit:
- Use the menu: **Window → Exit**, OR
- Right-click the tray icon and select "Exit"

## Testing Single Instance

To verify the single instance feature:

1. Start the application
2. Try to start it again in a new terminal:
   ```bash
   mvn javafx:run
   ```
3. You should see the error: "Another instance of the application is already running."

## Troubleshooting

### System Tray Not Supported
If you see "System tray is not supported on this platform":
- Ensure you're running on a desktop environment with system tray support
- On Linux, ensure you have a system tray extension installed
- Some minimal window managers may not support system tray

### Build Fails
```bash
# Clean and rebuild
mvn clean compile
```

### Can't Run Multiple Instances
This is by design! The application enforces single instance.
To run multiple instances (not recommended):
- Delete the `.app.lock` file before starting a new instance

## Next Steps

For more detailed information, see:
- `README.md` - Complete documentation
- `IMPLEMENTATION.md` - Technical implementation details
