package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.Serial;

/**
 * This is the primary JFrame for the application that contains all the top-level JPanels.
 *
 * We're not using any dependency injection framework, so this class also handles constructing
 * all our dependencies and providing them to other classes as necessary.
 */
public class CatpointGui extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L; // Added serialVersionUID for serialization

    // Updated fields to ensure proper instantiation and separation of concerns
    private final transient SecurityRepository securityRepository = new PretendDatabaseSecurityRepositoryImpl();
    private final transient FakeImageService imageService = new FakeImageService();
    private final transient SecurityService securityService = new SecurityService(securityRepository, imageService);

    // GUI components
    private final DisplayPanel displayPanel = new DisplayPanel(securityService); // Displays system status
    private final ControlPanel controlPanel = new ControlPanel(securityService); // Controls arming/disarming
    private final SensorPanel sensorPanel = new SensorPanel(securityService);   // Manages sensors
    private final ImagePanel imagePanel = new ImagePanel(securityService);     // Handles image processing

    /**
     * Constructor for the main application GUI.
     * Initializes and sets up the JFrame and all panels.
     */
    public CatpointGui() {
        try {
            initializeGui();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to initialize the GUI: " + e.getMessage(), 
                                          "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initializes the GUI components and layout.
     */
    private void initializeGui() {
        setLocation(100, 100);
        setSize(600, 850);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel layout using MigLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());
        mainPanel.add(displayPanel, "wrap");
        mainPanel.add(imagePanel, "wrap");
        mainPanel.add(controlPanel, "wrap");
        mainPanel.add(sensorPanel);

        // Use 'this' explicitly to ensure correct method resolution
        this.getContentPane().add(mainPanel);
    }
}
