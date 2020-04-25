package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import android.util.Log;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraRunModeCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.FujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXCameraModeChangeToLiveView;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXCameraModeChangeToPlayback;

public class FujiXRunMode implements ICameraRunMode, IFujiXRunModeHolder, IFujiXCommandCallback
{
    private final String TAG = toString();
    private final FujiXCameraModeChangeToLiveView toLiveViewCommand;
    private final FujiXCameraModeChangeToPlayback toPlaybackCommand;
    private boolean isChanging = false;
    private boolean isRecordingMode = false;
    private ICameraRunModeCallback runModeCallback = null;

    FujiXRunMode(@NonNull FujiXCommandPublisher commandPublisher)
    {
        //
        toLiveViewCommand = new FujiXCameraModeChangeToLiveView(commandPublisher, this);
        toPlaybackCommand = new FujiXCameraModeChangeToPlayback(commandPublisher, this);
    }

    @Override
    public void changeRunMode(boolean isRecording, @NonNull ICameraRunModeCallback callback)
    {
        // モードを切り替える
        Log.v(TAG, "changeRunMode() : " + isRecording);
        this.runModeCallback = callback;
        if (isRecording)
        {
            toLiveViewCommand.startModeChange(this);
        }
        else
        {
            toPlaybackCommand.startModeChange(this);
        }
    }

    @Override
    public boolean isRecordingMode()
    {
        Log.v(TAG, "isRecordingMode() : " + isRecordingMode + " (" + isChanging + ")");

        if (isChanging)
        {
            // モード変更中の場合は、かならず false を応答する
            return (false);
        }
        return (isRecordingMode);
    }

    @Override
    public void transitToRecordingMode(boolean isFinished)
    {
        isChanging = !isFinished;
        isRecordingMode = true;
        if (isFinished)
        {
            runModeCallback.onCompleted(isRecordingMode);
        }
    }

    @Override
    public void transitToPlaybackMode(boolean isFinished)
    {
        isChanging = !isFinished;
        isRecordingMode = false;
        if (isFinished)
        {
            runModeCallback.onCompleted(isRecordingMode);
        }
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        Log.v(TAG, " receivedMessage() " + id);
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
    {
        Log.v(TAG, " onReceiveProgress() ");
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }
}
