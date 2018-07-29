package net.osdn.gokigen.gr2control.camera;


public interface ICameraInformation
{
    boolean isManualFocus();
    boolean isElectricZoomLens();
    boolean isExposureLocked();
}
