package net.osdn.gokigen.gr2control.camera.playback;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFileInfo implements ICameraFileInfo, ICameraFileInfoSetter
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
    private long fileSize;

    public CameraFileInfo(@NonNull String path, @NonNull String name)
    {
        this.path = path;
        this.name = name;
        this.dateTime = new Date();
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

    @Override
    public void updateValues(String dateTime, String av, String tv, String sv, String xv, int orientation, String aspectRatio, String model, String latLng, boolean captured)
    {
        this.av = av;
        this.tv = tv;
        this.sv = sv;
        this.xv = xv;
        this.orientation = orientation;
        this.aspectRatio = aspectRatio;
        this.cameraModel = model;
        this.latlng = latLng;
        this.captured = captured;
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            this.dateTime = df.parse(dateTime);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setDate(Date datetime)
    {
        this.dateTime = datetime;
    }

}
