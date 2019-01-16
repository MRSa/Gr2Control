package net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;

/**
 *
 *
 *
 */
public class RicohGr2SingleShotControl
{
    private static final String TAG = RicohGr2SingleShotControl.class.getSimpleName();
    private final String shootUrl = "http://192.168.0.1/v1/camera/shoot";
    private final IAutoFocusFrameDisplay frameDisplayer;
    private int timeoutMs = 6000;

    /**
     *
     *
     */
    public RicohGr2SingleShotControl(@NonNull IAutoFocusFrameDisplay frameDisplayer)
    {
        this.frameDisplayer = frameDisplayer;
    }

    /**
     *
     *
     */
    public void singleShot(final boolean isCamera)
    {
        Log.v(TAG, "singleShot()");
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        //String postData = (isCamera) ? "af=camera" : "af=on";
                        String postData = (isCamera) ? "af=camera" : "";
                        String result = SimpleHttpClient.httpPost(shootUrl, postData, timeoutMs);
                        if ((result == null)||(result.length() < 1))
                        {
                            Log.v(TAG, "singleShot() reply is null.");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    frameDisplayer.hideFocusFrame();
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
