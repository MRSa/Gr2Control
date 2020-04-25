package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraRunModeCallback;

public class RicohGr2RunMode implements ICameraRunMode
{
    private boolean recordingMode = true;

    @Override
    public void changeRunMode(boolean isRecording, @NonNull ICameraRunModeCallback callback)
    {
        // モードレスなので、絶対成功する
        recordingMode = isRecording;
        callback.onCompleted(isRecording);
    }

    @Override
    public boolean isRecordingMode()
    {
        return (recordingMode);
    }
}
