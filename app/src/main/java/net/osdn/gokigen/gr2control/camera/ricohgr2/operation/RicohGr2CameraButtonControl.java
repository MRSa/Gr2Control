package net.osdn.gokigen.gr2control.camera.ricohgr2.operation;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;

/**
 *
 *
 */
public class RicohGr2CameraButtonControl implements ICameraButtonControl
{
    private final String TAG = toString();
    private final String buttonControlUrl = "http://192.168.0.1/_gr";
    private int timeoutMs = 6000;

    /**
     *
     *
     */
    @Override
    public void pushedButton(String code)
    {
        pushButton(code);
    }

    /**
     *
     *
     */
    private void pushButton(@NonNull final String keyName)
    {
        Log.v(TAG, "pushButton()");
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                /**
                 *
                 *
                 */
                @Override
                public void run()
                {
                    try
                    {
                        String cmd = "cmd=" + keyName;
                        String result = SimpleHttpClient.httpPost(buttonControlUrl, cmd, timeoutMs);
                        if ((result == null)||(result.length() < 1)) {
                            Log.v(TAG, "pushButton() reply is null. " + cmd);
                        } else {
                            Log.v(TAG, "pushButton() " + cmd + " result: " + result);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
