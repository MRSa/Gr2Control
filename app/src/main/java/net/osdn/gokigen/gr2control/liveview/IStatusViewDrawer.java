package net.osdn.gokigen.gr2control.liveview;

import net.osdn.gokigen.gr2control.camera.ICameraConnection;

public interface IStatusViewDrawer
{
    void updateGridIcon();
    void updateConnectionStatus(ICameraConnection.CameraConnectionStatus connectionStatus);
    void updateStatusView(String message);
    void updateLiveViewScale(boolean isChangeScale);
    void startLiveView();
}
