package net.osdn.gokigen.gr2control.camera.olympus.wrapper.connection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


/**
 *   カメラの接続・切断処理クラス
 *
 */
public class OlyCameraConnection implements ICameraConnection, OLYCameraConnectionListener
{
    private final String TAG = toString();
    private final Activity context;
    private final OLYCamera camera;
    private final Executor cameraExecutor = Executors.newFixedThreadPool(1);
    private final BroadcastReceiver connectionReceiver;
    private final ICameraStatusReceiver statusReceiver;

    //private boolean isWatchingWifiStatus = false;
    private CameraConnectionStatus connectionStatus = CameraConnectionStatus.UNKNOWN;

    //private ConnectivityManager connectivityManager;
    //private ConnectivityManager.NetworkCallback networkCallback = null;

    // Handler for dealing with network connection timeouts.
   // private Handler networkConnectionTimeoutHandler;

    // Message to notify the network request timout handler that too much time has passed.
    //private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    /**
     *    コンストラクタ
     *
     */
    public OlyCameraConnection(final Activity context, OLYCamera camera, ICameraStatusReceiver statusReceiver)
    {
        Log.v(TAG, "OlyCameraConnection()");
        this.context = context;
        this.camera = camera;
        this.camera.setConnectionListener(this);
/*
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Handler networkConnectionTimeoutHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Log.d(TAG, "Network connection timeout");
                        alertConnectingFailed(context.getString(R.string.network_connection_timeout));
                        connectionStatus = CameraConnectionStatus.DISCONNECTED;
                        break;
                }
            }
        };
*/
        this.statusReceiver = statusReceiver;
        connectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                onReceiveBroadcastOfConnection(context, intent);
            }
        };

    }

    /**
     *   Wifiが使える状態だったら、カメラと接続して動作するよ
     *
     */
    private void onReceiveBroadcastOfConnection(Context context, Intent intent)
    {
        statusReceiver.onStatusNotify(context.getString(R.string.connect_check_wifi));
        Log.v(TAG,context.getString(R.string.connect_check_wifi));

        String action = intent.getAction();
        try
        {
            if ((action != null)&&(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)))
            {
                Log.v(TAG, "onReceiveBroadcastOfConnection() : CONNECTIVITY_ACTION");

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    if (wifiManager.isWifiEnabled() && info != null) {
                        if (info.getNetworkId() != -1) {
                            Log.v(TAG, "Network ID is -1, there is no currently connected network.");
                        }
                        // 自動接続が指示されていた場合は、カメラとの接続処理を行う
                        connectToCamera();
                    } else {
                        if (info == null) {
                            Log.v(TAG, "NETWORK INFO IS NULL.");
                        } else {
                            Log.v(TAG, "isWifiEnabled : " + wifiManager.isWifiEnabled() + " NetworkId : " + info.getNetworkId());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "onReceiveBroadcastOfConnection() EXCEPTION" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Wifi接続状態の監視
     * (接続の実処理は onReceiveBroadcastOfConnection() で実施)
     */
    @Override
    public void startWatchWifiStatus(Context context)
    {
        Log.v(TAG, "startWatchWifiStatus()");
        statusReceiver.onStatusNotify("prepare");

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(connectionReceiver, filter);
    }

    /**
     * Wifi接続状態の監視終了
     */
    @Override
    public void stopWatchWifiStatus(Context context)
    {
        Log.v(TAG, "stopWatchWifiStatus()");
        context.unregisterReceiver(connectionReceiver);
        disconnect(false);
    }

    /**
     * 　 カメラとの接続を解除する
     *
     * @param powerOff 真ならカメラの電源オフを伴う
     */
    @Override
    public void disconnect(final boolean powerOff)
    {
        Log.v(TAG, "disconnect()");
        disconnectFromCamera(powerOff);
        connectionStatus = CameraConnectionStatus.DISCONNECTED;
        statusReceiver.onCameraDisconnected();
    }

    /**
     * カメラとの再接続を指示する
     */
    @Override
    public void connect() {
        connectToCamera();
    }

    @Override
    public CameraConnectionStatus getConnectionStatus()
    {
        return (connectionStatus);
    }

    /**
     *   カメラの接続状態を強制更新する
     *
     */
    @Override
    public void forceUpdateConnectionStatus(CameraConnectionStatus status)
    {
        connectionStatus = status;
    }


    /**
     * カメラの通信状態変化を監視するためのインターフェース
     *
     * @param camera 例外が発生した OLYCamera
     * @param e      カメラクラスの例外
     */
    @Override
    public void onDisconnectedByError(OLYCamera camera, OLYCameraKitException e)
    {
        Log.v(TAG, "onDisconnectedByError()");
        connectionStatus = CameraConnectionStatus.DISCONNECTED;

        // カメラが切れた時に通知する
        statusReceiver.onCameraDisconnected();
    }

    /**
     * カメラとの切断処理
     */
    private void disconnectFromCamera(final boolean powerOff)
    {
        Log.v(TAG, "disconnectFromCamera()");
        try
        {
            cameraExecutor.execute(new CameraDisconnectSequence(camera, powerOff));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * カメラとの接続処理
     */
    private void connectToCamera()
    {
        Log.v(TAG, "connectToCamera()");
        connectionStatus = CameraConnectionStatus.CONNECTING;
        try
        {
            cameraExecutor.execute(new CameraConnectSequence(context, camera, statusReceiver));
        }
        catch (Exception e)
        {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *   接続リトライのダイアログを出す
     *
     * @param message 表示用のメッセージ
     */
    @Override
    public void alertConnectingFailed(String message)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_title_connect_failed))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.dialog_title_button_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        connect();
                    }
                })
                .setNeutralButton(R.string.dialog_title_button_network_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            // Wifi 設定画面を表示する
                            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                        catch (android.content.ActivityNotFoundException ex)
                        {
                            // Activity が存在しなかった...設定画面が起動できなかった
                            Log.v(TAG, "android.content.ActivityNotFoundException...");

                            // この場合は、再試行と等価な動きとする
                            connect();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                builder.show();
            }
        });
    }
}
