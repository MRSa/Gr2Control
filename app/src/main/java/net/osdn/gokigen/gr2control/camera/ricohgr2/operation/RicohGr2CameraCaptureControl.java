package net.osdn.gokigen.gr2control.camera.ricohgr2.operation;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2SingleShotControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;

/**
 *
 *
 */
public class RicohGr2CameraCaptureControl implements ICaptureControl
{
    private final RicohGr2SingleShotControl singleShotControl;

    /**
     *
     *
     */
    public RicohGr2CameraCaptureControl(@NonNull IAutoFocusFrameDisplay frameDisplayer)
    {
        singleShotControl = new RicohGr2SingleShotControl(frameDisplayer);
    }

    /**
     *
     *
     */
    @Override
    public void doCapture(int kind)
    {
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
