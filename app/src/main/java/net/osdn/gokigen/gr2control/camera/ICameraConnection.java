package net.osdn.gokigen.gr2control.camera;

import android.content.Context;

/**
 *   カメラの接続/切断
 *
 */
public interface ICameraConnection
{
    enum CameraConnectionMethod
    {
        UNKNOWN,
        OPC,
        SONY,
        RICOH_GR2,
    }

    enum CameraConnectionStatus
    {
        UNKNOWN,
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    /**  WIFI 接続系  **/
    void startWatchWifiStatus(Context context);
    void stopWatchWifiStatus(Context context);

    /** カメラ接続系 **/
    void disconnect(final boolean powerOff);
    void connect();

    /** カメラ接続失敗 **/
    void alertConnectingFailed(String message);

    /** 接続状態 **/
    CameraConnectionStatus getConnectionStatus();
    void forceUpdateConnectionStatus(CameraConnectionStatus status);

}
