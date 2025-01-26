package com.udacity.catpoint.security.data;

import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Represents a sensor in the security system. Sensors are associated with a specific type and can be activated or deactivated.
 * Implements Comparable to allow sensors to be sorted based on name, type, and ID.
 */
public class Sensor implements Comparable<Sensor> {

    private static final Logger logger = Logger.getLogger(Sensor.class.getName());

    private UUID sensorId;
    private String name;
    private Boolean active;
    private SensorType sensorType;

    /**
     * Constructor for creating a sensor.
     *
     * @param name       The name of the sensor. Defaults to "Unknown Sensor" if null.
     * @param sensorType The type of the sensor. Defaults to SensorType.DOOR if null.
     */
    public Sensor(String name, SensorType sensorType) {
        this.name = name != null ? name : "Unknown Sensor"; // Default to "Unknown Sensor" if null
        this.sensorType = sensorType != null ? sensorType : SensorType.DOOR; // Default to DOOR if null
        this.sensorId = UUID.randomUUID(); // Always generate a new UUID
        this.active = Boolean.FALSE; // Default to inactive
    }

    /**
     * Checks if two sensors are equal based on their unique IDs.
     *
     * @param o The object to compare this sensor to.
     * @return True if the sensors have the same ID, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;

        logger.info(String.format("Comparing Sensors: this.sensorId=%s, other.sensorId=%s",
                this.sensorId != null ? this.sensorId : "null",
                sensor.sensorId != null ? sensor.sensorId : "null"));

        return Objects.equals(sensorId, sensor.sensorId);
    }

    /**
     * Generates a hash code for the sensor based on its unique ID.
     *
     * @return The hash code for the sensor.
     */
    @Override
    public int hashCode() {
        return Objects.hash(sensorId);
    }

    // Getters and Setters with defensive programming

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            logger.warning("Attempted to set an invalid name.");
        }
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active != null ? active : Boolean.FALSE; // Default to false if null
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        if (sensorType != null) {
            this.sensorType = sensorType;
        } else {
            logger.warning("Attempted to set null sensorType. Keeping the existing value.");
        }
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        if (sensorId != null) {
            this.sensorId = sensorId;
        } else {
            logger.warning("Attempted to set null sensorId. Keeping the existing value.");
        }
    }

    /**
     * Compares sensors based on name, type, and ID, handling null values safely.
     *
     * @param o The sensor to compare this sensor to.
     * @return A negative integer, zero, or a positive integer if this sensor is less than, equal to, or greater than the specified sensor.
     */
    @Override
    public int compareTo(Sensor o) {
        return ComparisonChain.start()
                .compare(this.name != null ? this.name : "", o.name != null ? o.name : "")
                .compare(this.sensorType != null ? this.sensorType.toString() : "Unknown",
                        o.sensorType != null ? o.sensorType.toString() : "Unknown")
                .compare(this.sensorId != null ? this.sensorId.toString() : "",
                        o.sensorId != null ? o.sensorId.toString() : "")
                .result();
    }
}
