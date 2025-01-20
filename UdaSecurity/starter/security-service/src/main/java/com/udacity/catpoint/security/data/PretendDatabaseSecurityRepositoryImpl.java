package com.udacity.catpoint.security.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

/**
 * Fake repository implementation for demo purposes. Stores state information in local
 * memory and writes it to user preferences between app loads. This implementation is
 * intentionally a little hard to use in unit tests, so watch out!
 */
public class PretendDatabaseSecurityRepositoryImpl implements SecurityRepository {

    private final Set<Sensor> sensors;
    private AlarmStatus alarmStatus;
    private ArmingStatus armingStatus;

    // Preference keys
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    private static final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
    private static final Gson gson = new Gson(); // Used to serialize objects into JSON

    public PretendDatabaseSecurityRepositoryImpl() {
        // Load system state from prefs, or else default
        alarmStatus = AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));
        armingStatus = ArmingStatus.valueOf(prefs.get(ARMING_STATUS, ArmingStatus.DISARMED.toString()));

        // Deserialize sensors from storage or initialize a new set
        String sensorString = prefs.get(SENSORS, null);
        if (sensorString == null) {
            sensors = new TreeSet<>();
        } else {
            Type type = new TypeToken<Set<Sensor>>() {}.getType();
            Set<Sensor> deserializedSensors;
            try {
                deserializedSensors = gson.fromJson(sensorString, type);
                // Ensure all sensors have valid sensorIds after deserialization
                for (Sensor sensor : deserializedSensors) {
                    if (sensor.getSensorId() == null) {
                        sensor.setSensorId(java.util.UUID.randomUUID());
                    }
                }
            } catch (JsonParseException e) {
                deserializedSensors = new TreeSet<>();
            }
            sensors = deserializedSensors;
        }
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        persistSensors();
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
        persistSensors();
    }

    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
        persistSensors();
    }

    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
        prefs.put(ALARM_STATUS, this.alarmStatus.toString());
    }

    @Override
    public void setArmingStatus(ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
        prefs.put(ARMING_STATUS, this.armingStatus.toString());
    }

    @Override
    public Set<Sensor> getSensors() {
        // Return an unmodifiable view to prevent external modification
        return Collections.unmodifiableSet(sensors);
    }

    @Override
    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public ArmingStatus getArmingStatus() {
        return armingStatus;
    }

    /**
     * Persists the current set of sensors to preferences storage.
     */
    private void persistSensors() {
        prefs.put(SENSORS, gson.toJson(sensors));
    }
}
