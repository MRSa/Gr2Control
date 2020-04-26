package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.playback;

import android.util.Log;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent;

public class FujiXFullImageReceiver implements IFujiXCommandCallback
{
    private final String TAG = toString();
    private final IDownloadContentCallback callback;
    private int receivedLength;

    FujiXFullImageReceiver( @NonNull IDownloadContentCallback callback)
    {
        this.callback = callback;
        this.receivedLength = 0;
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        try
        {
            Log.v(TAG, " receivedMessage() : onCompleted. " + id + " (" + receivedLength + " bytes.)");
            callback.onCompleted();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            {
                callback.onErrorOccurred(e);
            }
        }
    }

    @Override
    public void onReceiveProgress(final int currentBytes, final int totalBytes, byte[] body)
    {
        try
        {
            receivedLength = receivedLength + currentBytes;
            //Log.v(TAG, " onReceiveProgress() " + receivedLength + "/" + totalBytes);
            float percent = (totalBytes == 0) ? 0.0f : ((float) currentBytes / (float) totalBytes);
            ProgressEvent event = new ProgressEvent(percent, null);
            callback.onProgress(body, currentBytes, event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (true);
    }

}
