package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.image.service.ImageService;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Service that receives information about changes to the security system. Responsible for
 * forwarding updates to the repository and making decisions about changing the system state.
 *
 * This class contains most of the business logic for the system.
 */
public class SecurityService {

    private final ImageService imageService;
    private final SecurityRepository securityRepository; // Final to ensure immutability
    private final Set<StatusListener> statusListeners = Collections.synchronizedSet(new HashSet<>());
    private boolean catDetection = false;

    /**
     * Constructor for SecurityService.
     *
     * @param securityRepository The security repository.
     * @param imageService       The image analysis service.
     */
    public SecurityService(SecurityRepository securityRepository, ImageService imageService) {
        if (securityRepository == null || imageService == null) {
            throw new IllegalArgumentException("Repository and ImageService cannot be null");
        }
        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }

    /**
     * Sets the current arming status for the system.
     *
     * @param armingStatus The new arming status.
     */
    public void setArmingStatus(ArmingStatus armingStatus) {
	   if (armingStatus == null) {
			throw new NullPointerException("Arming status cannot be null");
	   }
       if (armingStatus == ArmingStatus.ARMED_HOME && catDetection) {
            setAlarmStatus(AlarmStatus.ALARM);
        }
        if (armingStatus == ArmingStatus.DISARMED) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        } else {
            // Reset all sensors to inactive
            getSensors().forEach(sensor -> changeSensorActivationStatus(sensor, false));
        }
        securityRepository.setArmingStatus(armingStatus);
        synchronized (statusListeners) {
            statusListeners.forEach(StatusListener::sensorStatusChanged);
        }
    }

    /**
     * Handles cat detection and updates alarm status accordingly.
     *
     * @param cat True if a cat is detected, false otherwise.
     */
    private void catDetected(Boolean cat) {
        catDetection = cat;
        if (cat && getArmingStatus() == ArmingStatus.ARMED_HOME) {
            setAlarmStatus(AlarmStatus.ALARM);
        } else if (!cat && allSensorsInactive()) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        }

        synchronized (statusListeners) {
            statusListeners.forEach(sl -> sl.catDetected(cat));
        }
    }

    /**
     * Adds a status listener.
     *
     * @param statusListener The status listener to add.
     */
    public void addStatusListener(StatusListener statusListener) {
        statusListeners.add(statusListener);
    }

    /**
     * Removes a status listener.
     *
     * @param statusListener The status listener to remove.
     */
    public void removeStatusListener(StatusListener statusListener) {
        statusListeners.remove(statusListener);
    }

    /**
     * Updates the alarm status and notifies listeners.
     *
     * @param status The new alarm status.
     */
    public void setAlarmStatus(AlarmStatus status) {
        securityRepository.setAlarmStatus(status);
        synchronized (statusListeners) {
            statusListeners.forEach(sl -> sl.notify(status));
        }
    }

    /**
     * Checks if all sensors are inactive.
     *
     * @return True if all sensors are inactive, false otherwise.
     */
    private boolean allSensorsInactive() {
        return getSensors().stream().noneMatch(Sensor::getActive);
    }

    /**
     * Handles activation of a sensor and updates the alarm status accordingly.
     */
    private void handleSensorActivated() {
        if (securityRepository.getAlarmStatus() == AlarmStatus.ALARM) {
            // Ignore state changes if the alarm is already active
            return;
        }
        if (securityRepository.getArmingStatus() == ArmingStatus.DISARMED) {
            return;
        }
        switch (securityRepository.getAlarmStatus()) {
            case NO_ALARM -> setAlarmStatus(AlarmStatus.PENDING_ALARM);
            case PENDING_ALARM -> setAlarmStatus(AlarmStatus.ALARM);
            default -> {
                // No action needed for other statuses
            }
        }
    }

    /**
     * Handles deactivation of a sensor and updates the alarm status accordingly.
     */
    private void handleSensorDeactivated() {
        if (securityRepository.getAlarmStatus() == AlarmStatus.PENDING_ALARM && allSensorsInactive()) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        }
    }

    /**
     * Changes the activation status of a sensor.
     *
     * @param sensor The sensor to update.
     * @param active The new activation status.
     */
    public void changeSensorActivationStatus(Sensor sensor, Boolean active) {
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor cannot be null");
        }

        if (active) {
            if (sensor.getActive()) {
                // If the sensor is already active, only escalate if alarm is pending
                if (securityRepository.getAlarmStatus() == AlarmStatus.PENDING_ALARM) {
                    setAlarmStatus(AlarmStatus.ALARM);
                }
                return;
            }
            sensor.setActive(true);
            securityRepository.updateSensor(sensor);
            handleSensorActivated();
        } else if (Boolean.TRUE.equals(sensor.getActive())) {
            sensor.setActive(false);
            securityRepository.updateSensor(sensor);
            handleSensorDeactivated();
        }
    }

    /**
     * Processes an image to check for a cat and updates the alarm status.
     *
     * @param currentCameraImage The image to process.
     */
    public void processImage(BufferedImage currentCameraImage) {
        if (currentCameraImage == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        catDetected(imageService.imageContainsCat(currentCameraImage, 50.0f));
    }

    // Getters for repository data

    public AlarmStatus getAlarmStatus() {
        return securityRepository.getAlarmStatus();
    }

    public Set<Sensor> getSensors() {
        return Collections.unmodifiableSet(securityRepository.getSensors());
    }

    public void addSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor cannot be null");
        }
        securityRepository.addSensor(sensor);
    }

    public void removeSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor cannot be null");
        }
        securityRepository.removeSensor(sensor);
    }

    public ArmingStatus getArmingStatus() {
        return securityRepository.getArmingStatus();
    }
}
