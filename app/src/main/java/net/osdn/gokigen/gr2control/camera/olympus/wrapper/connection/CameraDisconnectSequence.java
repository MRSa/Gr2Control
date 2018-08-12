package net.osdn.gokigen.gr2control.camera.olympus.wrapper.connection;

import android.util.Log;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 *   Olympusカメラとの切断処理
 *
 */
class CameraDisconnectSequence implements Runnable
{
    private final String TAG = this.toString();

    private final OLYCamera camera;
    private final boolean powerOff;

    /**
     *   コンストラクタ
     *
     */
    CameraDisconnectSequence(OLYCamera camera, boolean isOff)
    {
        this.camera = camera;
        this.powerOff = isOff;
    }

    @Override
    public void run()
    {
        // カメラをPowerOffして接続を切る
        try
        {
            camera.disconnectWithPowerOff(powerOff);
        }
        catch (OLYCameraKitException e)
        {
            // エラー情報をログに出力する
            Log.w(TAG, "To disconnect from the camera is failed. : " + e.getLocalizedMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
