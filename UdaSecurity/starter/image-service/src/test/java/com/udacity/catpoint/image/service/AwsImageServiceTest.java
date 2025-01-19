package com.udacity.catpoint.image.service;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;

import java.awt.image.BufferedImage;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AwsImageServiceTest {

    @Test
    public void testImageContainsCat_withHighConfidence_returnsTrue() {
        // Arrange: Mock RekognitionClient and response
        RekognitionClient mockClient = mock(RekognitionClient.class);
        Label catLabel = Label.builder().name("Cat").confidence(95.0f).build();
        DetectLabelsResponse mockResponse = DetectLabelsResponse.builder()
                .labels(Collections.singletonList(catLabel))
                .build();

        when(mockClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(mockResponse);

        AwsImageService service = new AwsImageService(mockClient);
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        // Act: Test the method
        boolean result = service.imageContainsCat(dummyImage, 90.0f);

        // Assert: Validate the result
        assertTrue(result, "Expected to detect a cat with high confidence.");
    }

    @Test
    public void testImageContainsCat_withLowConfidence_returnsFalse() {
        // Arrange: Mock RekognitionClient and response
        RekognitionClient mockClient = mock(RekognitionClient.class);
        Label catLabel = Label.builder().name("Cat").confidence(50.0f).build();
        DetectLabelsResponse mockResponse = DetectLabelsResponse.builder()
                .labels(Collections.singletonList(catLabel))
                .build();

        when(mockClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(mockResponse);

        AwsImageService service = new AwsImageService(mockClient);
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        // Act: Test the method
        boolean result = service.imageContainsCat(dummyImage, 90.0f);

        // Assert: Validate the result
        assertFalse(result, "Expected not to detect a cat with low confidence.");
    }

    @Test
    public void testImageContainsCat_withEmptyLabels_returnsFalse() {
        // Arrange: Mock RekognitionClient and response
        RekognitionClient mockClient = mock(RekognitionClient.class);
        DetectLabelsResponse mockResponse = DetectLabelsResponse.builder()
                .labels(Collections.emptyList())
                .build();

        when(mockClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(mockResponse);

        AwsImageService service = new AwsImageService(mockClient);
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        // Act: Test the method
        boolean result = service.imageContainsCat(dummyImage, 90.0f);

        // Assert: Validate the result
        assertFalse(result, "Expected not to detect a cat when no labels are returned.");
    }

    @Test
    public void testImageContainsCat_withNullImage_returnsFalse() {
        // Arrange: Mock RekognitionClient
        RekognitionClient mockClient = mock(RekognitionClient.class);

        AwsImageService service = new AwsImageService(mockClient);

        // Act: Test with null image
        boolean result = service.imageContainsCat(null, 90.0f);

        // Assert: Validate the result
        assertFalse(result, "Expected not to detect a cat when the image is null.");
    }
}
