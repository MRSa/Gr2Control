package net.osdn.gokigen.gr2control.camera.ricohgr2.operation;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2AutoFocusControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;

/**
 *
 *
 */
public class RicohGr2CameraFocusControl implements IFocusingControl
{
    private final String TAG = toString();
    private final RicohGr2AutoFocusControl afControl;
    private final IAutoFocusFrameDisplay frameDisplay;

    /**
     *
     *
     */
    public RicohGr2CameraFocusControl(@NonNull final IAutoFocusFrameDisplay frameDisplayer, @NonNull final IIndicatorControl indicator)
    {
        this.frameDisplay = frameDisplayer;
        this.afControl = new RicohGr2AutoFocusControl(frameDisplayer, indicator);
    }

    /**
     *
     *
     */
    @Override
    public boolean driveAutoFocus(MotionEvent motionEvent)
    {
        Log.v(TAG, "driveAutoFocus()");
        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
        {
            return (false);
        }
        try
        {
            PointF point = frameDisplay.getPointWithEvent(motionEvent);
            if (frameDisplay.isContainsPoint(point))
            {
                afControl.lockAutoFocus(point);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    /**
     *
     *
     */
    @Override
    public void unlockAutoFocus()
    {
        afControl.unlockAutoFocus();
    }
}
