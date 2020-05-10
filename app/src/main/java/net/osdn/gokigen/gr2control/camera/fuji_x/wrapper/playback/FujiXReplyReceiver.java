package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.playback;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.utils.SimpleLogDumper;

public class FujiXReplyReceiver implements IFujiXCommandCallback
{
    private final String TAG = toString();

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        try
        {
            SimpleLogDumper.dump_bytes(" RECV : ", rx_body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
    {
        Log.v(TAG, " " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }
}
