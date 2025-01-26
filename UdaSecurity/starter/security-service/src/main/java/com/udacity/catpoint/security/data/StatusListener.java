package com.udacity.catpoint.security.data;

/**
 * Interface for receiving updates about the security system's state.
 */
public interface StatusListener {

    /**
     * Notifies the listener of a change in the alarm status.
     *
     * @param status the new alarm status
     */
    void notify(AlarmStatus status);

    /**
     * Notifies the listener whether a cat has been detected.
     *
     * @param catDetected true if a cat is detected, false otherwise
     */
    void catDetected(boolean catDetected);

    /**
     * Notifies the listener of a change in the sensor status.
     */
    void sensorStatusChanged();
}
