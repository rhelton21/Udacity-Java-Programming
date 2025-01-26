package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.service.SecurityService;
import com.udacity.catpoint.security.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Panel containing the 'camera' output. Allows users to 'refresh' the camera
 * by uploading their own picture, and 'scan' the picture, sending it for image analysis.
 */
public class ImagePanel extends JPanel implements StatusListener {
    private final SecurityService securityService;

    private final JLabel cameraHeader;
    private final JLabel cameraLabel;
    private BufferedImage currentCameraImage;

    // Constants for image dimensions
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 225;

    /**
     * Constructor for the ImagePanel.
     *
     * @param securityService The SecurityService instance.
     */
    public ImagePanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = Objects.requireNonNull(securityService, "SecurityService cannot be null");
        securityService.addStatusListener(this);

        // Initialize UI components
        cameraHeader = new JLabel("Camera Feed");
        cameraHeader.setFont(StyleService.HEADING_FONT);

        cameraLabel = new JLabel();
        cameraLabel.setBackground(Color.WHITE);
        cameraLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // Button to refresh the camera feed
        JButton addPictureButton = new JButton("Refresh Camera");
        addPictureButton.addActionListener(e -> refreshCameraFeed());

        // Button to scan the loaded picture
        JButton scanPictureButton = new JButton("Scan Picture");
        scanPictureButton.addActionListener(e -> scanPicture());

        // Add components to the panel
        add(cameraHeader, "span 3, wrap");
        add(cameraLabel, "span 3, wrap");
        add(addPictureButton);
        add(scanPictureButton);
    }

    /**
     * Refresh the camera feed by allowing the user to select a new image.
     */
    private void refreshCameraFeed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Select Picture");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File selectedFile = chooser.getSelectedFile();
            if (!isImageFile(selectedFile)) {
                throw new IOException("Selected file is not a valid image.");
            }

            currentCameraImage = ImageIO.read(selectedFile);
            Image scaledImage = currentCameraImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            cameraLabel.setIcon(new ImageIcon(scaledImage));
            cameraHeader.setText("Camera Feed - Image Loaded");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Invalid image selected. Please choose a valid image file.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        repaint();
    }

    /**
     * Sends the current image to the security service for scanning.
     */
    private void scanPicture() {
        if (currentCameraImage == null) {
            JOptionPane.showMessageDialog(this, "No image loaded. Please load an image before scanning.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        securityService.processImage(currentCameraImage);
    }

    /**
     * Checks if a file is an image based on its extension.
     *
     * @param file The file to check.
     * @return True if the file is an image, false otherwise.
     */
    private boolean isImageFile(File file) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "bmp", "gif"};
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void notify(AlarmStatus status) {
        // No behavior necessary
    }

    @Override
    public void catDetected(boolean catDetected) {
        if (catDetected) {
            cameraHeader.setText("DANGER - CAT DETECTED");
        } else {
            cameraHeader.setText("Camera Feed - No Cats Detected");
        }
    }

    @Override
    public void sensorStatusChanged() {
        // No behavior necessary
    }
}
