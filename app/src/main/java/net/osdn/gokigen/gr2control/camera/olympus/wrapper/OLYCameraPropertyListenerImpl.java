package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraProperty;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;

/**
 *
 *
 */
public class OLYCameraPropertyListenerImpl implements OLYCameraPropertyListener, ICameraInformation
{
    private final String TAG = toString();
    private final OLYCamera camera;
    private IFocusingModeNotify focusCallback = null;

    /**
     *
     *
     */
    OLYCameraPropertyListenerImpl(OLYCamera olyCamera)
    {
        olyCamera.setCameraPropertyListener(this);
        this.camera = olyCamera;
    }

    /**
     *
     *
     */
    public void setFocusingControl(IFocusingModeNotify focusCallback)
    {
        this.focusCallback = focusCallback;
    }

    /**
     *
     *
     */
    @Override
    public void onUpdateCameraProperty(final OLYCamera olyCamera, final String name)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (name)
                {
                    case IOlyCameraProperty.FOCUS_STILL:
                        //
                        Log.v(TAG, "onUpdateCameraProperty() : " + name);
                        if (focusCallback != null)
                        {
                            focusCallback.changedFocusingMode();
                        }
                        break;

                    default:
                        //
                        break;
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
    }

    /**
     *  フォーカス状態を知る（MF or AF）
     * @return true : MF / false : AF
     */
    @Override
    public boolean isManualFocus()
    {
        boolean isManualFocus = false;
        try
        {
            String value = camera.getCameraPropertyValue(IOlyCameraProperty.FOCUS_STILL);
            Log.v(TAG, "OlyCameraPropertyProxy::isManualFocus() " + value);
            isManualFocus = !(value.contains("AF"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (isManualFocus);
    }

    /**
     * 電動ズーム機能を持つレンズが装着されているか確認
     *
     * @return true ; 電動ズーム付き / false : 電動ズームなし
     */
    @Override
    public boolean isElectricZoomLens()
    {
        return ((camera != null)&&(camera.getLensMountStatus()).contains("electriczoom"));
    }

    /**
     *  AE ロック状態を知る
     *
     * @return true : AE Lock / false : AE Unlock
     */
    @Override
    public boolean isExposureLocked()
    {
        boolean isExposureLocked =false;
        try
        {
            String value = camera.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE);
            Log.v(TAG, "OlyCameraPropertyProxy::isExposureLocked() " + value);
            isExposureLocked = !(value.contains("UNLOCK"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (isExposureLocked);
    }
}
