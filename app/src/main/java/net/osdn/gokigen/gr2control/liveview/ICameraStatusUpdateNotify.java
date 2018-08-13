package net.osdn.gokigen.gr2control.liveview;

/**
 *
 *
 */
public interface ICameraStatusUpdateNotify
{
    void updatedTakeMode(String mode);
    void updatedShutterSpeed(String tv);
    void updatedAperture(String av);
    void updatedExposureCompensation(String xv);
    void updatedMeteringMode(String meteringMode);
    void updatedWBMode(String wbMode);
    void updateRemainBattery(int percentage);

    void updateIsoSensitivity(String sv);
    void updateWarning(String warning);
    void updateStorageStatus(String status);
}
