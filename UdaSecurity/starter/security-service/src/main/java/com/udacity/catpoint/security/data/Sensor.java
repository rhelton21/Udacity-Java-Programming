package com.udacity.catpoint.security.data;

import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class Sensor implements Comparable<Sensor> {

    private static final Logger logger = Logger.getLogger(Sensor.class.getName());

    private UUID sensorId;
    private String name;
    private Boolean active;
    private SensorType sensorType;

    // Constructor with default handling for null sensorType
    public Sensor(String name, SensorType sensorType) {
        this.name = name != null ? name : "Unknown Sensor"; // Default to "Unknown Sensor" if null
        this.sensorType = sensorType != null ? sensorType : SensorType.DOOR; // Default to DOOR if null
        this.sensorId = UUID.randomUUID(); // Always generate a new UUID
        this.active = Boolean.FALSE; // Default to inactive
    }

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
            logger.warning("Attempted to set invalid name.");
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

    @Override
    public int compareTo(Sensor o) {
        return ComparisonChain.start()
                .compare(this.name, o.name)
                .compare(this.sensorType != null ? this.sensorType.toString() : "Unknown",
                        o.sensorType != null ? o.sensorType.toString() : "Unknown")
                .compare(this.sensorId, o.sensorId)
                .result();
    }
}
