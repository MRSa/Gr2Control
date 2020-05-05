package net.osdn.gokigen.gr2control.scene;

import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.liveview.IStatusViewDrawer;
import net.osdn.gokigen.gr2control.logcat.LogCatFragment;
import net.osdn.gokigen.gr2control.playback.ImageGridViewFragment;
import net.osdn.gokigen.gr2control.preference.Gr2ControlPreferenceFragment;
import net.osdn.gokigen.gr2control.preference.fuji_x.FujiXPreferenceFragment;
import net.osdn.gokigen.gr2control.preference.olympus.PreferenceFragment;
import net.osdn.gokigen.gr2control.preference.ricohgr2.RicohGr2PreferenceFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

/**
 *
 *
 */
public class CameraSceneUpdater implements ICameraStatusReceiver, IChangeScene
{
    private final String TAG = toString();
    private final AppCompatActivity activity;
    private IInterfaceProvider interfaceProvider;
    private IStatusViewDrawer statusViewDrawer;

    private PreferenceFragmentCompat preferenceFragment = null;
    private PreferenceFragmentCompat preferenceFragmentOpc = null;
    private PreferenceFragmentCompat preferenceFragmentRicoh = null;
    private PreferenceFragmentCompat preferenceFragmentFujiX = null;

    private LogCatFragment logCatFragment = null;
    private ImageGridViewFragment imageGridViewFragment = null;

    public static CameraSceneUpdater newInstance(@NonNull AppCompatActivity activity)
    {
        return (new CameraSceneUpdater(activity));
    }

    /**
     *  コンストラクタ
     *
     */
    private CameraSceneUpdater(@NonNull AppCompatActivity activity)
    {
        this.activity = activity;
    }

    //  CameraSceneUpdater
    public void registerInterface(@NonNull IStatusViewDrawer statusViewDrawer, @NonNull IInterfaceProvider interfaceProvider)
    {
        Log.v(TAG, "registerInterface()");
        this.statusViewDrawer = statusViewDrawer;
        this.interfaceProvider = interfaceProvider;
    }

    // ICameraStatusReceiver
    @Override
    public void onStatusNotify(String message)
    {
        Log.v(TAG, " CONNECTION MESSAGE : " + message);
        try
        {
            if (statusViewDrawer != null)
            {
                statusViewDrawer.updateStatusView(message);
                ICameraConnection connection = getCameraConnection(interfaceProvider.getCammeraConnectionMethod());
                if (connection != null)
                {
                    statusViewDrawer.updateConnectionStatus(connection.getConnectionStatus());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // ICameraStatusReceiver
    @Override
    public void onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()");

        try
        {
            ICameraConnection connection = getCameraConnection(interfaceProvider.getCammeraConnectionMethod());
            if (connection != null)
            {
                connection.forceUpdateConnectionStatus(ICameraConnection.CameraConnectionStatus.CONNECTED);
            }
            if (statusViewDrawer != null)
            {
                statusViewDrawer.updateConnectionStatus(ICameraConnection.CameraConnectionStatus.CONNECTED);

                // ライブビューの開始...
                statusViewDrawer.startLiveView();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // ICameraStatusReceiver
    @Override
    public void onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()");
        if (statusViewDrawer != null)
        {
            statusViewDrawer.updateStatusView(activity.getString(R.string.camera_disconnected));
            statusViewDrawer.updateConnectionStatus(ICameraConnection.CameraConnectionStatus.DISCONNECTED);
        }
    }

    // ICameraStatusReceiver
    @Override
    public void onCameraOccursException(String message, Exception e)
    {
        Log.v(TAG, "onCameraOccursException() " + message);
        try
        {
            e.printStackTrace();
            ICameraConnection connection = getCameraConnection(interfaceProvider.getCammeraConnectionMethod());
            if (connection != null)
            {
                connection.alertConnectingFailed(message + " " + e.getLocalizedMessage());
            }
            if (statusViewDrawer != null)
            {
                statusViewDrawer.updateStatusView(message);
                if (connection != null)
                {
                    statusViewDrawer.updateConnectionStatus(connection.getConnectionStatus());
                }
            }
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    //  IChangeScene
    @Override
    public void changeSceneToCameraPropertyList()
    {
/*
        try
        {
            ICameraConnection.CameraConnectionMethod method = interfaceProvider.getCammeraConnectionMethod();
            ICameraConnection connection = getCameraConnection(method);
            if (method == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                // OPCカメラでない場合には、「OPCカメラのみ有効です」表示をして画面遷移させない
                Toast.makeText(getApplicationContext(), getText(R.string.only_opc_feature), Toast.LENGTH_SHORT).show();
            }
            else if (method == ICameraConnection.CameraConnectionMethod.SONY)
            {
                // OPCカメラでない場合には、「OPCカメラのみ有効です」表示をして画面遷移させない
                Toast.makeText(getApplicationContext(), getText(R.string.only_opc_feature), Toast.LENGTH_SHORT).show();
            }
            else
            {
                // OPC カメラの場合...
                if (connection != null)
                {
                    ICameraConnection.CameraConnectionStatus status = connection.getConnectionStatus();
                    if (status == ICameraConnection.CameraConnectionStatus.CONNECTED)
                    {
                        if (propertyListFragment == null)
                        {
                            propertyListFragment = OlyCameraPropertyListFragment.newInstance(this, interfaceProvider.getOlympusInterface().getCameraPropertyProvider());
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment1, propertyListFragment);
                        // backstackに追加
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
    }

    //  IChangeScene
    @Override
    public void changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod connectionMethod)
    {
        try
        {
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                changeSceneToRicohConfiguration();
            }
            else if (connectionMethod == ICameraConnection.CameraConnectionMethod.FUJI_X)
            {
                changeSceneToFujiXConfiguration();
            }
            else if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                changeSceneToOpcConfiguration();
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.UNKNOWN)
            {
                changeSceneToSummaryConfiguration();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void changeSceneToSummaryConfiguration()
    {
        try
        {
            if (preferenceFragment == null)
            {
                preferenceFragment = Gr2ControlPreferenceFragment.newInstance(activity, this);
            }
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, preferenceFragment);
            // backstackに追加
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void changeSceneToOpcConfiguration()
    {
        try
        {
            if (preferenceFragmentOpc == null)
            {
                preferenceFragmentOpc = PreferenceFragment.newInstance(activity, interfaceProvider, this);
            }
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, preferenceFragmentOpc);
            // backstackに追加
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void changeSceneToRicohConfiguration()
    {
        try
        {
            if (preferenceFragmentRicoh == null)
            {
                preferenceFragmentRicoh = RicohGr2PreferenceFragment.newInstance(activity, this);
            }
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, preferenceFragmentRicoh);
            // backstackに追加
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void changeSceneToFujiXConfiguration()
    {
        try
        {
            if (preferenceFragmentFujiX == null)
            {
                preferenceFragmentFujiX = FujiXPreferenceFragment.newInstance(activity, this);
            }
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, preferenceFragmentFujiX);
            // backstackに追加
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //  IChangeScene
    @Override
    public void changeCameraConnection()
    {
        if (interfaceProvider == null)
        {
            Log.v(TAG, "changeCameraConnection() : interfaceProvider is NULL");
            return;
        }
        try
        {
            interfaceProvider.resetCameraConnectionMethod();
            ICameraConnection connection = interfaceProvider.getCameraConnection();
            if (connection != null)
            {
                ICameraConnection.CameraConnectionStatus status = connection.getConnectionStatus();
                if (status == ICameraConnection.CameraConnectionStatus.CONNECTED)
                {
                    // 接続中のときには切断する
                    connection.disconnect(false);
                    return;
                }
                // 接続中でない時は、接続中にする
                connection.startWatchWifiStatus(activity);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //  IChangeScene
    @Override
    public void changeSceneToDebugInformation()
    {
        if (logCatFragment == null)
        {
            logCatFragment = LogCatFragment.newInstance();
        }
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, logCatFragment);
        // backstackに追加
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //  IChangeScene
    @Override
    public void changeSceneToApiList()
    {
/*
        if (sonyApiListFragmentSony == null)
        {
            sonyApiListFragmentSony = SonyCameraApiListFragment.newInstance(interfaceProvider);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, sonyApiListFragmentSony);
        // backstackに追加
        transaction.addToBackStack(null);
        transaction.commit();
*/
    }

    /**
     *   画像一覧画面を開く
     *
     */
    //  IChangeScene
    @Override
    public void changeScenceToImageList()
    {
        Log.v(TAG, "changeScenceToImageList()");
        try
        {
            if (imageGridViewFragment == null)
            {
                imageGridViewFragment = ImageGridViewFragment.newInstance(interfaceProvider.getPlaybackControl(), interfaceProvider.getCameraRunMode());
            }
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, imageGridViewFragment);
            // backstackに追加
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //  IChangeScene
    @Override
    public void exitApplication()
    {
        Log.v(TAG, "exitApplication()");
        try
        {
            ICameraConnection connection = getCameraConnection(interfaceProvider.getCammeraConnectionMethod());
            if (connection != null)
            {
                connection.disconnect(true);
            }
            activity.finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ICameraConnection getCameraConnection(ICameraConnection.CameraConnectionMethod method)
    {
        Log.v(TAG, "method : " + method);
        return (interfaceProvider.getCameraConnection());
    }
}
