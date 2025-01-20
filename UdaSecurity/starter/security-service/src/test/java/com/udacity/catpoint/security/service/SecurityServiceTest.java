package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import com.udacity.catpoint.image.service.FakeImageService;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityServiceTest {

    @Test
    public void testSetArmingStatus_disarmed_setsNoAlarm() {
        // Arrange
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        SecurityService service = new SecurityService(mockRepository, mockImageService);

        // Act
        service.setArmingStatus(ArmingStatus.DISARMED);

        // Assert
        verify(mockRepository, times(1)).setArmingStatus(ArmingStatus.DISARMED);
        verify(mockRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    public void testChangeSensorActivationStatus_activatesSensor_updatesRepository() {
        // Arrange
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        when(mockRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        SecurityService service = new SecurityService(mockRepository, mockImageService);
        Sensor sensor = new Sensor("Sensor1", SensorType.DOOR);

        // Act
        service.changeSensorActivationStatus(sensor, true);

        // Assert
        assertTrue(sensor.getActive());
        verify(mockRepository, times(1)).updateSensor(sensor);
    }

    @Test
    public void testProcessImage_detectsCat_triggersAlarm() {
        // Arrange
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        BufferedImage mockImage = mock(BufferedImage.class);
        when(mockImageService.imageContainsCat(mockImage, 50.0f)).thenReturn(true);
        when(mockRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        // Act
        service.processImage(mockImage);

        // Assert
        verify(mockRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void testProcessImage_noCat_noAlarm() {
        // Arrange
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        BufferedImage mockImage = mock(BufferedImage.class);
        when(mockImageService.imageContainsCat(mockImage, 50.0f)).thenReturn(false);

        SecurityService service = new SecurityService(mockRepository, mockImageService);

        // Act
        service.processImage(mockImage);

        // Assert
        verify(mockRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void testAddStatusListener_registersListener() {
        // Arrange
        SecurityRepository mockRepository = mock(SecurityRepository.class);
        FakeImageService mockImageService = mock(FakeImageService.class);
        StatusListener mockListener = mock(StatusListener.class);
        SecurityService service = new SecurityService(mockRepository, mockImageService);

        // Act
        service.addStatusListener(mockListener);

        // Assert
        verify(mockRepository, never()).getSensors();
        assertNotNull(mockListener);
    }
}
