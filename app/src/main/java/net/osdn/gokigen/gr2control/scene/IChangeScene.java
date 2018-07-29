package net.osdn.gokigen.gr2control.scene;

/**
 *
 */
public interface IChangeScene
{
    void changeSceneToCameraPropertyList();
    void changeSceneToConfiguration();
    void changeCameraConnection();
    void changeSceneToDebugInformation();
    void changeSceneToApiList();
    void exitApplication();
}
