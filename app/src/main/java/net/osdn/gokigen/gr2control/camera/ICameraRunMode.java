package net.osdn.gokigen.gr2control.camera;

import androidx.annotation.NonNull;

public interface ICameraRunMode
{
    /** カメラの動作モード変更 **/
    void changeRunMode(boolean isRecording, @NonNull ICameraRunModeCallback callback);
    boolean isRecordingMode();
}
