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

    void updateValues(String dateTime, String av, String tv, String sv, String xv, int orientation, String aspectRatio, String model, String LatLng, boolean captured);
}
