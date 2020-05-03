package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

public interface IFujiXCameraCommands
{
/*
    int STOP = 0x1003;
    int IMAGE_INFO = 0x1008;
    int THUMBNAIL_INDEX = 0x100a;
    int SHUTTER = 0x100e;
    int SINGLE_REQUEST = 0x1015;
    int FULL_IMAGE = 0x101b;
    int LAST_IMAGE_CAMERA = 0x9022;
    int FOCUS_POINT = 0x9026;
    int FOCUS_UNLOCK = 0x9027;
    int CAMERA_CAPABILITIES = 0x902b;
*/
    int SHUTTER_SPEED = 0x902c;
    int APERTURE = 0x902d;
    int EXPREV = 0x902e;

    String STOP_STR = "STOP";
    String IMAGE_INFO_STR = "IMAGE_INFO";
    String THUMBNAIL_INDEX_STR = "THUMBNAIL";
    String SHUTTER_STR = "SHUTTER";
    String SINGLE_REQUEST_STR = "SINGLE_REQUEST";
    String FULL_IMAGE_STR = "FULL_IMAGE";
    String LAST_IMAGE_CAMERA_STR = "LAST_IMAGE";
    String FOCUS_POINT_STR = "FOCUS_POINT";
    String FOCUS_UNLOCK_STR = "FOCUS_UNLOCK";
    String CAMERA_CAPABILITIES_STR = "CAMERA_CAPABILITIES";
    String SHUTTER_SPEED_STR = "SHUTTER_SPEED";
    String APERTURE_STR = "APERTURE";
    String EXPREV_STR = "EXPREV";

    String STOP_STR_ID = "0x1003";
    String IMAGE_INFO_STR_ID = "0x1008";
    String THUMBNAIL_INDEX_STR_ID = "0x100a";
    String SHUTTER_STR_ID = "0x100e";
    String SINGLE_REQUEST_STR_ID = "0x1015";
    String FULL_IMAGE_STR_ID = "0x101b";
    String LAST_IMAGE_CAMERA_STR_ID = "0x9022";
    String FOCUS_POINT_STR_ID = "0x9026";
    String FOCUS_UNLOCK_STR_ID = "0x9027";
    String CAMERA_CAPABILITIES_STR_ID = "0x902b";
    String SHUTTER_SPEED_STR_ID = "0x902c";
    String APERTURE_STR_ID = "0x902d";
    String EXPREV_STR_ID = "0x902e";
}
