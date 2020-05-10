package net.osdn.gokigen.gr2control.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.RicohGr2CameraPowerOff;
import net.osdn.gokigen.gr2control.logcat.LogCatViewer;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class Gr2ControlPreferenceFragment  extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener
{
    private final String TAG = toString();
    //private RicohGr2CameraPowerOff powerOffController = null;
    private IChangeScene changeScene = null;
    private AppCompatActivity context = null;
    private SharedPreferences preferences = null;
    private LogCatViewer logCatViewer = null;

    /**
     *
     *
     */
    public static Gr2ControlPreferenceFragment newInstance(@NonNull AppCompatActivity context, @NonNull IChangeScene changeScene)
    {
        Gr2ControlPreferenceFragment instance = new Gr2ControlPreferenceFragment();
        instance.prepare(context, changeScene);

        // パラメータはBundleにまとめておく
        Bundle arguments = new Bundle();
        //arguments.putString("title", title);
        //arguments.putString("message", message);
        instance.setArguments(arguments);

        return (instance);
    }

    /**
     *
     *
     */
    private void prepare(@NonNull AppCompatActivity context, @NonNull IChangeScene changeScene)
    {
        try
        {
            //powerOffController = new RicohGr2CameraPowerOff(context, changeScene);
            //powerOffController.prepare();

            logCatViewer = new LogCatViewer(changeScene);
            logCatViewer.prepare();
            this.changeScene = changeScene;
            this.context = context;
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
    public void onAttach(@NonNull Context activity)
    {
        super.onAttach(activity);
        Log.v(TAG, " onAttach()");
        try
        {
            // Preference をつかまえる
            preferences = PreferenceManager.getDefaultSharedPreferences(activity);

            // Preference を初期設定する
            initializePreferences();

            preferences.registerOnSharedPreferenceChangeListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Preferenceの初期化...
     *
     */
    private void initializePreferences()
    {
        try
        {
            Map<String, ?> items = preferences.getAll();
            SharedPreferences.Editor editor = preferences.edit();

            if (!items.containsKey(IPreferencePropertyAccessor.RICOH_GET_PICS_LIST_TIMEOUT))
            {
                editor.putString(IPreferencePropertyAccessor.RICOH_GET_PICS_LIST_TIMEOUT, IPreferencePropertyAccessor.RICOH_GET_PICS_LIST_TIMEOUT_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.LIVE_VIEW_QUALITY))
            {
                editor.putString(IPreferencePropertyAccessor.LIVE_VIEW_QUALITY, IPreferencePropertyAccessor.LIVE_VIEW_QUALITY_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL))
            {
                editor.putString(IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL, IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.RAW))
            {
                editor.putBoolean(IPreferencePropertyAccessor.RAW, true);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA))
            {
                editor.putBoolean(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, true);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW))
            {
                editor.putBoolean(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, false);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.CONNECTION_METHOD))
            {
                editor.putString(IPreferencePropertyAccessor.CONNECTION_METHOD, IPreferencePropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.SHARE_AFTER_SAVE)) {
                editor.putBoolean(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.USE_PLAYBACK_MENU)) {
                editor.putBoolean(IPreferencePropertyAccessor.USE_PLAYBACK_MENU, true);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.GR2_DISPLAY_CAMERA_VIEW)) {
                editor.putBoolean(IPreferencePropertyAccessor.GR2_DISPLAY_CAMERA_VIEW, true);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.GR2_LCD_SLEEP)) {
                editor.putBoolean(IPreferencePropertyAccessor.GR2_LCD_SLEEP, false);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.USE_GR2_SPECIAL_COMMAND)) {
                editor.putBoolean(IPreferencePropertyAccessor.USE_GR2_SPECIAL_COMMAND, true);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PENTAX_CAPTURE_AFTER_AF)) {
                editor.putBoolean(IPreferencePropertyAccessor.PENTAX_CAPTURE_AFTER_AF, false);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.FUJI_X_DISPLAY_CAMERA_VIEW)) {
                editor.putBoolean(IPreferencePropertyAccessor.FUJI_X_DISPLAY_CAMERA_VIEW, false);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.FUJI_X_FOCUS_XY)) {
                editor.putString(IPreferencePropertyAccessor.FUJI_X_FOCUS_XY, IPreferencePropertyAccessor.FUJI_X_FOCUS_XY_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.FUJI_X_LIVEVIEW_WAIT)) {
                editor.putString(IPreferencePropertyAccessor.FUJI_X_LIVEVIEW_WAIT, IPreferencePropertyAccessor.FUJI_X_LIVEVIEW_WAIT_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.FUJI_X_COMMAND_POLLING_WAIT)) {
                editor.putString(IPreferencePropertyAccessor.FUJI_X_COMMAND_POLLING_WAIT, IPreferencePropertyAccessor.FUJI_X_COMMAND_POLLING_WAIT_DEFAULT_VALUE);
            }
            if (!items.containsKey(IPreferencePropertyAccessor.FUJI_X_GET_SCREENNAIL_AS_SMALL_PICTURE)) {
                editor.putBoolean(IPreferencePropertyAccessor.FUJI_X_GET_SCREENNAIL_AS_SMALL_PICTURE, false);
            }
            editor.apply();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.v(TAG, " onSharedPreferenceChanged() : " + key);
        boolean value;
        if (key != null)
        {
            switch (key)
            {
                case IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA:
                    value = preferences.getBoolean(key, true);
                    Log.v(TAG, " " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW:
                    value = preferences.getBoolean(key, false);
                    Log.v(TAG, " BOTH LV : " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.USE_PLAYBACK_MENU:
                    value = preferences.getBoolean(key, true);
                    Log.v(TAG, " PLAYBACK MENU : " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.SHARE_AFTER_SAVE:
                    value = preferences.getBoolean(key, false);
                    Log.v(TAG, " SHARE : " + key + " , " + value);
                    break;

                default:
                    String strValue = preferences.getString(key, "");
                    setListPreference(key, key, strValue);
                    break;
            }
        }
    }

    /**
     *
     *
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        Log.v(TAG, " onCreatePreferences()");
        try
        {
            //super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_summary);

            ListPreference connectionMethod = findPreference(IPreferencePropertyAccessor.CONNECTION_METHOD);
            if (connectionMethod != null)
            {
                connectionMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(newValue + " ");
                        return (true);
                    }
                });
                connectionMethod.setSummary(connectionMethod.getValue() + " ");
            }

            /*
            Preference exitApplication = findPreference("exit_application");
            if (exitApplication != null)
            {
                exitApplication.setOnPreferenceClickListener(powerOffController);
            }
            */

            Preference opcPreference = findPreference("opc_settings");
            if (opcPreference != null)
            {
                opcPreference.setOnPreferenceClickListener(this);
            }
            Preference gr2Preference = findPreference("ricoh_settings");
            if (gr2Preference != null)
            {
                gr2Preference.setOnPreferenceClickListener(this);
            }
            Preference fujiXPreference = findPreference("fuji_x_settings");
            if (fujiXPreference != null)
            {
                fujiXPreference.setOnPreferenceClickListener(this);
            }

            Preference debug = findPreference("debug_info");
            if (debug != null)
            {
                debug.setOnPreferenceClickListener(logCatViewer);
            }

            Preference wifi = findPreference("wifi_settings");
            if (wifi != null)
            {
                wifi.setOnPreferenceClickListener(this);
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
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, " onResume() Start");
        try
        {
            synchronizedProperty();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.v(TAG, " onResume() End");
    }

    /**
     *
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, " onPause() Start");
        try
        {
            // Preference変更のリスナを解除
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.v(TAG, " onPause() End");
    }

    /**
     * ListPreference の表示データを設定
     *
     * @param pref_key     Preference(表示)のキー
     * @param key          Preference(データ)のキー
     * @param defaultValue Preferenceのデフォルト値
     */
    private void setListPreference(String pref_key, String key, String defaultValue)
    {
        try
        {
            ListPreference pref;
            pref = findPreference(pref_key);
            String value = preferences.getString(key, defaultValue);
            if (pref != null)
            {
                pref.setValue(value);
                pref.setSummary(value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * BooleanPreference の表示データを設定
     *
     * @param pref_key     Preference(表示)のキー
     * @param key          Preference(データ)のキー
     * @param defaultValue Preferenceのデフォルト値
     */
    private void setBooleanPreference(String pref_key, String key, boolean defaultValue)
    {
        try
        {
            CheckBoxPreference pref = findPreference(pref_key);
            if (pref != null)
            {
                boolean value = preferences.getBoolean(key, defaultValue);
                pref.setChecked(value);
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
    private void synchronizedProperty()
    {
        final FragmentActivity activity = getActivity();
        if (activity != null)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        // Preferenceの画面に反映させる
                        setBooleanPreference(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, true);
                        setBooleanPreference(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, false);
                        setBooleanPreference(IPreferencePropertyAccessor.USE_PLAYBACK_MENU, IPreferencePropertyAccessor.USE_PLAYBACK_MENU, true);
                        setBooleanPreference(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        try
        {
            String preferenceKey = preference.getKey();
            Log.v(TAG, " onPreferenceClick() : " + preferenceKey);
            if (preferenceKey.contains("wifi_settings"))
            {
                // Wifi 設定画面を表示する
                Log.v(TAG, " onPreferenceClick : " + preferenceKey);
                if (context != null)
                {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }
            else if (preferenceKey.contains("ricoh_settings"))
            {
                changeScene.changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod.RICOH_GR2);
            }
            else if (preferenceKey.contains("fuji_x_settings"))
            {
                changeScene.changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod.FUJI_X);
            }
            else if (preferenceKey.contains("opc_settings"))
            {
                changeScene.changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod.OPC);
            }
            else
            {
                return (false);
            }
            return (true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }
}
