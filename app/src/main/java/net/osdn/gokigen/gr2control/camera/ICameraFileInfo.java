package net.osdn.gokigen.gr2control.camera;

import java.util.Date;

public interface ICameraFileInfo
{
    Date getDatetime();
    String getDirectoryPath();
    String getFilename();

}
