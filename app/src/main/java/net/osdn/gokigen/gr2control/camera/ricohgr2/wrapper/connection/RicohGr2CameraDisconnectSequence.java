package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.connection;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;


public class RicohGr2CameraDisconnectSequence  implements Runnable
{
    private final String TAG = this.toString();
    private final Activity activity;
    private final boolean powerOff;

    RicohGr2CameraDisconnectSequence(Activity activity, boolean isOff)
    {
        this.activity = activity;
        this.powerOff = isOff;
    }

    @Override
    public void run()
    {
        // カメラをPowerOffして接続を切る
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            if (preferences.getBoolean(IPreferencePropertyAccessor.GR2_LCD_SLEEP, false))
            {
                final String screenOnUrl = "http://192.168.0.1/_gr";
                final String postData = "lcd sleep off";
                final int TIMEOUT_MS = 5000;
                String response = SimpleHttpClient.httpPost(screenOnUrl, postData, TIMEOUT_MS);
                Log.v(TAG, screenOnUrl + " " + response);
            }

            if (powerOff)
            {
                final String cameraPowerOffUrl = "http://192.168.0.1/v1/device/finish";
                final String postData = "";
                final int TIMEOUT_MS = 5000;
                String response = SimpleHttpClient.httpPost(cameraPowerOffUrl, postData, TIMEOUT_MS);
                Log.v(TAG, cameraPowerOffUrl + " " + response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
