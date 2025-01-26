package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Service that tries to guess if an image displays a cat.
 */
public class FakeImageService implements ImageService {
    private final Random r = new Random();
    private Boolean catDetectionOverride = null; // Allows tests to override detection behavior

    /**
     * Determines if the provided image contains a cat.
     *
     * @param image               The image to analyze.
     * @param confidenceThreshold The confidence threshold for detection.
     * @return True if a cat is detected, otherwise false.
     */
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshold) {
        // If an override is set, return it; otherwise, use false as a default for predictability.
        return catDetectionOverride != null ? catDetectionOverride : false;
    }

    /**
     * Sets whether the service should detect a cat for testing purposes.
     *
     * @param detectCat True to simulate cat detection, false otherwise.
     */
    public void setCatDetection(Boolean detectCat) {
        this.catDetectionOverride = detectCat;
    }

    /**
     * Resets the cat detection override to allow default behavior.
     */
    public void resetCatDetection() {
        this.catDetectionOverride = null;
    }
}
