package net.osdn.gokigen.gr2control.camera;

public interface ICameraRunModeCallback
{
    void onCompleted(boolean isRecording);
    void onErrorOccurred(boolean isRecording);
}
