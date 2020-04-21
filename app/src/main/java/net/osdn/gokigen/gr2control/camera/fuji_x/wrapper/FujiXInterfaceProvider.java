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
import net.osdn.gokigen.gr2control.camera.fuji_x.operation.FujiXButtonControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.operation.FujiXCaptureControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.operation.FujiXFocusingControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.operation.FujiXHardwareStatus;
import net.osdn.gokigen.gr2control.camera.fuji_x.operation.FujiXZoomControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.FujiXAsyncResponseReceiver;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.FujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommunication;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXConnection;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

public class FujiXInterfaceProvider implements IFujiXInterfaceProvider, IDisplayInjector
{
    private final String TAG = toString();

    private static final int STREAM_PORT = 55742;
    private static final int ASYNC_RESPONSE_PORT = 55741;
    private static final int CONTROL_PORT = 55740;
    private static final String CAMERA_IP = "192.168.0.1";

    private final Activity activity;
    //private final ICameraStatusReceiver provider;
    private final FujiXCommandPublisher commandPublisher;
    private final FujiXLiveViewControl liveViewControl;
    private final FujiXAsyncResponseReceiver asyncReceiver;
    private final FujiXConnection fujiXConnection;

    private final FujiXButtonControl buttonControl;
    private final FujiXStatusChecker statusChecker;
    private final FujiXPlaybackControl playbackControl;
    private final FujiXHardwareStatus hardwareStatus;
    private final FujiXRunMode runMode;
    private final FujiXZoomControl zoomControl;

    private FujiXCaptureControl captureControl = null;
    private FujiXFocusingControl focusControl = null;

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
        this.activity = context;
        //this.provider = provider;
        this.commandPublisher = new FujiXCommandPublisher(CAMERA_IP, CONTROL_PORT);
        fujiXConnection = new FujiXConnection(context, provider, this);
        liveViewControl = new FujiXLiveViewControl(context, CAMERA_IP, STREAM_PORT);
        asyncReceiver = new FujiXAsyncResponseReceiver(CAMERA_IP, ASYNC_RESPONSE_PORT);
        zoomControl = new FujiXZoomControl();
        buttonControl = new FujiXButtonControl();
        statusChecker = new FujiXStatusChecker(context, commandPublisher);
        playbackControl = new FujiXPlaybackControl(communicationTimeoutMs);
        hardwareStatus = new FujiXHardwareStatus();
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
        focusControl = new FujiXFocusingControl(activity, commandPublisher, frameDisplayer, indicator);
        captureControl = new FujiXCaptureControl(commandPublisher, frameDisplayer);
    }

    @Override
    public ICameraConnection getCameraConnection()
    {
        return (fujiXConnection);
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
        return (liveViewControl);
    }

    @Override
    public IFujiXCommunication getAsyncEventCommunication() {
        return (asyncReceiver);
    }

    @Override
    public IFujiXCommunication getCommandCommunication()
    {
        return (commandPublisher);
    }

    @Override
    public ICameraStatusUpdateNotify getStatusListener()
    {
        return statusChecker.getStatusListener();
    }

    @Override
    public IFujiXCommandPublisher getCommandPublisher()
    {
        return (commandPublisher);
    }

    @Override
    public void setAsyncEventReceiver(@NonNull IFujiXCommandCallback receiver)
    {
        asyncReceiver.setEventSubscriber(receiver);
    }
}
