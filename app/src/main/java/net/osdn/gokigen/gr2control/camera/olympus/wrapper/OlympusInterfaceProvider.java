package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.app.Activity;

import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraHardwareStatus;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.camera.olympus.IOlympusInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.connection.OlyCameraConnection;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.playback.OlyCameraPlaybackControl;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.OlyCameraPropertyProxy;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

/**
 *
 *
 */
public class OlympusInterfaceProvider implements IOlympusInterfaceProvider, IDisplayInjector
{
    private final OlyCameraWrapper wrapper;
    private final OlyCameraConnection connection;
    private final OlyCameraPropertyProxy propertyProxy;
    private final OlyCameraHardwareStatus hardwareStatus;
    private final OLYCameraPropertyListenerImpl propertyListener;
    private final OlyCameraZoomLensControl zoomLensControl;
    private final OlyCameraPlaybackControl playbackControl;
    private final OlyCameraStatusWrapper statusWrapper;
    private OlyCameraFocusControl focusControl = null;
    private OlyCameraCaptureControl captureControl = null;


    public OlympusInterfaceProvider(Activity context, ICameraStatusReceiver provider)
    {
        this.wrapper = new OlyCameraWrapper(context);
        this.connection = new OlyCameraConnection(context, this.wrapper.getOLYCamera(), provider);
        this.propertyProxy = new OlyCameraPropertyProxy(this.wrapper.getOLYCamera());
        this.hardwareStatus = new OlyCameraHardwareStatus(this.wrapper.getOLYCamera());
        this.propertyListener = new OLYCameraPropertyListenerImpl(this.wrapper.getOLYCamera());
        this.zoomLensControl = new OlyCameraZoomLensControl(context, this.wrapper.getOLYCamera());
        this.playbackControl = new OlyCameraPlaybackControl(this.wrapper.getOLYCamera());
        this.statusWrapper = new OlyCameraStatusWrapper(this.wrapper.getOLYCamera());
    }

    @Override
    public void injectDisplay(IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator, IFocusingModeNotify focusingModeNotify)
    {
        focusControl = new OlyCameraFocusControl(wrapper, frameDisplayer, indicator);
        captureControl = new OlyCameraCaptureControl (wrapper, frameDisplayer, indicator);
        propertyListener.setFocusingControl(focusingModeNotify);
    }

    @Override
    public ICameraConnection getOlyCameraConnection()
    {
        return (connection);
    }

    @Override
    public ICameraHardwareStatus getHardwareStatus()
    {
        return (hardwareStatus);
    }

    @Override
    public IOlyCameraPropertyProvider getCameraPropertyProvider()
    {
        return (propertyProxy);
    }

    @Override
    public ICameraStatus getCameraStatusListHolder()
    {
        return (statusWrapper);
    }

    @Override
    public ICameraStatusWatcher getCameraStatusWatcher()
    {
        return (statusWrapper);
    }

    @Override
    public IPlaybackControl getPlaybackControl()
    {
        return (playbackControl);
    }

    @Override
    public ICameraRunMode getCameraRunMode()
    {
        return (wrapper);
    }

    @Override
    public IZoomLensControl getZoomLensControl()
    {
        return (zoomLensControl);
    }

    @Override
    public ILiveViewControl getLiveViewControl()
    {
        return (wrapper);
    }

    @Override
    public ILiveViewListener getLiveViewListener()
    {
        return (wrapper);
    }

    @Override
    public IFocusingControl getFocusingControl()
    {
        return (focusControl);
    }

    @Override
    public ICaptureControl getCaptureControl() {
        return (captureControl);
    }

    @Override
    public ICameraButtonControl getButtonControl()
    {
        return (null);
    }

    @Override
    public ICameraInformation getCameraInformation()
    {
        return (propertyListener);
    }

    @Override
    public IDisplayInjector getDisplayInjector()
    {
        return (this);
    }
}
