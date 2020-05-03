package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

import android.util.Log;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.utils.SimpleLogDumper;

public class FujiXReplyMessageReceiver implements IFujiXCommandCallback
{
    private final String TAG = toString();
    private final String messageHeader;
    private final boolean isLogging;


    public FujiXReplyMessageReceiver(@NonNull String messageHeader, boolean isLogging)
    {
        this.messageHeader = messageHeader;
        this.isLogging = isLogging;
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        if (isLogging)
        {
            SimpleLogDumper.dump_bytes(messageHeader, rx_body);
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
    {
        if (isLogging)
        {
            Log.v(TAG, " " + messageHeader + " onReceiveProgress : " + currentBytes + "/" + totalBytes + " Bytes.");
        }
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }
}
