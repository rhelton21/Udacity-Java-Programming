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
 * Image Recognition Service that can identify cats. Requires aws credentials to be entered in config.properties to work.
 */
public class AwsImageService {

    private static final Logger log = LoggerFactory.getLogger(AwsImageService.class);

    // AWS recommendation is to maintain only a single instance of client objects
    private static volatile RekognitionClient rekognitionClient;

    // Static initialization for thread safety
    static {
        initializeRekognitionClient();
    }

    private static void initializeRekognitionClient() {
        Properties props = new Properties();
        try (InputStream is = AwsImageService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new IOException("config.properties file not found in resources.");
            }
            props.load(is);

            String awsId = props.getProperty("aws.id");
            String awsSecret = props.getProperty("aws.secret");
            String awsRegion = props.getProperty("aws.region");

            AwsCredentials awsCredentials = AwsBasicCredentials.create(awsId, awsSecret);
            rekognitionClient = RekognitionClient.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.of(awsRegion))
                    .build();
        } catch (IOException ioe) {
            log.error("Unable to initialize AWS Rekognition client due to missing or invalid properties file", ioe);
        }
    }

    /**
     * Returns true if the provided image contains a cat.
     *
     * @param image               Image to scan
     * @param confidenceThreshold Minimum threshold to consider for cat detection.
     * @return True if a cat is detected, false otherwise.
     */
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshold) {
        if (rekognitionClient == null) {
            log.error("RekognitionClient is not initialized. Unable to process image.");
            return false;
        }

        Image awsImage;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", os);
            awsImage = Image.builder().bytes(SdkBytes.fromByteArray(os.toByteArray())).build();
        } catch (IOException ioe) {
            log.error("Error building image byte array", ioe);
            return false;
        }

        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(awsImage)
                .minConfidence(confidenceThreshold)
                .build();

        DetectLabelsResponse response = rekognitionClient.detectLabels(detectLabelsRequest);
        logLabelsForFun(response);

        return response.labels().stream()
                .anyMatch(label -> label.name().toLowerCase().contains("cat"));
    }

    private void logLabelsForFun(DetectLabelsResponse response) {
        log.info(response.labels().stream()
                .map(label -> String.format("%s(%.1f%%)", label.name(), label.confidence()))
                .collect(Collectors.joining(", ")));
    }
}
