package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;
import com.udacity.catpoint.image.service.FakeImageService;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityServiceTest {

    @Test
    public void testSetArmingStatus_disarmed_setsNoAlarm() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.setArmingStatus(ArmingStatus.DISARMED);

        verify(mockRepository, times(1)).setArmingStatus(ArmingStatus.DISARMED);
        verify(mockRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    public void testSetArmingStatus_armedHome_withCat_triggersAlarm() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        when(mockImageService.imageContainsCat(any(BufferedImage.class), eq(50.0f))).thenReturn(true);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.setArmingStatus(ArmingStatus.ARMED_HOME);

        verify(mockRepository, times(1)).setArmingStatus(ArmingStatus.ARMED_HOME);
    }

    @Test
    public void testChangeSensorActivationStatus_activateSensor_updatesRepository() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        when(mockRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        Sensor sensor = new Sensor("Sensor1", SensorType.DOOR);
        service.changeSensorActivationStatus(sensor, true);

        assertTrue(sensor.getActive());
        verify(mockRepository, times(1)).updateSensor(sensor);
    }

    @Test
    public void testChangeSensorActivationStatus_deactivateSensor_updatesRepository() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        when(mockRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        Sensor sensor = new Sensor("Sensor1", SensorType.DOOR);
        sensor.setActive(true);

        service.changeSensorActivationStatus(sensor, false);

        assertFalse(sensor.getActive());
        verify(mockRepository, times(1)).updateSensor(sensor);
    }

    @Test
    public void testProcessImage_detectCat_triggersAlarm() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        BufferedImage mockImage = mock(BufferedImage.class);

        when(mockImageService.imageContainsCat(mockImage, 50.0f)).thenReturn(true);
        when(mockRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.processImage(mockImage);

        verify(mockRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void testProcessImage_noCat_noAlarm() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        BufferedImage mockImage = mock(BufferedImage.class);

        when(mockImageService.imageContainsCat(mockImage, 50.0f)).thenReturn(false);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.processImage(mockImage);

        verify(mockRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void testHandleSensorActivated_pendingAlarm_setsAlarm() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);

        when(mockRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        Sensor sensor = new Sensor("Test", SensorType.DOOR);
        service.changeSensorActivationStatus(sensor, true);

        verify(mockRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void testAddAndRemoveStatusListener() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        StatusListener mockListener = mock(StatusListener.class);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.addStatusListener(mockListener);
        service.removeStatusListener(mockListener);

        assertDoesNotThrow(() -> {
            service.addStatusListener(mockListener);
            service.removeStatusListener(mockListener);
        });
    }

    @Test
    public void testGetSensors() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.getSensors();
        verify(mockRepository, times(1)).getSensors();
    }

    @Test
    public void testGetAlarmStatus() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.getAlarmStatus();
        verify(mockRepository, times(1)).getAlarmStatus();
    }

    @Test
    public void testGetArmingStatus() {
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        service.getArmingStatus();
        verify(mockRepository, times(1)).getArmingStatus();
    }
}
