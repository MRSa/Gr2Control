package net.osdn.gokigen.gr2control.preference.olympus;

import android.content.SharedPreferences;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.CameraPropertyUtilities;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraProperty;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;


class PreferenceSynchronizer implements Runnable
{
    private final String TAG = toString();
    private final IOlyCameraPropertyProvider propertyInterface;
    private final SharedPreferences preference;
    private final IPropertySynchronizeCallback callback;

    PreferenceSynchronizer(IOlyCameraPropertyProvider propertyInterface, SharedPreferences preference, IPropertySynchronizeCallback callback)
    {
        this.propertyInterface = propertyInterface;
        this.preference = preference;
        this.callback = callback;
    }

    private String getPropertyValue(String key)
    {
        String propertyValue;
        try
        {
            String value = propertyInterface.getCameraPropertyValue(key);
            propertyValue = CameraPropertyUtilities.getPropertyValue(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            propertyValue = "";
        }
        Log.v(TAG, "getPropertyValue(" + key + ") : " + propertyValue);
        return (propertyValue);
    }

    @Override
    public void run()
    {
        Log.v(TAG, "run()");
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL, getPropertyValue(IOlyCameraProperty.SOUND_VOLUME_LEVEL));
        boolean value = getPropertyValue(IOlyCameraProperty.RAW).equals("ON");
        editor.putBoolean(IPreferencePropertyAccessor.RAW, value);
        editor.apply();
        if (callback != null)
        {
            callback.synchronizedProperty();
        }
    }

    interface IPropertySynchronizeCallback
    {
        void synchronizedProperty();
    }
}
