package net.osdn.gokigen.gr2control.camera.olympus.wrapper.property;

import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *   カメラプロパティをやり取りするクラス (Wrapperクラス)
 */
public class OlyCameraPropertyProxy implements IOlyCameraPropertyProvider
{
    private final String TAG = toString();
    private final OLYCamera camera;

    /**
     *   コンストラクタ
     *
     * @param camera OLYCameraクラス
     */
    public OlyCameraPropertyProxy(OLYCamera camera)
    {
        this.camera = camera;
    }

    /**
     *  フォーカス状態を知る（MF or AF）
     * @return true : MF / false : AF
     */
    boolean isManualFocus()
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
     *  AE ロック状態を知る
     *
     * @return true : AE Lock / false : AE Unlock
     */
    boolean isExposureLocked()
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

    @Override
    public Set<String> getCameraPropertyNames()
    {
        try
        {
            return (camera.getCameraPropertyNames());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyValue(String name)
    {
        try
        {
            return (camera.getCameraPropertyValue(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public Map<String, String> getCameraPropertyValues(Set<String> names)
    {
        try
        {
            return (camera.getCameraPropertyValues(names));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyTitle(String name)
    {
        try
        {
            return (camera.getCameraPropertyTitle(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public List<String> getCameraPropertyValueList(String name)
    {
        try
        {
            return (camera.getCameraPropertyValueList(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyValueTitle(String propertyValue)
    {
        try
        {
            return (camera.getCameraPropertyValueTitle(propertyValue));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public void setCameraPropertyValue(String name, String value)
    {
        try
        {
            camera.setCameraPropertyValue(name, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setCameraPropertyValues(Map<String, String> values)
    {
        try
        {
            camera.setCameraPropertyValues(values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canSetCameraProperty(String name)
    {
        try
        {
            return (camera.canSetCameraProperty(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }
}
