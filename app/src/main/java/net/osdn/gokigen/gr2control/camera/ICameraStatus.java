package net.osdn.gokigen.gr2control.camera;

import java.util.List;

import androidx.annotation.NonNull;

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

    String AE_STATUS_MULTI = "multi";
    String AE_STATUS_ESP = "ESP";
    String AE_STATUS_SPOT = "spot";
    String AE_STATUS_PINPOINT = "Spot";
    String AE_STATUS_CENTER = "center";
    String AE_STATUS_CENTER2 = "Ctr-Weighted";

    String EFFECT = "effect";
    String TAKE_MODE = "exposureMode";
    String IMAGESIZE = "stillSize";
    String MOVIESIZE = "movieSize";

    String APERATURE = "av";
    String SHUTTER_SPEED = "tv";
    String ISO_SENSITIVITY = "sv";
    String EXPREV = "xv";
    String FLASH_XV = "flashxv";

    String TAKE_MODE_MOVIE = "movie";
}
