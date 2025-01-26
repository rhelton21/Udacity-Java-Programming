package com.udacity.catpoint.security.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Fake repository implementation for demo purposes. Stores state information in local
 * memory and writes it to user preferences between app loads. This implementation is
 * intentionally less practical to use in production but is sufficient for testing and demo purposes.
 */
public class PretendDatabaseSecurityRepositoryImpl implements SecurityRepository {

    private final Set<Sensor> sensors = new HashSet<>();
    private AlarmStatus alarmStatus;
    private ArmingStatus armingStatus;
    private boolean catDetected;

    // Preference keys for storing state
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    // Preferences and Gson for state serialization
    private static final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
    private static final Gson gson = new Gson();

    /**
     * Constructor initializes the repository by loading saved preferences.
     */
    public PretendDatabaseSecurityRepositoryImpl() {
        // Load system state from preferences, defaulting if none exists
        alarmStatus = AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));
        armingStatus = ArmingStatus.valueOf(prefs.get(ARMING_STATUS, ArmingStatus.DISARMED.toString()));

        // Deserialize saved sensors if present
        String sensorString = prefs.get(SENSORS, null);
        if (sensorString != null) {
            Type type = new TypeToken<Set<Sensor>>() {}.getType();
            sensors.addAll(gson.fromJson(sensorString, type));
        }
    }

    @Override
    public void addSensor(Sensor sensor) {
        if (sensor != null) {
            sensors.add(sensor);
            saveSensors();
        }
    }

	@Override
	public void removeSensor(Sensor sensor) {
		sensors.remove(sensor);
		saveSensors(); 
	}

    @Override
    public void updateSensor(Sensor sensor) {
        if (sensor != null) {
            sensors.remove(sensor); 
            sensors.add(sensor);   
            saveSensors();
        }
    }

    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {
        if (alarmStatus != null) {
            this.alarmStatus = alarmStatus;
            prefs.put(ALARM_STATUS, this.alarmStatus.toString());
        }
    }

    @Override
    public void setArmingStatus(ArmingStatus armingStatus) {
        if (armingStatus != null) {
            this.armingStatus = armingStatus;
            prefs.put(ARMING_STATUS, this.armingStatus.toString());
        }
    }

    @Override
    public Set<Sensor> getSensors() {
        return new HashSet<>(sensors); 
    }

    @Override
    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public ArmingStatus getArmingStatus() {
        return armingStatus;
    }

    @Override
    public boolean isCatDetected() {
        return catDetected;
    }

    /**
     * Updates the cat detection status.
     *
     * @param detected True if a cat is detected, false otherwise.
     */
    public void setCatDetected(boolean detected) {
        this.catDetected = detected;
    }

    /**
     * Saves the current sensor state to preferences by serializing to JSON.
     */
    private void saveSensors() {
        prefs.put(SENSORS, gson.toJson(sensors));
    }
}
