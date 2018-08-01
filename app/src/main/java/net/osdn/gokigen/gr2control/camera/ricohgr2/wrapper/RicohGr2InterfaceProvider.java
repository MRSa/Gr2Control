package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.IRicohGr2InterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraCaptureControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraFocusControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraZoomLensControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.connection.RicohGr2Connection;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

/**
 *
 *
 */
public class RicohGr2InterfaceProvider implements IRicohGr2InterfaceProvider, IDisplayInjector
{
    private final String TAG = toString();
    private final Activity activity;
    private final ICameraStatusReceiver provider;
    private final RicohGr2Connection gr2Connection;
    private final RicohGr2CameraButtonControl buttonControl;
    private final RicohGr2StatusChecker statusChecker;
    private final RicohGr2PlaybackControl playbackControl;
    private RicohGr2LiveViewControl liveViewControl;
    private RicohGr2CameraCaptureControl captureControl;
    private RicohGr2CameraZoomLensControl zoomControl;
    private RicohGr2CameraFocusControl focusControl;

    /**
     *
     *
     */
    public RicohGr2InterfaceProvider(@NonNull Activity context, @NonNull ICameraStatusReceiver provider)
    {
        this.activity = context;
        this.provider = provider;
        gr2Connection = new RicohGr2Connection(context, provider);
        liveViewControl = new RicohGr2LiveViewControl();
        zoomControl = new RicohGr2CameraZoomLensControl();
        buttonControl = new RicohGr2CameraButtonControl();
        statusChecker = new RicohGr2StatusChecker(500);
        playbackControl = new RicohGr2PlaybackControl();
    }

    /**
     *
     *
     */
    public void prepare()
    {
        Log.v(TAG, "prepare()");
    }

    /**
     *
     *
     */
    @Override
    public void injectDisplay(IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator, IFocusingModeNotify focusingModeNotify)
    {
        Log.v(TAG, "injectDisplay()");
        focusControl = new RicohGr2CameraFocusControl(frameDisplayer, indicator);
        captureControl = new RicohGr2CameraCaptureControl(frameDisplayer);
    }

    /**
     *
     *
     */
    @Override
    public ICameraConnection getRicohGr2CameraConnection()
    {
        return (gr2Connection);
    }

    /**
     *
     *
     */
    @Override
    public ILiveViewControl getLiveViewControl()
    {
        return (liveViewControl);
    }

    /**
     *
     *
     */
    @Override
    public ILiveViewListener getLiveViewListener()
    {
        if (liveViewControl == null)
        {
            return (null);
        }
        return (liveViewControl.getLiveViewListener());
    }

    @Override
    public IFocusingControl getFocusingControl()
    {
        return (focusControl);
    }

    @Override
    public ICameraInformation getCameraInformation()
    {
        return null;
    }

    @Override
    public IZoomLensControl getZoomLensControl()
    {
        return (zoomControl);
    }

    @Override
    public ICaptureControl getCaptureControl()
    {
        return (captureControl);
    }

    @Override
    public IDisplayInjector getDisplayInjector() {
        return (this);
    }

    @Override
    public ICameraButtonControl getButtonControl()
    {
        return (buttonControl);
    }

    @Override
    public ICameraStatusWatcher getCameraStatusWatcher() {
        return (statusChecker);
    }

    @Override
    public IPlaybackControl getPlaybackControl()
    {
        return (playbackControl);
    }
}
