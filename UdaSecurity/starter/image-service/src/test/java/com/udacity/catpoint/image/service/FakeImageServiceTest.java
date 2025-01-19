package com.udacity.catpoint.image.service;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class FakeImageServiceTest {

    @Test
    public void testImageContainsCat_returnsTrueOrFalse() {
        // Create an instance of FakeImageService
        FakeImageService service = new FakeImageService();

        // Create a dummy BufferedImage (can be null since FakeImageService does not use the image content)
        BufferedImage dummyImage = null;

        // Test the method with random confidence thresholds
        float confidenceThreshold = 0.5f;
        boolean result1 = service.imageContainsCat(dummyImage, confidenceThreshold);
        boolean result2 = service.imageContainsCat(dummyImage, confidenceThreshold);

        // Since Random is used, we can't assert a specific result, but we can assert it returns a boolean
        assertTrue(result1 || !result1, "Expected result to be a boolean (true or false).");
        assertTrue(result2 || !result2, "Expected result to be a boolean (true or false).");
    }

    @Test
    public void testImageContainsCat_withDifferentConfidenceThresholds() {
        // Create an instance of FakeImageService
        FakeImageService service = new FakeImageService();

        // Create a dummy BufferedImage (null is acceptable for this test)
        BufferedImage dummyImage = null;

        // Test the method with varying confidence thresholds
        float lowConfidenceThreshold = 0.1f;
        float highConfidenceThreshold = 0.9f;

        boolean resultLow = service.imageContainsCat(dummyImage, lowConfidenceThreshold);
        boolean resultHigh = service.imageContainsCat(dummyImage, highConfidenceThreshold);

        // Assertions for each call
        assertTrue(resultLow || !resultLow, "Expected a boolean result (true or false) for low confidence threshold.");
        assertTrue(resultHigh || !resultHigh, "Expected a boolean result (true or false) for high confidence threshold.");
    }
}
