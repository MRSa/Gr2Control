package net.osdn.gokigen.gr2control.camera.fuji_x.operation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FocusLock;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FocusUnlock;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

public class FujiXFocusingControl implements IFocusingControl, IFujiXCommandCallback
{
    private final String TAG = this.toString();

    public static final int FOCUS_LOCK = 0;
    public static final int FOCUS_UNLOCK = 1;

    private float maxPointLimitWidth;
    private float maxPointLimitHeight;

    private final IFujiXCommandPublisher issuer;
    private final IAutoFocusFrameDisplay frameDisplayer;
    private final IIndicatorControl indicator;
    private RectF preFocusFrameRect = null;


    public FujiXFocusingControl(@NonNull Activity activity, @NonNull IFujiXCommandPublisher issuer, @NonNull final IAutoFocusFrameDisplay frameDisplayer, @NonNull final IIndicatorControl indicator)
    {
        this.issuer = issuer;
        this.frameDisplayer = frameDisplayer;
        this.indicator = indicator;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String focusPoint = preferences.getString(IPreferencePropertyAccessor.FUJI_X_FOCUS_XY, IPreferencePropertyAccessor.FUJI_X_FOCUS_XY_DEFAULT_VALUE);
            String[] focus = focusPoint.split(",");
            if (focus.length == 2)
            {
                maxPointLimitWidth = Integer.parseInt(focus[0]);
                maxPointLimitHeight = Integer.parseInt(focus[1]);
            }
            else
            {
                maxPointLimitWidth = 7.0f;
                maxPointLimitHeight = 7.0f;
            }
            Log.v(TAG, "FOCUS RESOLUTION : " + maxPointLimitWidth + "," + maxPointLimitHeight);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            maxPointLimitWidth = 7.0f;
            maxPointLimitHeight = 7.0f;
        }
    }

    @Override
    public boolean driveAutoFocus(final MotionEvent motionEvent)
    {
        Log.v(TAG, "driveAutoFocus()");
        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
        {
            return (false);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    PointF point = frameDisplayer.getPointWithEvent(motionEvent);
                    if (point != null)
                    {
                        preFocusFrameRect = getPreFocusFrameRect(point);
                        showFocusFrame(preFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Running, 0.0);
                        if (frameDisplayer.isContainsPoint(point))
                        {
                            lockAutoFocus(point);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        try
        {
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    @Override
    public void unlockAutoFocus()
    {
        try
        {
            issuer.enqueueCommand(new FocusUnlock(this));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void halfPressShutter(boolean isPressed)
    {
        lockAutoFocus(new PointF(0.5f, 0.5f));
    }

    private void lockAutoFocus(PointF point)
    {
        try
        {
            byte x = (byte) (0x000000ff & (Math.round(point.x * maxPointLimitWidth) + 1));
            byte y = (byte) (0x000000ff & (Math.round(point.y * maxPointLimitHeight) + 1));
            Log.v(TAG, "Lock AF: [" + x + ","+ y + "]");
            issuer.enqueueCommand(new FocusLock(x, y, this));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     */
    private RectF getPreFocusFrameRect(@NonNull PointF point)
    {
        float imageWidth =  frameDisplayer.getContentSizeWidth();
        float imageHeight =  frameDisplayer.getContentSizeHeight();

        // Display a provisional focus frame at the touched point.
        float focusWidth = 0.125f;  // 0.125 is rough estimate.
        float focusHeight = 0.125f;
        if (imageWidth > imageHeight)
        {
            focusHeight *= (imageWidth / imageHeight);
        }
        else
        {
            focusHeight *= (imageHeight / imageWidth);
        }
        return (new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f));
    }

    /**
     *
     *
     */
    private void showFocusFrame(RectF rect, IAutoFocusFrameDisplay.FocusFrameStatus status, double duration)
    {
        frameDisplayer.showFocusFrame(rect, status, duration);
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused == status);
    }

    /**
     *
     *
     */
    private void hideFocusFrame()
    {
        frameDisplayer.hideFocusFrame();
        indicator.onAfLockUpdate(false);
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
        if (id == FOCUS_LOCK)
        {
            Log.v(TAG, "FOCUS LOCKED");
            if (preFocusFrameRect != null)
            {
                showFocusFrame(preFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Focused, 1.0);  // いったん1秒だけ表示
            }
        }
        else // if (id == FOCUS_UNLOCK)
        {
            Log.v(TAG, "FOCUS UNLOCKED");
            hideFocusFrame();

        }
        preFocusFrameRect = null;
    }
}
