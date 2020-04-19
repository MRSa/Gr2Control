package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

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
import net.osdn.gokigen.gr2control.camera.fuji_x.IFujiXInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommunication;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraCaptureControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraFocusControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraZoomLensControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2HardwareStatus;
import net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.connection.RicohGr2Connection;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

public class FujiXInterfaceProvider implements IFujiXInterfaceProvider, IDisplayInjector
{
    private final String TAG = toString();
    //private final Activity activity;
    //private final ICameraStatusReceiver provider;
    private final RicohGr2Connection gr2Connection;
    private final RicohGr2CameraButtonControl buttonControl;
    private final FujiXStatusChecker statusChecker;
    private final FujiXPlaybackControl playbackControl;
    private final RicohGr2HardwareStatus hardwareStatus;
    private final FujiXRunMode runMode;

    private FujiXLiveViewControl liveViewControl;
    private RicohGr2CameraCaptureControl captureControl;
    private RicohGr2CameraZoomLensControl zoomControl;
    private RicohGr2CameraFocusControl focusControl;

    /**
     *
     *
     */
    public FujiXInterfaceProvider(@NonNull Activity context, @NonNull ICameraStatusReceiver provider)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int communicationTimeoutMs = 5000;  // デフォルトは 5000ms とする
        try
        {
            communicationTimeoutMs = Integer.parseInt(preferences.getString(IPreferencePropertyAccessor.RICOH_GET_PICS_LIST_TIMEOUT, IPreferencePropertyAccessor.RICOH_GET_PICS_LIST_TIMEOUT_DEFAULT_VALUE)) * 1000;
            if (communicationTimeoutMs < 5000)
            {
                communicationTimeoutMs = 5000;  // 最小値は 5000msとする。
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //this.activity = context;
        //this.provider = provider;
        gr2Connection = new RicohGr2Connection(context, provider);
        liveViewControl = new FujiXLiveViewControl();
        zoomControl = new RicohGr2CameraZoomLensControl();
        buttonControl = new RicohGr2CameraButtonControl();
        statusChecker = new FujiXStatusChecker(500);
        playbackControl = new FujiXPlaybackControl(communicationTimeoutMs);
        hardwareStatus = new RicohGr2HardwareStatus();
        runMode = new FujiXRunMode();
    }

    public void prepare()
    {
        Log.v(TAG, "prepare()");
    }

    @Override
    public void injectDisplay(IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator, IFocusingModeNotify focusingModeNotify)
    {
        Log.v(TAG, "injectDisplay()");
        focusControl = new RicohGr2CameraFocusControl(true, frameDisplayer, indicator);
        captureControl = new RicohGr2CameraCaptureControl(true, false, frameDisplayer, statusChecker);
    }

    @Override
    public ICameraConnection getCameraConnection()
    {
        return (gr2Connection);
    }

    @Override
    public ILiveViewControl getLiveViewControl()
    {
        return (liveViewControl);
    }

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
        return (null);
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
    public IDisplayInjector getDisplayInjector()
    {
        return (this);
    }

    @Override
    public ICameraStatus getCameraStatusListHolder()
    {
        return (statusChecker);
    }

    @Override
    public ICameraButtonControl getButtonControl()
    {
        return (buttonControl);
    }

    @Override
    public ICameraStatusWatcher getCameraStatusWatcher()
    {
        return (statusChecker);
    }

    @Override
    public IPlaybackControl getPlaybackControl()
    {
        return (playbackControl);
    }

    @Override
    public ICameraHardwareStatus getHardwareStatus()
    {
        return (hardwareStatus);
    }

    @Override
    public ICameraRunMode getCameraRunMode()
    {
        return (runMode);
    }

    @Override
    public IFujiXCommunication getLiveviewCommunication() {
        return null;
    }

    @Override
    public IFujiXCommunication getAsyncEventCommunication() {
        return null;
    }

    @Override
    public IFujiXCommunication getCommandCommunication()
    {
        return (null);
    }

    @Override
    public IFujiXCommandCallback getStatusHolder()
    {
        return null;
    }

    @Override
    public ICameraStatusUpdateNotify getStatusListener() {
        return null;
    }

    @Override
    public IFujiXCommandPublisher getCommandPublisher()
    {
        return null;
    }

    @Override
    public void setAsyncEventReceiver(@NonNull IFujiXCommandCallback receiver)
    {

    }
}
