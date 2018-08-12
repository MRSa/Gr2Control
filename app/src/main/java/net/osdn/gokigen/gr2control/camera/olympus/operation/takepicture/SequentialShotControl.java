package net.osdn.gokigen.gr2control.camera.olympus.operation.takepicture;

import android.graphics.RectF;
import android.util.Log;

import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;
import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;

/**
 *   連続撮影用のクラス
 *
 */
public class SequentialShotControl implements OLYCamera.TakePictureCallback
{
    private final String TAG = toString();
    //private final Context context;
    private final OLYCamera camera;
    private final IIndicatorControl indicator;
    private IAutoFocusFrameDisplay frameDisplayer;

    private IIndicatorControl.shootingStatus currentStatus = IIndicatorControl.shootingStatus.Unknown;

    /**
     *   コンストラクタ
     *
     */
    //public SequentialShotControl(Context context, OLYCamera camera, IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator)
    public SequentialShotControl(OLYCamera camera, IAutoFocusFrameDisplay frameDisplayer, IIndicatorControl indicator)
    {
        //this.context = context;
        this.camera = camera;
        this.frameDisplayer = frameDisplayer;
        this.indicator = indicator;
    }

    /**
     *   撮影の開始と終了
     *
     */
    public void shotControl()
    {
        if (camera.isRecordingVideo())
        {
            // ビデオ撮影中の場合は、何もしない（モード異常なので）
            return;
        }
        try
        {
           if (!camera.isTakingPicture())
            {
                // 連続撮影の開始
                currentStatus = IIndicatorControl.shootingStatus.Starting;
                camera.startTakingPicture(null, this);
                indicator.onShootingStatusUpdate(currentStatus);
            }
            else
            {
                // 連続撮影の終了
                currentStatus = IIndicatorControl.shootingStatus.Stopping;
                camera.stopTakingPicture(this);
                indicator.onShootingStatusUpdate(currentStatus);
            }
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
    @Override
    public void onProgress(OLYCamera olyCamera, OLYCamera.TakingProgress takingProgress, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
    {
        if (currentStatus == IIndicatorControl.shootingStatus.Stopping)
        {
            // 終了中の時にはなにもしない
            return;
        }

        // 撮影中の更新処理
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

    /**
     *
     *
     */
    @Override
    public void onCompleted()
    {
        Log.v(TAG, "SequentialShotControl::onCompleted()");
        indicator.onShootingStatusUpdate(currentStatus);
        if (currentStatus != IIndicatorControl.shootingStatus.Stopping)
        {
            // 撮影停止中以外ではなにもしない。
            return;
        }

        // 撮影停止処理...
        try
        {
            camera.clearAutoFocusPoint();
            hideFocusFrame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        currentStatus = IIndicatorControl.shootingStatus.Unknown;
        indicator.onShootingStatusUpdate(currentStatus);
    }

    /**
     *
     *
     */
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
        currentStatus = IIndicatorControl.shootingStatus.Unknown;
    }

    /**
     *
     *
     */
    private void showFocusFrame(RectF rect, IAutoFocusFrameDisplay.FocusFrameStatus status, double duration)
    {
        if (frameDisplayer != null)
        {
            frameDisplayer.showFocusFrame(rect, status, duration);
        }
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused == status);
    }

    /**
     *
     *
     */
    private void hideFocusFrame()
    {
        if (frameDisplayer != null)
        {
            frameDisplayer.hideFocusFrame();
        }
        indicator.onAfLockUpdate(false);
    }
}
