package net.osdn.gokigen.gr2control.camera.olympus.wrapper.property;

/**
 *   使用するカメラプロパティのキー一覧
 *
 *
 */
public interface IOlyCameraProperty
{
    String TAKE_MODE = "TAKEMODE";
    String SOUND_VOLUME_LEVEL = "SOUND_VOLUME_LEVEL";
    String RAW = "RAW";

    String FOCUS_STILL = "FOCUS_STILL";
    String AE_LOCK_STATE = "AE_LOCK_STATE";

    String AE = "AE";
    String AE_PINPOINT = "<AE/AE_PINPOINT>";

    String STILL_MF = "<FOCUS_STILL/FOCUS_MF>";
    String STILL_AF = "<FOCUS_STILL/FOCUS_SAF>";

    //String TAKE_MODE_MOVIE = "<TAKEMODE/movie>";
    //String DRIVE_MODE_SINGLE = "<TAKE_DRIVE/DRIVE_NORMAL>";
}
