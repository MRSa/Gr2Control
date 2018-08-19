package net.osdn.gokigen.gr2control.preference.olympus;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraHardwareStatus;
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.olympus.operation.CameraPowerOff;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraProperty;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;
import net.osdn.gokigen.gr2control.logcat.LogCatViewer;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;


/**
 *   SettingFragment
 *
 */
public class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, PreferenceSynchronizer.IPropertySynchronizeCallback
{
    private final String TAG = toString();
    private IOlyCameraPropertyProvider propertyInterface = null;
    private ICameraHardwareStatus hardwareStatusInterface = null;
    private ICameraRunMode changeRunModeExecutor = null;
    private CameraPowerOff powerOffController = null;
    private LogCatViewer logCatViewer = null;
    private SharedPreferences preferences = null;
    private ProgressDialog busyDialog = null;
    private PreferenceSynchronizer preferenceSynchronizer = null;


    public static PreferenceFragment newInstance(@NonNull AppCompatActivity context, @NonNull IInterfaceProvider factory, @NonNull IChangeScene changeScene)
    {
        PreferenceFragment instance = new PreferenceFragment();
        instance.setInterface(context, factory, changeScene);

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
    private void setInterface(@NonNull AppCompatActivity context, @NonNull IInterfaceProvider factory, @NonNull IChangeScene changeScene)
    {
        Log.v(TAG, "setInterface()");
        this.propertyInterface = factory.getOlympusInterfaceProvider().getCameraPropertyProvider();
        this.changeRunModeExecutor = factory.getCameraRunMode();
        hardwareStatusInterface = factory.getHardwareStatus();
        powerOffController = new CameraPowerOff(context, changeScene);
        powerOffController.prepare();
        logCatViewer = new LogCatViewer(changeScene);
        logCatViewer.prepare();
    }

    /**
     *
     *
     */
    @Override
    public void onAttach(Context activity)
    {
        super.onAttach(activity);
        Log.v(TAG, "onAttach()");

        // Preference をつかまえる
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferenceSynchronizer == null)
        {
            preferenceSynchronizer = new PreferenceSynchronizer(this.propertyInterface, preferences, this);
        }

        // Preference を初期設定する
        initializePreferences();

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Preferenceの初期化...
     */
    private void initializePreferences()
    {
        Map<String, ?> items = preferences.getAll();
        SharedPreferences.Editor editor = preferences.edit();

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
            editor.putBoolean(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, true);
        }
        if (!items.containsKey(IPreferencePropertyAccessor.CONNECTION_METHOD))
        {
            editor.putString(IPreferencePropertyAccessor.CONNECTION_METHOD, IPreferencePropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE);
        }
        if (!items.containsKey(IPreferencePropertyAccessor.SHARE_AFTER_SAVE)) {
            editor.putBoolean(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false);
        }
        if (!items.containsKey(IPreferencePropertyAccessor.USE_PLAYBACK_MENU)) {
            editor.putBoolean(IPreferencePropertyAccessor.USE_PLAYBACK_MENU, false);
        }
        editor.apply();
    }

    /**
     *
     *
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        Log.v(TAG, "onCreatePreferences()");

        //super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_opc);

        {
            final HashMap<String, String> sizeTable = new HashMap<>();
            sizeTable.put("QVGA", "(320x240)");
            sizeTable.put("VGA", "(640x480)");
            sizeTable.put("SVGA", "(800x600)");
            sizeTable.put("XGA", "(1024x768)");
            sizeTable.put("QUAD_VGA", "(1280x960)");

            ListPreference liveViewQuality = (ListPreference) findPreference(IPreferencePropertyAccessor.LIVE_VIEW_QUALITY);
            liveViewQuality.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String key = (String) newValue;
                    preference.setSummary(newValue + " " + sizeTable.get(key));
                    return (true);
                }
            });
            liveViewQuality.setSummary(liveViewQuality.getValue() + " " + sizeTable.get(liveViewQuality.getValue()));

            ListPreference connectionMethod = (ListPreference) findPreference(IPreferencePropertyAccessor.CONNECTION_METHOD);
            connectionMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue + " ");
                    return (true);
                }
            });
            connectionMethod.setSummary(connectionMethod.getValue() + " ");
        }
        findPreference("exit_application").setOnPreferenceClickListener(powerOffController);
        findPreference("debug_info").setOnPreferenceClickListener(logCatViewer);
    }

    /**
     * ハードウェアのサマリ情報を取得し設定する
     */
    private void setHardwareSummary()
    {
        // レンズ状態
        findPreference("lens_status").setSummary(hardwareStatusInterface.getLensMountStatus());

        // メディア状態
        findPreference("media_status").setSummary(hardwareStatusInterface.getMediaMountStatus());

        // 焦点距離
        String focalLength;
        float minLength = hardwareStatusInterface.getMinimumFocalLength();
        float maxLength = hardwareStatusInterface.getMaximumFocalLength();
        float actualLength = hardwareStatusInterface.getActualFocalLength();
        if (minLength == maxLength)
        {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm", actualLength);
        }
        else
        {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm - %3.0fmm (%3.0fmm)", minLength, maxLength, actualLength);
        }
        findPreference("focal_length").setSummary(focalLength);

        // カメラのバージョン
        try
        {
            Map<String, Object> hardwareInformation = hardwareStatusInterface.inquireHardwareInformation();
            findPreference("camera_version").setSummary((String) hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY));

            // 取得した一覧はログに出力する。)
            Log.v(TAG, "- - - - - - - - - -");
            for (Map.Entry<String, Object> entry : hardwareInformation.entrySet())
            {
                String value = (String) entry.getValue();
                Log.v(TAG, entry.getKey() + " : " + value);
            }
            Log.v(TAG, "- - - - - - - - - -");
        }
        catch (Exception e)
        {
            findPreference("camera_version").setSummary("Unknown");
            e.printStackTrace();
        }
    }

    /**
     *
     *
     */
    private void setCameraProperty(String name, String value)
    {
        try
        {
            String propertyValue = "<" + name + "/" + value + ">";
            Log.v(TAG, "setCameraProperty() : " + propertyValue);
            propertyInterface.setCameraPropertyValue(name, propertyValue);
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
        Log.v(TAG, "onResume() Start");

        // 撮影モードかどうかを確認して、撮影モードではなかったら撮影モードに切り替える
        if ((changeRunModeExecutor != null) && (!changeRunModeExecutor.isRecordingMode()))
        {
            // Runモードを切り替える。（でも切り替えると、設定がクリアされてしまう...。
            changeRunModeExecutor.changeRunMode(true);
        }
        synchronizeCameraProperties(true);
        Log.v(TAG, "onResume() End");

    }

    /**
     *
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause() Start");

        // Preference変更のリスナを解除
        preferences.unregisterOnSharedPreferenceChangeListener(this);

        Log.v(TAG, "onPause() End");
    }

    /**
     * カメラプロパティとPreferenceとの同期処理を実行
     */
    private void synchronizeCameraProperties(boolean isPropertyLoad)
    {
        // 実行中ダイアログを取得する
        busyDialog = new ProgressDialog(getActivity());
        busyDialog.setTitle(getString(R.string.dialog_title_loading_properties));
        busyDialog.setMessage(getString(R.string.dialog_message_loading_properties));
        busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        busyDialog.setCancelable(false);
        busyDialog.show();

        // データ読み込み処理（別スレッドで実行）
        if (isPropertyLoad)
        {
            new Thread(preferenceSynchronizer).start();
        }
    }

    /**
     * Preferenceが更新された時に呼び出される処理
     *
     * @param sharedPreferences sharedPreferences
     * @param key               変更されたキー
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.v(TAG, "onSharedPreferenceChanged() : " + key);
        String propertyValue;
        boolean value;
        if (key != null)
        {
            switch (key)
            {
                case IPreferencePropertyAccessor.RAW:
                    value = preferences.getBoolean(key, true);
                    setBooleanPreference(key, key, value);
                    propertyValue = (value) ? "ON" : "OFF";
                    setCameraProperty(IOlyCameraProperty.RAW, propertyValue);
                    break;

                case IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA:
                    value = preferences.getBoolean(key, true);
                    Log.v(TAG, " " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW:
                    value = preferences.getBoolean(key, true);
                    Log.v(TAG, " " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.SHARE_AFTER_SAVE:
                    value = preferences.getBoolean(key, false);
                    Log.v(TAG, " " + key + " , " + value);
                    break;

                case IPreferencePropertyAccessor.USE_PLAYBACK_MENU:
                    value = preferences.getBoolean(key, false);
                    Log.v(TAG, " " + key + " , " + value);
                    break;

                default:
                    String strValue = preferences.getString(key, "");
                    setListPreference(key, key, strValue);
                    String propertyKey = convertKeyFromPreferenceToCameraPropertyKey(key);
                    if (propertyKey != null)
                    {
                        setCameraProperty(propertyKey, strValue);
                    }
                    break;
            }
        }
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
        ListPreference pref;
        pref = (ListPreference) findPreference(pref_key);
        String value = preferences.getString(key, defaultValue);
        if (pref != null)
        {
            pref.setValue(value);
            pref.setSummary(value);
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
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(pref_key);
        if (pref != null)
        {
            boolean value = preferences.getBoolean(key, defaultValue);
            pref.setChecked(value);
        }
    }

    /**
     *
     *
     */
    private String convertKeyFromPreferenceToCameraPropertyKey(String key)
    {
        String target = null;
        if (key == null)
        {
            return (null);
        }
        switch (key)
        {
            case IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL:
                target = IOlyCameraProperty.SOUND_VOLUME_LEVEL;
                break;

            default:
                // target == null
                break;
        }
        return (target);
    }

    /**
     * カメラプロパティの同期処理終了通知
     */
    @Override
    public void synchronizedProperty()
    {
        FragmentActivity activity = getActivity();
        if (activity == null)
        {
            try
            {
                busyDialog.dismiss();
                busyDialog = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Preferenceの画面に反映させる
                    setListPreference(IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL, IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL, IPreferencePropertyAccessor.SOUND_VOLUME_LEVEL_DEFAULT_VALUE);
                    setBooleanPreference(IPreferencePropertyAccessor.RAW, IPreferencePropertyAccessor.RAW, true);
                    setBooleanPreference(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, true);
                    setBooleanPreference(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false);

                    // カメラキットのバージョン
                    findPreference(IPreferencePropertyAccessor.CAMERAKIT_VERSION).setSummary(OLYCamera.getVersion());
                    if (hardwareStatusInterface != null)
                    {
                        // その他のハードウェア情報の情報設定
                        setHardwareSummary();
                    }

                    // 実行中ダイアログを消す
                    busyDialog.dismiss();
                    busyDialog = null;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
