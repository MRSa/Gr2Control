package net.osdn.gokigen.gr2control.camera;

import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

/**
 *
 */
public interface IInterfaceProvider
{

    ICameraConnection getCameraConnection();
    ICameraButtonControl getButtonControl();

    IDisplayInjector getDisplayInjector();

    ILiveViewControl getLiveViewControl();
    ILiveViewListener getLiveViewListener();
    IFocusingControl getFocusingControl();
    ICameraInformation getCameraInformation();
    IZoomLensControl getZoomLensControl();
    ICaptureControl getCaptureControl();
    ICameraStatus getCameraStatusListHolder();
    ICameraStatusWatcher getCameraStatusWatcher();

    IPlaybackControl getPlaybackControl();

    ICameraHardwareStatus getHardwareStatus();
    ICameraRunMode getCameraRunMode();

    ICameraConnection.CameraConnectionMethod getCammeraConnectionMethod();
    void resetCameraConnectionMethod();
}
