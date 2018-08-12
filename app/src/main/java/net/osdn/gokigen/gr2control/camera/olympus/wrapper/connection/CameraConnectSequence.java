package net.osdn.gokigen.gr2control.camera.olympus.wrapper.connection;

import android.content.Context;
import android.util.Log;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraStatusReceiver;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 *   Olympusカメラとの接続処理
 *
 */
class CameraConnectSequence implements Runnable
{
    private final String TAG = this.toString();
    private final Context context;
    private final OLYCamera camera;
    private final ICameraStatusReceiver cameraStatusReceiver;

    /**
     *   コンストラクタ
     */
    CameraConnectSequence(Context context, OLYCamera camera, ICameraStatusReceiver statusReceiver)
    {
        Log.v(TAG, "CameraConnectSequence");
        this.context = context;
        this.camera =camera;
        this.cameraStatusReceiver = statusReceiver;
    }

    /**
     *   カメラとの接続実処理
     *
     */
    @Override
    public void run()
    {
        String statusMessage = context.getString(R.string.connect_start);
        try
        {
            statusMessage = context.getString(R.string.connect_check_wifi);
            cameraStatusReceiver.onStatusNotify(statusMessage);
            camera.connect(OLYCamera.ConnectionType.WiFi);

            // ライブビューの自動スタート設定の場合、自動スタートをやめる
            if (camera.isAutoStartLiveView())
            {
                camera.setAutoStartLiveView(false);
            }

            // 一度カメラの動作モードを確認する
            OLYCamera.RunMode runMode = camera.getRunMode();
            if (runMode == OLYCamera.RunMode.Unknown)
            {
                // UNKNOWNモードは動作しない、メッセージを作って応答する
                statusMessage = context.getString(R.string.fatal_cannot_use_camera);
                cameraStatusReceiver.onCameraOccursException(statusMessage, new IllegalStateException(context.getString(R.string.camera_reset_required)));
                Log.w(TAG, "DETECT : OLYCamera.RunMode.Unknown");
            }
            if (runMode != OLYCamera.RunMode.Recording)
            {
                // Recordingモードでない場合は切り替える
                statusMessage = context.getString(R.string.connect_change_run_mode);
                cameraStatusReceiver.onStatusNotify(statusMessage);
                camera.changeRunMode(OLYCamera.RunMode.Recording);
            }
       }
        catch (OLYCameraKitException e)
        {
            cameraStatusReceiver.onCameraOccursException(statusMessage, e);
            e.printStackTrace();
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        Log.v(TAG, "CameraConnectSequence:: connected.");

        // カメラとの接続確立を通知する
        cameraStatusReceiver.onStatusNotify(context.getString(R.string.connect_connected));
        cameraStatusReceiver.onCameraConnected();
    }
}
