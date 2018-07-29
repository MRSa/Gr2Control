package net.osdn.gokigen.gr2control.camera;

import android.view.MotionEvent;

/**
 *
 *
 */
public interface IFocusingControl
{
    boolean driveAutoFocus(MotionEvent motionEvent);
    void unlockAutoFocus();
}
