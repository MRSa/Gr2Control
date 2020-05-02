package net.osdn.gokigen.gr2control.camera.fuji_x.cameraproperty;


import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;

class FujiXCameraCommandResponse  implements IFujiXCommandCallback
{
    private final String TAG = toString();
    private final Activity activity;
    private final TextView field;

    FujiXCameraCommandResponse(@NonNull Activity activity, @NonNull TextView field)
    {
        this.activity = activity;
        this.field = field;
    }

    void clear()
    {
        try
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    field.setText("");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] body)
    {
        Log.v(TAG, " " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        //Log.v(TAG, "RECEIVE : " + rx_body.length + " bytes.");
        String message = "[Receive "+ rx_body.length + " bytes.]\n";
        message = message + dump_bytes(rx_body);
        final String messageToShow = message;
        try
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    field.setText(messageToShow);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   デバッグ用：ログにバイト列を出力する
     *
     */
    private String dump_bytes(byte[] data)
    {
        int index = 0;
        StringBuilder message = new StringBuilder();
        for (byte item : data)
        {
            index++;
            message.append(String.format("%02x ", item));
            if (index >= 8)
            {
                message.append("\n");
                index = 0;
            }
        }
        message.append("\n");
        return (message.toString());
    }
}
