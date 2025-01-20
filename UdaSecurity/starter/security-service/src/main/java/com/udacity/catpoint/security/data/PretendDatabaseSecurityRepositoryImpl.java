package com.udacity.catpoint.security.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class PretendDatabaseSecurityRepositoryImpl implements SecurityRepository {

    private static final Logger logger = Logger.getLogger(PretendDatabaseSecurityRepositoryImpl.class.getName());

    private final Set<Sensor> sensors;
    private AlarmStatus alarmStatus;
    private ArmingStatus armingStatus;

    // Preference keys
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    private static final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
    private static final Gson gson = new Gson();

    public PretendDatabaseSecurityRepositoryImpl() {
        // Load system state from preferences or initialize defaults
        alarmStatus = AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));
        armingStatus = ArmingStatus.valueOf(prefs.get(ARMING_STATUS, ArmingStatus.DISARMED.toString()));

        // Load sensors from preferences or initialize as an empty set
        String sensorString = prefs.get(SENSORS, null);
        if (sensorString == null) {
            sensors = new TreeSet<>();
        } else {
            Type type = new TypeToken<Set<Sensor>>() {}.getType();
            Set<Sensor> deserializedSensors;
            try {
                deserializedSensors = gson.fromJson(sensorString, type);
                for (Sensor sensor : deserializedSensors) {
                    if (sensor.getSensorId() == null) {
                        logger.warning("Detected Sensor with null ID. Generating new UUID.");
                        sensor.setSensorId(UUID.randomUUID());
                    }
                    if (sensor.getSensorType() == null) {
                        logger.warning("Detected Sensor with null SensorType. Setting default SensorType.");
                        sensor.setSensorType(SensorType.DOOR); // Default to DOOR type
                    }
                    if (sensor.getActive() == null) {
                        logger.warning("Detected Sensor with null active state. Setting to inactive.");
                        sensor.setActive(false); // Default to inactive
                    }
                }
            } catch (JsonParseException e) {
                logger.severe("Failed to parse sensors from preferences. Initializing empty set.");
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
