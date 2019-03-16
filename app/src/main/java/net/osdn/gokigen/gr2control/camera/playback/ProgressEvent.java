package net.osdn.gokigen.gr2control.camera.playback;

import androidx.annotation.Nullable;

public class ProgressEvent
{
    private final float percent;
    private final CancelCallback callback;

    public ProgressEvent(float percent, @Nullable CancelCallback callback)
    {
        this.percent = percent;
        this.callback = callback;
    }

    public float getProgress()
    {
        return (percent);
    }

    public boolean isCancellable()
    {
        return ((callback != null));
    }

    public void requestCancellation()
    {
        if (callback != null)
        {
            callback.requestCancellation();
        }
    }

    interface CancelCallback
    {
        void requestCancellation();
    }
}
