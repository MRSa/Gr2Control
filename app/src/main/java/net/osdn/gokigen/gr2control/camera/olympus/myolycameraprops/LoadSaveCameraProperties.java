package net.osdn.gokigen.gr2control.camera.olympus.myolycameraprops;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.olympus.IOlympusInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraProperty;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *   カメラプロパティを一括でバックアップしたり、リストアしたりするクラス
 *
 */
public class LoadSaveCameraProperties implements ILoadSaveCameraProperties
{
    private final String TAG = toString();

    private final Activity parent;
    private final ICameraConnection cameraConnection;
    private final IOlyCameraPropertyProvider propertyProvider;

    public LoadSaveCameraProperties(Activity context, IOlympusInterfaceProvider interfaceProvider)
    {
        this.parent = context;
        this.cameraConnection = interfaceProvider.getOlyCameraConnection();
        this.propertyProvider = interfaceProvider.getCameraPropertyProvider();
    }

    /**
     *   カメラの現在の設定を本体から読みだして記憶する
     *
     */
    @Override
    public void saveCameraSettings(final String idHeader, final String dataName)
    {
        Log.v(TAG, "saveCameraSettings() : START [" + idHeader + "], dataName: " + dataName);

        // カメラから設定を一括で読みだして、Preferenceに記録する
        if (cameraConnection.getConnectionStatus() == ICameraConnection.CameraConnectionStatus.CONNECTED)
        {
            //
            // BUSYダイアログを表示する
            //
            final ProgressDialog busyDialog = new ProgressDialog(parent);
            busyDialog.setMessage(parent.getString(R.string.dialog_start_save_property_message));
            busyDialog.setTitle(parent.getString(R.string.dialog_start_save_property_title));
            busyDialog.setIndeterminate(false);
            busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            busyDialog.show();

            try
            {
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final boolean toast = saveCameraSettingsImpl(idHeader, dataName);
                        busyDialog.dismiss();

                        parent.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // Toast で保存したよのメッセージを表示
                                if (toast)
                                {
                                    String storedMessage = parent.getString(R.string.saved_my_props) + dataName;
                                    Toast.makeText(parent, storedMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                thread.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Log.v(TAG, "PROPERTY STORED : " + idHeader + " " + dataName);
        }
    }

    /**
     *   Preferenceにあるカメラの設定をカメラに登録する
     *　(注： Read Onlyなパラメータを登録しようとするとエラーになるので注意）
     */
    @Override
    public void loadCameraSettings(final String idHeader, final String dataName)
    {
        Log.v(TAG, "loadCameraSettings() : START [" + idHeader + "], dataName: " + dataName);
        if (cameraConnection.getConnectionStatus() == ICameraConnection.CameraConnectionStatus.CONNECTED)
        {
            //Log.v(TAG, "PROPERTY RESTORE ENTER : (" + id + ") " + name);

            //
            // BUSYダイアログを表示する
            //
            final ProgressDialog busyDialog = new ProgressDialog(parent);
            busyDialog.setMessage(parent.getString(R.string.dialog_start_load_property_message));
            busyDialog.setTitle(parent.getString(R.string.dialog_start_load_property_title));
            busyDialog.setIndeterminate(false);
            busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            busyDialog.show();

            try
            {
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //final boolean toast = loadCameraSettingsSequential(idHeader);
                        final boolean toast = loadCameraSettingsOnlyDifferences(idHeader);
                        busyDialog.dismiss();

                        parent.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // Toast で展開したよのメッセージを表示
                                if (toast)
                                {
                                    String restoredMessage = parent.getString(R.string.restored_my_props) + dataName;
                                    Toast.makeText(parent, restoredMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                thread.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //Log.v(TAG, "PROPERTY RESTORE EXIT : (" + id + ") " + name);
        }
    }

    /**
     *   カメラのプロパティを１つづつ個別設定
     *
     */
    private boolean saveCameraSettingsImpl(String idHeader, String dataName)
    {
        boolean ret = false;
        Map<String, String> values;
        try
        {
            values = propertyProvider.getCameraPropertyValues(propertyProvider.getCameraPropertyNames());
            if (values != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                SharedPreferences.Editor editor = preferences.edit();
                for (String key : values.keySet())
                {
                    editor.putString(idHeader + key, values.get(key));
                    //Log.v(TAG, "storeCameraSettings(): " + idHeader + key + " , " + values.get(key));
                }
                DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                editor.putString(idHeader + DATE_KEY, dateFormat.format(new Date()));
                editor.putString(idHeader + TITLE_KEY, dataName);
                //editor.commit();
                editor.apply();

                ret = true;
                Log.v(TAG, "storeCameraSettings() COMMITED : " + idHeader + " [" + dataName + "]");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = false;
        }
        return (ret);
        //Log.v(TAG, "CameraPropertyBackupRestore::storeCameraSettings() : " + idHeader);
    }

    /**
     *   カメラのプロパティを１つづつ個別設定
     *
     */
    private boolean loadCameraSettingsSequential(String idHeader)
    {
        boolean ret = false;
        int setCount = 0;
        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (cameraConnection.getConnectionStatus() == ICameraConnection.CameraConnectionStatus.CONNECTED)
        {
            String takeModeValue = preferences.getString(idHeader + IOlyCameraProperty.TAKE_MODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    propertyProvider.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettings() TAKEMODE : " + takeModeValue);
                    setCount++;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : loadCameraSettingsSequential() fail...");
            }

            Set<String> names = propertyProvider.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        try
                        {
                            // カメラプロパティを個別登録（全パラメータを一括登録すると何か落ちている
                            Log.v(TAG, "loadCameraSettingsSequential(): " + value);
                            propertyProvider.setCameraPropertyValue(name, value);
                            setCount++;
                            //Thread.sleep(5);   //　処理落ちしている？かもしれないので必要なら止める
                            ret = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            ret = false;
                        }
                    }
                }
            }
            Log.v(TAG, "loadCameraSettingsSequential() : END [" + idHeader + "]" + " " + setCount);
        }
        return (ret);
    }


    /**
     *   カメラのプロパティを１つづつ個別設定（違っているものだけ設定する）
     *
     */
    private boolean loadCameraSettingsOnlyDifferences(String idHeader)
    {
        boolean ret = false;
        int setCount = 0;

        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (cameraConnection.getConnectionStatus() == ICameraConnection.CameraConnectionStatus.CONNECTED)
        {

            //  現在の設定値を全部とってくる
            Map<String, String> propertyValues;
            try
            {
                propertyValues = propertyProvider.getCameraPropertyValues(propertyProvider.getCameraPropertyNames());
            }
            catch (Exception e)
            {
                // 設定値が取得できなかった場合は、終了する。
                e.printStackTrace();
                return (false);
            }
            if (propertyValues == null)
            {
                // プロパティの取得が失敗していたら、何もせずに折り返す
                return (false);
            }

            String takeModeValue = preferences.getString(idHeader + IOlyCameraProperty.TAKE_MODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    propertyProvider.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettingsOnlyDifferences() TAKEMODE : " + takeModeValue);
                    setCount++;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : loadCameraSettingsOnlyDifferences() fail...");
            }

            Set<String> names = propertyProvider.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                String currentValue = propertyValues.get(name);
                if ((value != null)&&(currentValue != null)&&(!value.equals(currentValue)))
                //if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        try
                        {
                            // カメラプロパティを個別登録（全パラメータを一括登録すると何か落ちている
                            Log.v(TAG, "loadCameraSettingsOnlyDifferences(): SET : " + value);
                            propertyProvider.setCameraPropertyValue(name, value);
                            setCount++;
                            //Thread.sleep(5);   //　処理落ちしている？かもしれないので必要なら止める
                            ret = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            ret = false;
                        }
                    }
                }
            }
            Log.v(TAG, "loadCameraSettingsOnlyDifferences() : END [" + idHeader + "]" + " " + setCount);
        }
        return (ret);
    }
}
