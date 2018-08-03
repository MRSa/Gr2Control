package net.osdn.gokigen.gr2control.camera.playback;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import java.util.Date;

public class CameraFileInfo implements ICameraFileInfo
{
    private final String path;
    private final String name;
    private Date dateTime;

    public CameraFileInfo(@NonNull String path, @NonNull String name)
    {
        this.path = path;
        this.name = name;
        this.dateTime = new Date();

    }

    public void setDateTime(@NonNull Date dateTime)
    {
        this.dateTime = dateTime;
    }

    @Override
    public Date getDatetime()
    {
        return (dateTime);
    }

    @Override
    public String getDirectoryPath()
    {
        return (path);
    }

    @Override
    public String getFilename()
    {
        return (name);
    }
}
