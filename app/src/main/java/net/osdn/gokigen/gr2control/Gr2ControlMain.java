package net.osdn.gokigen.gr2control;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import net.osdn.gokigen.gr2control.camera.CameraInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.liveview.LiveViewFragment;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.CameraSceneUpdater;

/**
 *
 *
 */
public class Gr2ControlMain extends AppCompatActivity
{
    private final String TAG = toString();
    private IInterfaceProvider interfaceProvider = null;
    private CameraSceneUpdater scenceUpdater = null;
    private  LiveViewFragment liveViewFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 画面表示の準備
        setContentView(R.layout.activity_gr2_control_main);
        try
        {
            ActionBar bar = getSupportActionBar();
            if (bar != null)
            {
                // タイトルバーは表示しない
                bar.hide();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 外部メモリアクセス権のオプトイン
        final int REQUEST_NEED_PERMISSIONS = 1010;
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET,
                    },
                    REQUEST_NEED_PERMISSIONS);
        }

        initializeClass();
        onReadyClass();
    }

    /**
     *
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String  permissions[], @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onReadyClass();
    }

    /**
     *
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            ICameraConnection connection = interfaceProvider.getCameraConnection();
            if (connection != null)
            {
                connection.stopWatchWifiStatus(this);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * クラスの初期化 (instantiate)
     *
     */
    private void initializeClass()
    {
        try
        {
            scenceUpdater = CameraSceneUpdater.newInstance(this);
            interfaceProvider = CameraInterfaceProvider.newInstance(this, scenceUpdater);

            if (liveViewFragment == null)
            {
                liveViewFragment = LiveViewFragment.newInstance(scenceUpdater, interfaceProvider);
                scenceUpdater.registerInterface(liveViewFragment, interfaceProvider);
            }
            liveViewFragment.setRetainInstance(true);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, liveViewFragment);
            transaction.commitAllowingStateLoss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *    初期化終了時の処理 (カメラへの自動接続)
     */
    private void onReadyClass()
    {
        try
        {
            // カメラに自動接続するかどうか確認
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isAutoConnectCamera = preferences.getBoolean(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, true);
            Log.v(TAG, "isAutoConnectCamera() : " + isAutoConnectCamera);

            // カメラに接続する
            if (isAutoConnectCamera)
            {
                // 自動接続の指示があったとき
                scenceUpdater.changeCameraConnection();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.v(TAG, "onKeyDown()" + " " + keyCode);
        try
        {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)&&
                    ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)))
            {
                if (liveViewFragment != null)
                {
                    return (liveViewFragment.handleKeyDown(keyCode, event));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (super.onKeyDown(keyCode, event));
    }
}
