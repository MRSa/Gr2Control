package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.olympus.operation.takepicture.SingleShotControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;

public class OlyCameraCaptureControl implements ICaptureControl
{
    private final String TAG = toString();
    private final SingleShotControl singleShotControl;

    OlyCameraCaptureControl(OlyCameraWrapper wrapper, IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator)
    {
        singleShotControl = new SingleShotControl(wrapper.getOLYCamera(), frameDisplayer, indicator);
    }

    /**
     *   撮影する
     *
     */
    @Override
    public void doCapture(int kind)
    {
        Log.v(TAG, "doCapture() : " + kind);
        try
        {
            singleShotControl.singleShot();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
