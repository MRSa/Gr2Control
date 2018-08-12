package net.osdn.gokigen.gr2control.camera.olympus.myolycameraprops;

interface ILoadSaveCameraProperties
{
    int MAX_STORE_PROPERTIES = 256;   // お気に入り設定の最大記憶数...
    String TITLE_KEY = "CameraPropTitleKey";
    String DATE_KEY = "CameraPropDateTime";

    void loadCameraSettings(final String id, final String dataName);
    void saveCameraSettings(final String id, final String dataName);
}
