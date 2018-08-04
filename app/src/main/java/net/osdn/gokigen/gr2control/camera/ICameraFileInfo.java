package net.osdn.gokigen.gr2control.camera;

import java.util.Date;

public interface ICameraFileInfo
{
    Date getDatetime();
    String getDirectoryPath();
    String getFilename();

    String getAperature();
    String getShutterSpeed();
    String getIsoSensitivity();
    String getExpRev();
    int getOrientation();
    String getAspectRatio();
    String getModel();
    String getLatLng();
    boolean getCaptured();

}
