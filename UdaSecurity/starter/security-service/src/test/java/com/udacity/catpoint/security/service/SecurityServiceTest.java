package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.udacity.catpoint.security.application.StatusListener;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    private SecurityService securityService;

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private ImageService imageService;

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = new Sensor("Test Sensor", SensorType.DOOR);
    }

    /**
     * Test that when the system is armed and a sensor becomes active, 
     * the alarm status changes to PENDING_ALARM.
     */
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void whenSystemArmedAndSensorActivated_thenAlarmStatusPending(ArmingStatus armingStatus) {
        when(securityRepository.getArmingStatus()).thenReturn(armingStatus);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    /**
     * Test that when the system is armed, the alarm is pending, and a sensor becomes active, 
     * the alarm status changes to ALARM.
     */
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void whenSystemArmedAndPendingAndSensorActivated_thenAlarmStatusAlarm(ArmingStatus armingStatus) {
        when(securityRepository.getArmingStatus()).thenReturn(armingStatus);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    /**
     * Test that when all sensors are inactive while the alarm is pending, 
     * the alarm status changes to NO_ALARM.
     */
    @Test
    void whenAllSensorsInactiveWhilePendingAlarm_thenAlarmStatusNoAlarm() {
        Sensor activeSensor = new Sensor("Active Sensor", SensorType.WINDOW);
        activeSensor.setActive(true);
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(activeSensor);

        when(securityRepository.getSensors()).thenReturn(sensors);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(activeSensor, false);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    /**
     * Test that when the alarm is active, changing a sensor's state does not affect the alarm state.
     */
    @Test
    void whenAlarmActiveAndSensorStateChanges_thenNoEffectOnAlarmState() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository, never()).setAlarmStatus(any());

        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    /**
     * Test that activating an already active sensor while the system is in a pending state changes 
     * the alarm status to ALARM.
     */
    @Test
    void whenSensorAlreadyActiveAndActivatedAgainWhilePending_thenAlarmStatusAlarm() {
        sensor.setActive(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    /**
     * Test that deactivating an already inactive sensor does not change the alarm state.
     */
    @Test
    void whenSensorAlreadyInactiveAndDeactivatedAgain_thenNoEffectOnAlarmState() {
        sensor.setActive(false);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    /**
     * Test that when the system is armed-home and the camera detects a cat, 
     * the alarm status changes to ALARM.
     */
    @Test
    void whenSystemArmedHomeAndCatDetected_thenAlarmStatusAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    /**
     * Test that when no cat is detected and all sensors are inactive, 
     * the alarm status changes to NO_ALARM.
     */
    @Test
    void whenNoCatDetectedAndAllSensorsInactive_thenAlarmStatusNoAlarm() {
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(new Sensor("Inactive Sensor", SensorType.DOOR));
        when(securityRepository.getSensors()).thenReturn(sensors);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    /**
     * Test that disarming the system sets the alarm status to NO_ALARM.
     */
    @Test
    void whenSystemDisarmed_thenAlarmStatusNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    /**
     * Test that arming the system resets all sensors to inactive.
     */
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void whenSystemArmed_thenAllSensorsResetToInactive(ArmingStatus armingStatus) {
        Set<Sensor> sensors = new HashSet<>();
        Sensor activeSensor = new Sensor("Active Sensor", SensorType.WINDOW);
        activeSensor.setActive(true);
        sensors.add(activeSensor);

        when(securityRepository.getSensors()).thenReturn(sensors);

        securityService.setArmingStatus(armingStatus);

        sensors.forEach(sensor -> assertFalse(sensor.getActive()));
    }

    /**
     * Test that if the system is disarmed and a cat is detected, 
     * it remains in NO_ALARM even when re-armed-home.
     */
    @Test
    void whenDisarmedDetectCatAndThenArmedHome_thenAlarmStatusAlarm() {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.setArmingStatus(ArmingStatus.DISARMED);
        securityService.processImage(mock(BufferedImage.class));
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
	
    /**
     * Test that adding a null sensor throws an exception.
     */
    @Test
    void whenAddingNullSensor_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> securityService.addSensor(null));
    }


    /**
     * Test that processing a null image throws an exception.
     */
    @Test
    void whenProcessingNullImage_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> securityService.processImage(null));
    }

    /**
     * Test that setting a null arming status throws an exception.
     */
    @Test
    void whenSettingNullArmingStatus_thenThrowsException() {
        assertThrows(NullPointerException.class, () -> securityService.setArmingStatus(null));
    }

    /**
     * Test that when a cat is detected and the system is armed away,
     * the alarm status remains unchanged.
     */
    @Test
    void whenCatDetectedAndSystemArmedAway_thenNoAlarmStatusChange() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_AWAY);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    /**
     * Test that when no cat is detected but some sensors are active,
     * the alarm status remains unchanged.
     */
    @Test
    void whenNoCatDetectedAndSomeSensorsActive_thenNoAlarmStatusChange() {
        Sensor activeSensor = new Sensor("Active Sensor", SensorType.WINDOW);
        activeSensor.setActive(true);
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(activeSensor);

        when(securityRepository.getSensors()).thenReturn(sensors);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);

        securityService.processImage(mock(BufferedImage.class));

        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    /**
     * Test that removing a status listener works correctly.
     */
    @Test
    void whenStatusListenerRemoved_thenNoNotificationsSent() {
        StatusListener listener = mock(StatusListener.class);
        securityService.addStatusListener(listener);

        securityService.removeStatusListener(listener);

        securityService.setAlarmStatus(AlarmStatus.ALARM);
        verify(listener, never()).notify(AlarmStatus.ALARM);
    }

    /**
     * Test that all listeners are notified when a cat is detected.
     */
    @Test
    void whenCatDetected_thenListenersAreNotified() {
        StatusListener listener1 = mock(StatusListener.class);
        StatusListener listener2 = mock(StatusListener.class);
        securityService.addStatusListener(listener1);
        securityService.addStatusListener(listener2);

        securityService.processImage(mock(BufferedImage.class));

        verify(listener1).catDetected(anyBoolean());
        verify(listener2).catDetected(anyBoolean());
    }
	
	/**
	* Test that calling removeSensor with a null sensor throws an exception.
	*/
	@Test
	void whenRemovingNullSensor_thenThrowsException() {
		assertThrows(IllegalArgumentException.class, 
			() -> securityService.removeSensor(null),
			"Expected an IllegalArgumentException when the sensor to remove is null");
	}

	/**
	* Test that calling changeSensorActivationStatus with a null sensor throws an exception.
	*/
	@Test
	void whenChangingActivationStatusWithNullSensor_thenThrowsException() {
		assertThrows(IllegalArgumentException.class, 
			() -> securityService.changeSensorActivationStatus(null, true),
			"Expected an IllegalArgumentException when the sensor is null");
	}

	/**
	* Test that when a sensor is activated while the system is disarmed, the state does not change.
	*/
	@Test
	void whenSensorActivatedWhileSystemDisarmed_thenNoStateChange() {
		when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);

		securityService.changeSensorActivationStatus(sensor, true);

		verify(securityRepository, never()).setAlarmStatus(any());
	}

	/**
	* Test that when the alarm is already active, activating a sensor does not change the state.
	*/
	@Test
	void whenSensorActivatedWhileAlarmActive_thenNoStateChange() {
		when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

		securityService.changeSensorActivationStatus(sensor, true);

		verify(securityRepository, never()).setAlarmStatus(any());
	}

    /**
     * Test that initializing SecurityService with null arguments throws an exception.
     */
    @Test
    void whenConstructorArgumentsAreNull_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new SecurityService(null, imageService), 
            "Expected exception when securityRepository is null"
        );

        assertThrows(IllegalArgumentException.class, 
            () -> new SecurityService(securityRepository, null), 
            "Expected exception when imageService is null"
        );
    }
	 	
    /**
     * Test that when handleSensorActivated is called and alarm is already active, no changes occur.
     */
    @Test
    void whenHandleSensorActivatedAndAlarmAlreadyActive_thenNoChanges() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
    }	
	
	/**
     * Test that when handleSensorActivated is called and system is disarmed, no changes occur.
     */
    @Test
    void whenHandleSensorActivatedAndSystemDisarmed_thenNoChanges() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    /**
     * Test changeSensorActivationStatus with an active sensor, no escalation to ALARM.
     */
    @Test
    void whenChangeSensorActivationAndSensorAlreadyActive_thenNoEscalationToAlarm() {
        sensor.setActive(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    /**
     * Test removeSensor with a null sensor.
     */
    @Test
    void whenRemoveNullSensor_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> securityService.removeSensor(null), 
            "Expected exception when removing a null sensor"
        );
    }
	
	/**
     * Test that removing a sensor invokes the repository's removeSensor method.
     */
    @Test
    void whenRemovingValidSensor_thenRepositoryRemoveSensorInvoked() {
        securityService.removeSensor(sensor);
        verify(securityRepository).removeSensor(sensor);
    }
    /**
     * Test that removing a sensor while system is armed does not change alarm state.
     */
    @Test
    void whenRemovingSensorWhileSystemArmed_thenNoAlarmStateChange() {
		lenient().when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
		lenient().when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

		securityService.removeSensor(sensor);

		verify(securityRepository).removeSensor(sensor);
		verify(securityRepository, never()).setAlarmStatus(any());	
	}    
	/**
     * Test that removing a sensor while system is disarmed does not change alarm state.
     */
    @Test
    void whenRemovingSensorWhileSystemDisarmed_thenNoAlarmStateChange() {
		lenient().when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);

		securityService.removeSensor(sensor);

		verify(securityRepository).removeSensor(sensor);
		verify(securityRepository, never()).setAlarmStatus(any());
    }

    /**
     * Test that removing a sensor does not affect other sensors' states.
     */
	@Test
	void whenRemovingSensor_thenOtherSensorsUnaffected() {
		// Create a second sensor to test unaffected state
		Sensor otherSensor = new Sensor("Other Sensor", SensorType.WINDOW);
		otherSensor.setActive(true);

		// Add both sensors to the repository's sensor set
		Set<Sensor> sensors = new HashSet<>();
		sensors.add(sensor);
		sensors.add(otherSensor);

		// Call the method to remove a sensor
		securityService.removeSensor(sensor);

		// Ensure the other sensor's state remains unaffected
		assertTrue(otherSensor.getActive(), "Other sensor's state should remain active");

		// Verify repository interaction for the removed sensor
		verify(securityRepository).removeSensor(sensor);
		verify(securityRepository, never()).updateSensor(otherSensor); // Ensure no changes to other sensor
	}
}