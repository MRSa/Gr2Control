package net.osdn.gokigen.gr2control.camera.playback;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import java.util.Date;

public class CameraFileInfo implements ICameraFileInfo
{
    private final String path;
    private final String name;
    private Date dateTime;
    private boolean captured;
    private String av;
    private String sv;
    private String tv;
    private String xv;
    private int orientation;
    private String aspectRatio;
    private String cameraModel;
    private String latlng;

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
    public void setCaptured(boolean captured)
    {
        this.captured = captured;
    }
    public void setAperature(String av)
    {
        this.av = av;
    }
    public void setShutterSpeed(String tv)
    {
        this.tv = tv;
    }
    public void setIso(String sv)
    {
        this.sv = sv;
    }
    public void setExpRev(String xv)
    {
        this.xv = xv;
    }
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
    }
    public void setAspectRatio(String aspectRatio)
    {
        this.aspectRatio = aspectRatio;
    }
    public void setModel(String cameraModel)
    {
        this.cameraModel = cameraModel;
    }
    public void setLatLng(String latlng)
    {
        this.latlng = latlng;
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

    @Override
    public String getAperature()
    {
        return (av);
    }

    @Override
    public String getShutterSpeed()
    {
        return (tv);
    }

    @Override
    public String getIsoSensitivity()
    {
        return (sv);
    }

    @Override
    public String getExpRev()
    {
        return (xv);
    }

    @Override
    public int getOrientation()
    {
        return (orientation);
    }

    @Override
    public String getAspectRatio()
    {
        return (aspectRatio);
    }

    @Override
    public String getModel()
    {
        return (cameraModel);
    }

    @Override
    public String getLatLng()
    {
        return (latlng);
    }

    @Override
    public boolean getCaptured()
    {
        return (captured);
    }
}
