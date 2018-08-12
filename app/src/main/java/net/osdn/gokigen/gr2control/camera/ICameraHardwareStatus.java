package net.osdn.gokigen.gr2control.camera;

import java.util.Map;

/**
 *
 *
 */
public interface ICameraHardwareStatus
{
    boolean isAvailableHardwareStatus();
    String getLensMountStatus();
    String getMediaMountStatus();

    float getMinimumFocalLength();
    float getMaximumFocalLength();
    float getActualFocalLength();

    Map<String, Object> inquireHardwareInformation();
}
