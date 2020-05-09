package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

public interface IFujiXMessages
{
    int SEQ_DUMMY = 0;
    int SEQ_REGISTRATION = 1;
    int SEQ_START = 2;
    int SEQ_START_2ND = 3;
    int SEQ_START_2ND_READ = 10;
    int SEQ_START_2ND_RECEIVE = 4;
    int SEQ_START_3RD = 5;
    int SEQ_START_4TH = 6;
    int SEQ_CAMERA_REMOTE = 7;
    int SEQ_START_5TH = 8;
    int SEQ_STATUS_REQUEST = 9;
    int SEQ_QUERY_CAMERA_CAPABILITIES = 11;

    int SEQ_CHANGE_TO_LIVEVIEW_ZERO = 20;
    int SEQ_CHANGE_TO_LIVEVIEW_1ST = 21;
    int SEQ_CHANGE_TO_LIVEVIEW_2ND = 22;
    int SEQ_CHANGE_TO_LIVEVIEW_3RD = 23;
    int SEQ_CHANGE_TO_LIVEVIEW_4TH = 24;
    int SEQ_CHANGE_TO_LIVEVIEW_5TH = 25;
    int SEQ_CHANGE_TO_LIVEVIEW_6TH = 26;
    int SEQ_CHANGE_TO_LIVEVIEW_7TH = 27;


    int SEQ_CHANGE_TO_PLAYBACK_ZERO = 30;
    int SEQ_CHANGE_TO_PLAYBACK_1ST = 31;
    int SEQ_CHANGE_TO_PLAYBACK_2ND = 32;
    int SEQ_CHANGE_TO_PLAYBACK_3RD = 33;
    int SEQ_CHANGE_TO_PLAYBACK_4TH = 34;
    int SEQ_CHANGE_TO_PLAYBACK_5TH = 35;
    int SEQ_CHANGE_TO_PLAYBACK_6TH = 36;
    int SEQ_CHANGE_TO_PLAYBACK_7TH = 37;

    int SEQ_SET_PROPERTY_VALUE = 100;
    int SEQ_FOCUS_LOCK = 101;
    int SEQ_FOCUS_UNLOCK = 102;
    int SEQ_CAPTURE = 103;

    int SEQ_IMAGE_INFO = 104;
    int SEQ_THUMBNAIL = 105;
    int SEQ_FULL_IMAGE = 106;

    int SEQ_START_MOVIE = 107;
    int SEQ_FINISH_MOVIE = 108;
}
