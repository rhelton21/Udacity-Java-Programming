package com.udacity.catpoint.security.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Fake implementation of SecurityRepository for integration testing.
 * Stores data in memory and simulates behavior of PretendDatabaseSecurityRepository.
 */
public class FakeSecurityRepository implements SecurityRepository {

    private final Set<Sensor> sensors = new HashSet<>(); // Stores all registered sensors
    private AlarmStatus alarmStatus = AlarmStatus.NO_ALARM; // Default alarm status
    private ArmingStatus armingStatus = ArmingStatus.DISARMED; // Default arming status
    private boolean catDetected = false; // Default cat detection state

    /**
     * Adds a sensor to the repository.
     *
     * @param sensor The sensor to add.
     */
    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    /**
     * Removes a sensor from the repository.
     *
     * @param sensor The sensor to remove.
     */
    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
    }

    /**
     * Updates a sensor in the repository. If the sensor already exists, it is replaced.
     *
     * @param sensor The sensor to update.
     */
    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor); // Remove the old instance of the sensor
        sensors.add(sensor);   // Add the updated instance
    }

    /**
     * Sets the alarm status.
     *
     * @param alarmStatus The new alarm status.
     */
    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    /**
     * Sets the arming status.
     *
     * @param armingStatus The new arming status.
     */
    @Override
    public void setArmingStatus(ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
    }

    /**
     * Retrieves all registered sensors.
     *
     * @return A copy of the set of sensors.
     */
    @Override
    public Set<Sensor> getSensors() {
        return new HashSet<>(sensors); // Return a new set to ensure immutability
    }

    /**
     * Retrieves the current alarm status.
     *
     * @return The current alarm status.
     */
    @Override
    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    /**
     * Retrieves the current arming status.
     *
     * @return The current arming status.
     */
    @Override
    public ArmingStatus getArmingStatus() {
        return armingStatus;
    }

    /**
     * Checks if a cat has been detected.
     *
     * @return True if a cat is detected, false otherwise.
     */
    @Override
    public boolean isCatDetected() {
        return catDetected;
    }

    /**
     * Sets the cat detection state.
     *
     * @param detected True if a cat is detected, false otherwise.
     */
    @Override
    public void setCatDetected(boolean detected) {
        this.catDetected = detected;
    }
}
