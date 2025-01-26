package com.udacity.catpoint.security.data;

import java.util.Set;

/**
 * Interface showing the methods our security repository will need to support
 */
public interface SecurityRepository {
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
    void setAlarmStatus(AlarmStatus alarmStatus);
    void setArmingStatus(ArmingStatus armingStatus);
    Set<Sensor> getSensors();
    AlarmStatus getAlarmStatus();
    ArmingStatus getArmingStatus();
    /**
     * Checks if a cat is currently detected in the system.
     * 
     * @return True if a cat is detected, false otherwise.
     */
    boolean isCatDetected();
    /**
     * Sets whether a cat is detected in the system.
     * 
     * @param catDetected True if a cat is detected, false otherwise.
     */
    void setCatDetected(boolean catDetected);

}
