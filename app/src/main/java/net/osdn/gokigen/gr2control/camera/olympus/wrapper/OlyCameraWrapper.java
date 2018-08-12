package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.CameraPropertyUtilities;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.IImageDataReceiver;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;

/**
 *
 *
 */
class OlyCameraWrapper implements ICameraRunMode, ILiveViewControl, ILiveViewListener, OLYCameraLiveViewListener
{
    private final String TAG = toString();
    private final Activity context;
    private final OLYCamera camera;
    private IImageDataReceiver dataReceiver = null;

    /**
     *
     *
     */
    OlyCameraWrapper(Activity context)
    {
        this.context = context;
        camera = new OLYCamera();
        camera.setContext(context.getApplicationContext());
    }

    /**
     *
     *
     */
    OLYCamera getOLYCamera()
    {
        return (camera);
    }

    /**
     *   ICameraRunMode の実装
     *
     */
    @Override
    public void changeRunMode(boolean isRecording)
    {
        OLYCamera.RunMode runMode = (isRecording) ? OLYCamera.RunMode.Recording : OLYCamera.RunMode.Playback;
        Log.v(TAG, "changeRunMode() : " + runMode);
        try
        {
            camera.changeRunMode(runMode);
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
    public boolean isRecordingMode()
    {
        boolean isRecordingMode = false;
        try
        {
            OLYCamera.RunMode runMode = camera.getRunMode();
            isRecordingMode =  (runMode == OLYCamera.RunMode.Recording);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (isRecordingMode);
    }


    /**
     *   ILiveViewControl の実装
     *
     */
    @Override
    public void changeLiveViewSize(String size)
    {
        try
        {
            camera.changeLiveViewSize(CameraPropertyUtilities.toLiveViewSizeType(size));
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
    public void startLiveView(boolean isCameraScreen)
    {
        Log.v(TAG, "startLiveView() : " + isCameraScreen);
        try
        {
            camera.startLiveView();
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
    public void stopLiveView()
    {
        try
        {
            camera.stopLiveView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューを拡大する
     *
     */
    @Override
    public void updateDigitalZoom()
    {
        try
        {
            float scale = getDigitalZoomScalePreference();
            camera.changeDigitalZoomScale(scale);
            Log.v(TAG, "DIGITAL ZOOM SCALE : " + scale);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューを拡大する
     *
     */
    @Override
    public void updateMagnifyingLiveViewScale(boolean isChangeScale)
    {
       try
       {
           updateMagnifyingLiveViewScale(getMagnifyingLiveViewScale(isChangeScale));
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }

    /**
     *   ライブビューのサイズ
     *
     * @return  ライブビュー倍率
     */
    @Override
    public float getMagnifyingLiveViewScale()
    {
        return (getMagnifyingLiveViewScale(false));
    }

    /**
     *   デジタルズームの倍率を取得する
     *
     */
    @Override
    public float getDigitalZoomScale()
    {
        return (getDigitalZoomScalePreference());
    }

    /**
     *
     *
     */
    private void updateMagnifyingLiveViewScale(float scale)
    {
        try
        {
            if (scale < 5.0f)
            {
                if (camera.isMagnifyingLiveView())
                {
                    camera.stopMagnifyingLiveView();
                    Log.v(TAG, "RESET LIVE VIEW SCALE : " + 1.0f);
                }
                return;
            }
            OLYCamera.MagnifyingLiveViewScale setScale;
            if (scale >= 14.0f)
            {
                setScale = OLYCamera.MagnifyingLiveViewScale.X14;
            }
            else if (scale >= 10.0f)
            {
                setScale = OLYCamera.MagnifyingLiveViewScale.X10;
            }
            else if (scale >= 7.0f)
            {
                setScale = OLYCamera.MagnifyingLiveViewScale.X7;
            }
            else // if (scale >= 5.0f)
            {
                setScale = OLYCamera.MagnifyingLiveViewScale.X5;
            }
            changeMagnifyingLiveView(setScale);
            Log.v(TAG, "SET LIVE VIEW SCALE : " + scale);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの中心を拡大する
     *
     */
    private void changeMagnifyingLiveView(OLYCamera.MagnifyingLiveViewScale setScale)
    {
        try
        {
            PointF centerPoint = new PointF(0.5f, 0.5f);  // 中心座標
            if (camera.isMagnifyingLiveView())
            {
                camera.changeMagnifyingLiveViewScale(setScale);
            }
            else
            {
                camera.startMagnifyingLiveViewAtPoint(centerPoint, setScale);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビュー拡大倍率の設定値を応答する
     *
     */
    private float getMagnifyingLiveViewScale(boolean isChangeScale)
    {
        float scale;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(IPreferencePropertyAccessor.MAGNIFYING_LIVE_VIEW_SCALE, IPreferencePropertyAccessor.MAGNIFYING_LIVE_VIEW_SCALE_DEFAULT_VALUE);
        try
        {
            scale = Float.parseFloat(value);
            String changeValue = null;
            if(isChangeScale)
            {
                if (scale >= 14.0f)
                {
                    scale = 1.0f;
                    changeValue = "1.0";
                }
                else if (scale >= 10.0f)
                {
                    scale = 14.0f;
                    changeValue = "14.0";
                }
                else if (scale >= 7.0f)
                {
                    scale = 10.0f;
                    changeValue = "10.0";
                }
                else if (scale >= 5.0f)
                {
                    scale = 7.0f;
                    changeValue = "7.0";
                }
                else // if (scale < 5.0f)
                {
                    scale = 5.0f;
                    changeValue = "5.0";
                }
            }
            if (changeValue != null)
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(IPreferencePropertyAccessor.MAGNIFYING_LIVE_VIEW_SCALE, changeValue);
                editor.apply();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            scale = 1.0f;
        }
        return (scale);
    }

    /**
     *   デジタルズーム倍率の設定値を応答する
     *
     */
    private float getDigitalZoomScalePreference()
    {
        float scale;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(IPreferencePropertyAccessor.DIGITAL_ZOOM_LEVEL, IPreferencePropertyAccessor.DIGITAL_ZOOM_LEVEL_DEFAULT_VALUE);
        try
        {
            Map<String, Float> range = camera.getDigitalZoomScaleRange();
            float max = range.get(OLYCamera.DIGITAL_ZOOM_SCALE_RANGE_MAXIMUM_KEY);
            float min = range.get(OLYCamera.DIGITAL_ZOOM_SCALE_RANGE_MINIMUM_KEY);

            if (value.equals("MAX"))
            {
                scale = max;
            }
            else
            {
                scale = Float.parseFloat(value);
            }
            if (scale < min)
            {
                scale = min;
            }
            else if (scale > max)
            {
                scale = max;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            scale = 1.0f;
        }
        return (scale);
    }

    /**
     *
     *
     */
    @Override
    public void setCameraLiveImageView(@NonNull IImageDataReceiver target)
    {
        try
        {
            dataReceiver = target;
            if (camera != null)
            {
                camera.setLiveViewListener(this);
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
    public void onUpdateLiveView(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map)
    {
        try
        {
            if (dataReceiver != null)
            {
                dataReceiver.setImageData(bytes, map);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
