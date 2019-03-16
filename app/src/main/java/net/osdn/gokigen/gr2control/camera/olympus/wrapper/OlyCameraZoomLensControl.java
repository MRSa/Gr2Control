package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

import androidx.preference.PreferenceManager;
import jp.co.olympus.camerakit.OLYCamera;

/**
 *   ズームレンズの情報を保持する
 *
 */
class OlyCameraZoomLensControl implements IZoomLensControl
{
    private final String TAG = toString();
    private boolean canZoom = false;
    private float minimumLength = 0.0f;
    private float maximumLength = 0.0f;
    private float currentLength = 0.0f;

    private final Context context;
    private final OLYCamera camera;

    OlyCameraZoomLensControl(Context context, OLYCamera camera)
    {
        this.context = context;
        this.camera = camera;
        initialize();
    }

    private void initialize()
    {
        if (camera != null)
        {
            String mountStatus = camera.getLensMountStatus();
            //Log.v(TAG, "OlyCameraZoomLensControl() : " + mountStatus);
            canZoom = ((mountStatus != null)&&(mountStatus.contains("electriczoom")));
            if (mountStatus != null)
            {
                try
                {
                    minimumLength = camera.getMinimumFocalLength();
                    maximumLength = camera.getMaximumFocalLength();
                    currentLength = camera.getActualFocalLength();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean canZoom()
    {
        initialize();
        return (canZoom);
    }

    @Override
    public void updateStatus()
    {
        initialize();
    }

    @Override
    public float getMaximumFocalLength()
    {
        return (maximumLength);
    }

    @Override
    public float getMinimumFocalLength()
    {
        return (minimumLength);
    }

    @Override
    public float getCurrentFocalLength()
    {
        return (currentLength);
    }

    /**
     * ズームレンズを動作させる
     *
     * @param targetLength  焦点距離
     */
    @Override
    public void driveZoomLens(float targetLength)
    {
        try
        {
            // 現在位置を設定する
            initialize();

            // レンズがサポートする焦点距離と、現在の焦点距離を取得する
            float targetFocalLength = targetLength;

            // 焦点距離が最大値・最小値を超えないようにする
            if (targetFocalLength > maximumLength)
            {
                targetFocalLength = maximumLength;
            }
            if (targetFocalLength < minimumLength)
            {
                targetFocalLength = minimumLength;
            }

            // レンズのスーム操作
            Log.v(TAG, "ZOOM from " + currentLength + "mm to " + targetFocalLength + "mm");

            // ズーム動作中でない時には、レンズをズームさせる
            if (!camera.isDrivingZoomLens())
            {
                camera.startDrivingZoomLensToFocalLength(targetFocalLength);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ズームレンズを動作させる
     *
     * @param isZoomIn  寄る方向に動かす場合は true
     */
    @Override
    public void driveZoomLens(boolean isZoomIn)
    {
        try
        {
            // 現在位置を設定する
            initialize();
            float targetFocalLength = currentLength;
            if (isZoomIn)
            {
                // 寄る
                targetFocalLength = targetFocalLength * 1.15f;
            }
            else
            {
                // 引く
                targetFocalLength = targetFocalLength * 0.9f;
            }

            // 焦点距離が最大値・最小値を超えないようにする
            if (targetFocalLength > maximumLength)
            {
                targetFocalLength = maximumLength;
            }
            if (targetFocalLength < minimumLength)
            {
                targetFocalLength = minimumLength;
            }

            // レンズのスーム操作
            Log.v(TAG, "ZOOM from " + currentLength + "mm to " + targetFocalLength + "mm");

            // ズーム動作中でない時には、レンズをズームさせる
            if (!camera.isDrivingZoomLens())
            {
                camera.startDrivingZoomLensToFocalLength(targetFocalLength);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  ズームレンズの初期位置に移動させる
     */
    @Override
    public void moveInitialZoomPosition()
    {
        initialize();
        if (!canZoom())
        {
            // ズームできない場合、何もせずに応答する
            Log.v(TAG, "moveInitialZoomPosition() : not PZ lens.");
            return;
        }

        float scale;
        float focalLength;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(IPreferencePropertyAccessor.POWER_ZOOM_LEVEL, IPreferencePropertyAccessor.POWER_ZOOM_LEVEL_DEFAULT_VALUE);
        try
        {
            scale = Float.parseFloat(value);
            if (scale == 0.0f)
            {
                focalLength = minimumLength;
            }
            else if (scale == 1.0f)
            {
                focalLength = maximumLength;
            }
            else
            {
                focalLength = (maximumLength + minimumLength) / scale;
            }
            driveZoomLens(focalLength);
            //Log.v(TAG, "moveInitialZoomPosition() : zoom to " + focalLength + "mm");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 現在ズーム中か確認する
     *
     * @return true : ズーム中  / false : ズーム中でない
     */
    @Override
    public boolean isDrivingZoomLens()
    {
        return  ((camera != null)&&(camera.isDrivingZoomLens()));
    }

}
