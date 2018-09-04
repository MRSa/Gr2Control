package net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;

/**
 *
 *
 */
public class RicohGr2MovieShotControl
{
    private static final String TAG = RicohGr2SingleShotControl.class.getSimpleName();
    private final String shootStartUrl = "http://192.168.0.1/v1/camera/shoot/start";
    private final String shootStopUrl = "http://192.168.0.1/v1/camera/shoot/finish";
    private final IAutoFocusFrameDisplay frameDisplayer;
    private int timeoutMs = 6000;
    private boolean isMovieRecording = false;

    /**
     *
     *
     */
    public RicohGr2MovieShotControl(@NonNull IAutoFocusFrameDisplay frameDisplayer)
    {
        this.frameDisplayer = frameDisplayer;
    }

    /**
     *
     *
     */
    public void toggleMovie()
    {
        Log.v(TAG, "toggleMovie()");
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String postData = "";
                        String result = SimpleHttpClient.httpPost((isMovieRecording)? shootStopUrl : shootStartUrl , postData, timeoutMs);
                        if ((result == null)||(result.length() < 1))
                        {
                            Log.v(TAG, "toggleMovie() reply is null.");
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
