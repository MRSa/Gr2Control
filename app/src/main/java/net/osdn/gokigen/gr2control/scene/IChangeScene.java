package net.osdn.gokigen.gr2control.scene;

import net.osdn.gokigen.gr2control.camera.ICameraConnection;

/**
 *
 */
public interface IChangeScene
{
    void changeSceneToCameraPropertyList();
    void changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod connectionMethod);
    void changeCameraConnection();
    void changeSceneToDebugInformation();
    void changeSceneToApiList();
    void changeScenceToImageList();
    void exitApplication();
}
