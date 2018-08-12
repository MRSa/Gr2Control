package net.osdn.gokigen.gr2control.camera;

public interface ICameraRunMode
{
    /** カメラの動作モード変更 **/
    void changeRunMode(boolean isRecording);
    boolean isRecordingMode();
}
