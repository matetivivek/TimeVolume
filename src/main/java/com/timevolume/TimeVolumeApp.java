package com.timevolume;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * JavaFX Application with System Tray support.
 * Features:
 * - Minimizes to system tray on close
 * - Single instance application
 * - Can be restored from system tray
 * - Only exits when closed from Window menu
 */
public class TimeVolumeApp extends Application {

    private Stage primaryStage;
    private TrayIcon trayIcon;
    private boolean exitFromMenu = false;
    private static FileChannel lockChannel;
    private static FileLock fileLock;
    private static final String LOCK_FILE = ".app.lock";

    @Override
    public void init() throws Exception {
        // Check for single instance
        if (!acquireLock()) {
            System.err.println("Another instance of the application is already running.");
            Platform.exit();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Check if system tray is supported
        if (!SystemTray.isSupported()) {
            showAlert("System tray is not supported on this platform.");
            Platform.exit();
            return;
        }

        // Set up the UI
        setupUI();
        
        // Set up system tray
        setupSystemTray();
        
        // Show the stage
        primaryStage.show();
    }

    private void setupUI() {
        primaryStage.setTitle("TimeVolume Application");
        
        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu windowMenu = new Menu("Window");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> exitApplication());
        windowMenu.getItems().add(exitItem);
        menuBar.getMenus().add(windowMenu);
        
        // Create content
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("TimeVolume Application");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label infoLabel = new Label("This application will minimize to system tray when closed.");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 14px;");
        
        Label instructionLabel = new Label("To exit completely, use Window → Exit from the menu.");
        instructionLabel.setWrapText(true);
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        Button minimizeButton = new Button("Minimize to Tray");
        minimizeButton.setOnAction(e -> hideToSystemTray());
        
        contentBox.getChildren().addAll(titleLabel, infoLabel, instructionLabel, minimizeButton);
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(contentBox);
        
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        
        // Handle close request - minimize to tray instead of exiting
        primaryStage.setOnCloseRequest(e -> {
            if (!exitFromMenu) {
                e.consume();
                hideToSystemTray();
            }
        });
    }

    private void setupSystemTray() {
        try {
            // Create system tray icon
            SystemTray tray = SystemTray.getSystemTray();
            
            // Create an image for the tray icon
            BufferedImage trayIconImage = createTrayIcon();
            
            // Create popup menu
            PopupMenu popup = new PopupMenu();
            
            java.awt.MenuItem openItem = new java.awt.MenuItem("Open");
            openItem.addActionListener(e -> Platform.runLater(this::showStage));
            
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(e -> Platform.runLater(this::exitApplication));
            
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            
            // Create tray icon
            trayIcon = new TrayIcon(trayIconImage, "TimeVolume", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("TimeVolume Application");
            
            // Add double-click listener to restore window
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                        Platform.runLater(() -> showStage());
                    }
                }
            });
            
            tray.add(trayIcon);
            
        } catch (AWTException e) {
            showAlert("Unable to add application to system tray: " + e.getMessage());
        }
    }

    private BufferedImage createTrayIcon() {
        // Create a simple colored circle as tray icon
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a blue circle
        g2d.setColor(new Color(41, 128, 185));
        g2d.fillOval(0, 0, size - 1, size - 1);
        
        // Draw a white border
        g2d.setColor(Color.WHITE);
        g2d.drawOval(0, 0, size - 1, size - 1);
        
        g2d.dispose();
        return image;
    }

    private void hideToSystemTray() {
        Platform.runLater(() -> {
            primaryStage.hide();
            if (trayIcon != null) {
                trayIcon.displayMessage("TimeVolume", 
                    "Application minimized to system tray", 
                    TrayIcon.MessageType.INFO);
            }
        });
    }

    private void showStage() {
        primaryStage.show();
        primaryStage.toFront();
    }

    private void exitApplication() {
        exitFromMenu = true;
        
        // Remove tray icon
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
        
        // Release lock
        releaseLock();
        
        // Exit application
        Platform.exit();
        System.exit(0);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Acquire a file lock to ensure single instance
     */
    private boolean acquireLock() {
        try {
            File lockFile = new File(LOCK_FILE);
            lockChannel = new RandomAccessFile(lockFile, "rw").getChannel();
            fileLock = lockChannel.tryLock();
            
            if (fileLock == null) {
                lockChannel.close();
                return false;
            }
            
            // Delete lock file on exit
            lockFile.deleteOnExit();
            return true;
            
        } catch (IOException e) {
            System.err.println("Error acquiring lock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Release the file lock
     */
    private void releaseLock() {
        try {
            if (fileLock != null) {
                fileLock.release();
            }
            if (lockChannel != null) {
                lockChannel.close();
            }
            File lockFile = new File(LOCK_FILE);
            if (lockFile.exists()) {
                lockFile.delete();
            }
        } catch (IOException e) {
            System.err.println("Error releasing lock: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
