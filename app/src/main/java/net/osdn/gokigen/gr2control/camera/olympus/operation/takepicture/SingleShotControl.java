package net.osdn.gokigen.gr2control.camera.olympus.operation.takepicture;

import android.graphics.RectF;
import android.util.Log;

import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;

import java.util.HashMap;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;


/**
 *   一枚撮影用のクラス
 *
 * Created by MRSa on 2016/06/18.
 */
public class SingleShotControl implements OLYCamera.TakePictureCallback
{
    private final String TAG = toString();
    private final OLYCamera camera;
    private final IIndicatorControl indicator;

    private IAutoFocusFrameDisplay frameDisplayer;

    /**
     *  コンストラクタ
     *
     */
    public SingleShotControl(OLYCamera camera, IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator)
    {
        this.camera = camera;
        this.frameDisplayer = frameDisplayer;
        this.indicator = indicator;
    }

    /**
     *   1枚撮影する
     *
     */
    public void singleShot()
    {
        try
        {
            camera.takePicture(new HashMap<String, Object>(), this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgress(OLYCamera olyCamera, OLYCamera.TakingProgress takingProgress, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
    {
        if (takingProgress != OLYCamera.TakingProgress.EndFocusing)
        {
            return;
        }

        String result = olyCameraAutoFocusResult.getResult();
        if (result == null)
        {
            Log.v(TAG, "FocusResult is null.");
        }
        else switch (result)
        {
            case "ok":
                RectF postFocusFrameRect = olyCameraAutoFocusResult.getRect();
                if (postFocusFrameRect != null)
                {
                    showFocusFrame(postFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Focused, 0.0);
                }
                break;

            case "none":
            default:
                hideFocusFrame();
                break;
        }
    }

    @Override
    public void onCompleted()
    {
        try
        {
            camera.clearAutoFocusPoint();
            hideFocusFrame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorOccurred(Exception e)
    {
        try
        {
            camera.clearAutoFocusPoint();
            hideFocusFrame();
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        e.printStackTrace();
        //control.presentMessage(R.string.shutter_control_take_failed, e.getMessage());
    }

    private void showFocusFrame(RectF rect, IAutoFocusFrameDisplay.FocusFrameStatus status, double duration)
    {
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused == status);
    }

    private void hideFocusFrame()
    {
        if (frameDisplayer != null)
        {
            frameDisplayer.hideFocusFrame();
        }
        indicator.onAfLockUpdate(false);
    }
}
