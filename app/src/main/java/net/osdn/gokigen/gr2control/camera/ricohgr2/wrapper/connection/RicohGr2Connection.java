package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;


/**
 *
 *
 */
public class RicohGr2Connection implements ICameraConnection
{
    private final String TAG = toString();
    private final FragmentActivity context;
    private final ICameraStatusReceiver statusReceiver;
    private final BroadcastReceiver connectionReceiver;
    private final IUseGR2CommandNotify gr2CommandNotify;
    //private final ConnectivityManager connectivityManager;
    private final Executor cameraExecutor = Executors.newFixedThreadPool(1);
    //private final Handler networkConnectionTimeoutHandler;
    //private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;
    private CameraConnectionStatus connectionStatus = CameraConnectionStatus.UNKNOWN;


    /**
     *
     *
     */
    public RicohGr2Connection(@NonNull final FragmentActivity context, @NonNull final ICameraStatusReceiver statusReceiver, @NonNull IUseGR2CommandNotify gr2CommandNotify)
    {
        Log.v(TAG, " RicohGr2Connection()");
        this.context = context;
        this.statusReceiver = statusReceiver;
        this.gr2CommandNotify = gr2CommandNotify;
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
     *
     *
     */
    private void onReceiveBroadcastOfConnection(Context context, Intent intent)
    {
        statusReceiver.onStatusNotify(context.getString(R.string.connect_check_wifi));
        Log.v(TAG,context.getString(R.string.connect_check_wifi));

        String action = intent.getAction();
        if (action == null)
        {
            //
            Log.v(TAG, "intent.getAction() : null");
            return;
        }

        try
        {
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                Log.v(TAG, "onReceiveBroadcastOfConnection() : CONNECTIVITY_ACTION");

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    if (wifiManager.isWifiEnabled() && info != null)
                    {
                        if (info.getNetworkId() == -1)
                        {
                            Log.v(TAG, "Network ID is -1, there is no currently connected network.");
                        }
                        else
                        {
                            Log.v(TAG, "Network ID is " + info.getNetworkId());
                        }
                        // 自動接続が指示されていた場合は、カメラとの接続処理を行う
                        connectToCamera();
                    }
                    else
                    {
                        if (info == null)
                        {
                            Log.v(TAG, "NETWORK INFO IS NULL.");
                        }
                        else
                        {
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
     *
     *
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
     *
     *
     */
    @Override
    public void stopWatchWifiStatus(Context context)
    {
        Log.v(TAG, "stopWatchWifiStatus()");
        context.unregisterReceiver(connectionReceiver);
        disconnect(false);
    }

    /**
     *
     *
     */
    @Override
    public void disconnect(boolean powerOff)
    {
        Log.v(TAG, "disconnect()");
        disconnectFromCamera(powerOff);
        connectionStatus = CameraConnectionStatus.DISCONNECTED;
        statusReceiver.onCameraDisconnected();
    }


    /**
     *
     *
     */
    @Override
    public void connect()
    {
        Log.v(TAG, "connect()");
        connectToCamera();
    }


    /**
     *
     *
     */
    @Override
    public void alertConnectingFailed(String message)
    {
        Log.v(TAG, "alertConnectingFailed() : " + message);
        if (context != null)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.dialog_title_connect_failed))
                    .setMessage(message)
                    .setPositiveButton(context.getString(R.string.dialog_title_button_retry), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connect();
                        }
                    })
                    .setNeutralButton(R.string.dialog_title_button_network_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Wifi 設定画面を表示する
                                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            } catch (android.content.ActivityNotFoundException ex) {
                                // Activity が存在しなかった...設定画面が起動できなかった
                                Log.v(TAG, "android.content.ActivityNotFoundException...");

                                // この場合は、再試行と等価な動きとする
                                connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();
                }
            });
        }
    }

    @Override
    public CameraConnectionStatus getConnectionStatus()
    {
        Log.v(TAG, "getConnectionStatus()");
        return (connectionStatus);
    }

    /**
     *
     *
     */
    @Override
    public void forceUpdateConnectionStatus(CameraConnectionStatus status)
    {
        Log.v(TAG, "forceUpdateConnectionStatus()");
        connectionStatus = status;
    }

    /**
     * カメラとの切断処理
     */
    private void disconnectFromCamera(final boolean powerOff)
    {
        Log.v(TAG, "disconnectFromCamera()");
        try
        {
            cameraExecutor.execute(new RicohGr2CameraDisconnectSequence(context, powerOff));
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
            cameraExecutor.execute(new RicohGr2CameraConnectSequence(context, statusReceiver, this, gr2CommandNotify));
        }
        catch (Exception e)
        {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
