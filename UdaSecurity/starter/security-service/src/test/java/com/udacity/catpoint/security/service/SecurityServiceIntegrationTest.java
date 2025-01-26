package com.udacity.catpoint.security.integration;

import com.udacity.catpoint.security.data.*;
import com.udacity.catpoint.security.service.SecurityService;
import com.udacity.catpoint.image.service.FakeImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SecurityService. These tests ensure that SecurityService interacts correctly
 * with FakeSecurityRepository and FakeImageService and validates various business rules.
 */
public class SecurityServiceIntegrationTest {

    private SecurityService securityService;
    private FakeSecurityRepository fakeRepository;
    private FakeImageService fakeImageService;

    /**
     * Initializes the test environment before each test.
     * Sets up a new SecurityService with a FakeSecurityRepository and FakeImageService.
     */
    @BeforeEach
    public void setUp() {
        fakeRepository = new FakeSecurityRepository();
        fakeImageService = new FakeImageService();
        securityService = new SecurityService(fakeRepository, fakeImageService);
    }

    /**
     * Tests that disarming the system resets the alarm status to NO_ALARM.
     */
    @Test
    public void disarmingSystem_resetsAlarmToNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that arming the system resets all sensors to inactive.
     */
    @Test
    public void armingSystem_resetsAllSensorsToInactive() {
        Sensor sensor1 = new Sensor("Sensor1", SensorType.DOOR);
        Sensor sensor2 = new Sensor("Sensor2", SensorType.WINDOW);

        sensor1.setActive(true);
        sensor2.setActive(true);

        securityService.addSensor(sensor1);
        securityService.addSensor(sensor2);

        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);

        assertFalse(sensor1.getActive());
        assertFalse(sensor2.getActive());
    }

    /**
     * Tests that activating a sensor when the system is armed triggers the appropriate alarm status.
     */
    @Test
    public void armingSystem_withActiveSensor_shouldTriggerPendingAlarm() {
        Sensor sensor = new Sensor("Sensor1", SensorType.DOOR);

        securityService.addSensor(sensor);
        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);
        securityService.changeSensorActivationStatus(sensor, true);

        assertEquals(AlarmStatus.PENDING_ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that detecting a cat while the system is armed at home triggers an alarm.
     */
    @Test
    public void armedHome_detectCat_triggersAlarm() {
        BufferedImage imageWithCat = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        // Simulate a cat detection with FakeImageService
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        fakeImageService.setCatDetection(true); // Added a method to FakeImageService
        securityService.processImage(imageWithCat);

        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that detecting no cat while the system is armed at home does not trigger an alarm.
     */
	@Test
	public void armedHome_noCat_noAlarm() {
		// Explicitly override cat detection to false
		fakeImageService.setCatDetection(false);
    
		BufferedImage imageWithoutCat = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

		// Set the system to ARMED_HOME and process the image
		securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
		securityService.processImage(imageWithoutCat);

		// Expect the alarm status to remain NO_ALARM
		assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
	}

    /**
     * Tests that detecting a cat while the system is disarmed does not trigger an alarm.
     */
    @Test
    public void disarmedSystem_detectCat_noAlarm() {
        BufferedImage imageWithCat = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        fakeRepository.setCatDetected(true); // Simulate cat detection
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        securityService.processImage(imageWithCat);

        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that activating a sensor while the alarm is active does not change the alarm state.
     */
    @Test
    public void activeAlarm_sensorStateChange_doesNotAffectAlarmState() {
        Sensor sensor = new Sensor("Sensor1", SensorType.DOOR);

        securityService.addSensor(sensor);
        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor, true); // Activate again to trigger ALARM

        securityService.changeSensorActivationStatus(sensor, false);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that when all sensors become inactive, the system resets the alarm to NO_ALARM.
     */
    @Test
    public void pendingAlarm_allSensorsInactive_resetsToNoAlarm() {
        Sensor sensor = new Sensor("Sensor1", SensorType.WINDOW);

        securityService.addSensor(sensor);
        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor, false);

        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    /**
     * Tests that the system remains in NO_ALARM state if disarmed while detecting a cat.
     */
    @Test
    public void disarmedSystem_noAlarm_withCatDetection() {
        BufferedImage imageWithCat = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        fakeRepository.setCatDetected(true); // Simulate cat detection
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        securityService.processImage(imageWithCat);

        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
}
