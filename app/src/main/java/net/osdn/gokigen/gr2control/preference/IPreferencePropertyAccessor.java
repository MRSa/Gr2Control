package net.osdn.gokigen.gr2control.preference;

/**
 *
 *
 *
 */
public interface IPreferencePropertyAccessor
{
    String EXIT_APPLICATION = "exit_application";

    String AUTO_CONNECT_TO_CAMERA = "auto_connect_to_camera";

    String SOUND_VOLUME_LEVEL = "sound_volume_level";
    String SOUND_VOLUME_LEVEL_DEFAULT_VALUE = "OFF";

    String USE_PLAYBACK_MENU = "use_playback_menu";

    String RAW = "raw";

    String LIVE_VIEW_QUALITY = "live_view_quality";
    String LIVE_VIEW_QUALITY_DEFAULT_VALUE = "VGA";

    String CAMERAKIT_VERSION = "camerakit_version";

    String SHOW_GRID_STATUS = "show_grid";

    String SHARE_AFTER_SAVE = "share_after_save";

    String CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW = "capture_both_camera_and_live_view";

    String CONNECTION_METHOD = "connection_method";
    String CONNECTION_METHOD_DEFAULT_VALUE = "RICOH_GR2";

    String GR2_DISPLAY_CAMERA_VIEW = "gr2_display_camera_view";

    String GR2_LCD_SLEEP = "gr2_lcd_sleep";

    String USE_GR2_SPECIAL_COMMAND = "use_gr2_special_command";

    String PENTAX_CAPTURE_AFTER_AF = "pentax_capture_after_auto_focus";

    String DIGITAL_ZOOM_LEVEL = "digital_zoom_level";
    String DIGITAL_ZOOM_LEVEL_DEFAULT_VALUE = "1.0";

    String POWER_ZOOM_LEVEL = "power_zoom_level";
    String POWER_ZOOM_LEVEL_DEFAULT_VALUE = "1.0";

    String MAGNIFYING_LIVE_VIEW_SCALE = "magnifying_live_view_scale";
    String MAGNIFYING_LIVE_VIEW_SCALE_DEFAULT_VALUE = "10.0";

    String RICOH_GET_PICS_LIST_TIMEOUT = "ricoh_get_pics_list_timeout";
    String RICOH_GET_PICS_LIST_TIMEOUT_DEFAULT_VALUE = "5";

/*
    //String GR2_DISPLAY_MODE = "gr2_display_mode";
    //String GR2_DISPLAY_MODE_DEFAULT_VALUE = "0";


    int CHOICE_SPLASH_SCREEN = 10;

    int SELECT_SAMPLE_IMAGE_CODE = 110;
    int SELECT_SPLASH_IMAGE_CODE = 120;

    String getLiveViewSize();
    void restoreCameraSettings(Callback callback);
    void storeCameraSettings(Callback callback);

    interface Callback
    {
        void stored(boolean result);
        void restored(boolean result);
    }
*/

}
