package net.osdn.gokigen.gr2control.camera.playback;

public interface IProgressEvent
{
    float getProgress();
    boolean isCancellable();
    void requestCancellation();

    interface CancelCallback
    {
        void requestCancellation();
    }
}
