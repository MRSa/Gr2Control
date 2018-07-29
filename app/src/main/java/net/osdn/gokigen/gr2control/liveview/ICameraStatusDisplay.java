package net.osdn.gokigen.gr2control.liveview;

/**
 *
 */
public interface ICameraStatusDisplay
{
    void updateTakeMode();
    void updateDriveMode();
    void updateWhiteBalance();
    void updateBatteryLevel();
    void updateAeMode();
    void updateAeLockState();
    void updateCameraStatus();
    void updateCameraStatus(String message);
    void updateLevelGauge(String orientation, float roll, float pitch);
}
