package net.osdn.gokigen.gr2control.logcat;

import android.support.v7.preference.Preference;
import android.util.Log;

import net.osdn.gokigen.gr2control.scene.IChangeScene;


public class LogCatViewer implements android.support.v7.preference.Preference.OnPreferenceClickListener
{
    private final String TAG = toString();
    private final IChangeScene changeScene;
    //private String preferenceKey = null;

    public LogCatViewer(IChangeScene changeScene)
    {
        this.changeScene = changeScene;
    }

    public void prepare()
    {
        Log.v(TAG, "prepare() ");
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (!preference.hasKey())
        {
            return (false);
        }

        String preferenceKey = preference.getKey();
        if ((preferenceKey.contains("debug_info"))&&(changeScene != null))
        {
            try
            {
                // デバッグ情報を表示する
                changeScene.changeSceneToDebugInformation();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return (true);
        }
        return (false);
    }
}
