package net.osdn.gokigen.gr2control.camera.olympus;

import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraHardwareStatus;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

/**
 *
 */
public interface IOlympusInterfaceProvider
{
    ICameraConnection getOlyCameraConnection();
    ILiveViewControl getLiveViewControl();
    ILiveViewListener getLiveViewListener();
    IFocusingControl getFocusingControl();
    ICameraInformation getCameraInformation();
    IZoomLensControl getZoomLensControl();
    ICaptureControl getCaptureControl();
    ICameraButtonControl getButtonControl();
    IDisplayInjector getDisplayInjector();
    IOlyCameraPropertyProvider getCameraPropertyProvider();
    ICameraStatus getCameraStatusListHolder();
    ICameraStatusWatcher getCameraStatusWatcher();
    IPlaybackControl getPlaybackControl();
    ICameraHardwareStatus getHardwareStatus();
    ICameraRunMode getCameraRunMode();
}
