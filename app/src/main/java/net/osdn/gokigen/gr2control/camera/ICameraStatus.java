package net.osdn.gokigen.gr2control.camera;

import android.support.annotation.NonNull;

import java.util.List;

/**
 *
 */
public interface ICameraStatus
{
    @NonNull List<String> getStatusList(@NonNull final String key);
    String getStatus(@NonNull final String key);
    void setStatus(@NonNull final String key, @NonNull final String value);

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
}
