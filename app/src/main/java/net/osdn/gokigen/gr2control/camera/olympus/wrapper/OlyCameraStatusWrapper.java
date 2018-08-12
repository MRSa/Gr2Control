package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;

import jp.co.olympus.camerakit.OLYCamera;

public class OlyCameraStatusWrapper implements ICameraStatus, ICameraStatusWatcher
{
    private final OLYCamera camera;
    private ICameraStatusUpdateNotify updateReceiver = null;

    OlyCameraStatusWrapper(OLYCamera camera)
    {
        this.camera = camera;
    }


    @Override
    public @NonNull List<String> getStatusList(@NonNull String key)
    {
        List<String> array = new ArrayList<>();

        // OPC用に変更...
/*
        String BATTERY = "battery";
        String STATE = "state";
        String FOCUS_MODE = "focusMode";
        String AF_MODE = "AFMode";

        String RESOLUTION = "reso";
        String DRIVE_MODE = "shootMode";
        String WHITE_BALANCE = "WBMode";
        String AE = "meteringMode";

        String EFFECT = "effect";
        String TAKE_MODE = "exposureMode";
        String IMAGESIZE = "stillSize";
        String MOVIESIZE = "movieSize";

        String APERATURE = "av";
        String SHUTTER_SPEED = "tv";
        String ISO_SENSITIVITY = "sv";
        String EXPREV = "xv";
        String FLASH_XV = "flashxv";
*/
        return (array);
    }

    @Override
    public String getStatus(@NonNull String key)
    {
        return ("");
    }

    @Override
    public void setStatus(@NonNull String key, @NonNull String value)
    {

    }

    @Override
    public void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier)
    {
        this.updateReceiver = notifier;
        try
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stoptStatusWatch()
    {
        this.updateReceiver = null;

    }
}
