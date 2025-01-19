package com.udacity.catpoint.image.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Service for detecting cats in images using AWS Rekognition.
 */
public class AwsImageService {

    private final Logger log = LoggerFactory.getLogger(AwsImageService.class);
    private final RekognitionClient rekognitionClient;

    // Default constructor for production use
    public AwsImageService() {
        this.rekognitionClient = initializeRekognitionClient();
    }

    // Constructor for testing or dependency injection
    public AwsImageService(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    private RekognitionClient initializeRekognitionClient() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                throw new IllegalArgumentException("config.properties file not found");
            }
        } catch (IOException e) {
            log.error("Failed to load AWS configuration properties", e);
            throw new IllegalStateException(e);
        }

        AwsCredentials awsCredentials = AwsBasicCredentials.create(
                props.getProperty("aws.id"),
                props.getProperty("aws.secret")
        );

        return RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(props.getProperty("aws.region")))
                .build();
    }

    /**
     * Determines if a given image contains a cat above a confidence threshold.
     *
     * @param image               the image to analyze
     * @param confidenceThreshold the minimum confidence level for detection
     * @return true if a cat is detected, otherwise false
     */
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshold) {
        if (image == null) {
            log.error("Provided image is null");
            return false;
        }

        Image awsImage;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", os);
            awsImage = Image.builder().bytes(SdkBytes.fromByteArray(os.toByteArray())).build();
        } catch (IOException e) {
            log.error("Error converting image to bytes", e);
            return false;
        }

        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(awsImage)
                .minConfidence(confidenceThreshold)
                .build();

        DetectLabelsResponse response = rekognitionClient.detectLabels(request);
        logDetectedLabels(response);

        return response.labels().stream()
                .anyMatch(label -> label.name().equalsIgnoreCase("cat") && label.confidence() >= confidenceThreshold);
    }

    private void logDetectedLabels(DetectLabelsResponse response) {
        String labels = response.labels().stream()
                .map(label -> String.format("%s (%.2f%%)", label.name(), label.confidence()))
                .collect(Collectors.joining(", "));
        log.info("Detected labels: " + labels);
    }
}
