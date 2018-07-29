package net.osdn.gokigen.gr2control.camera;

import java.util.Map;

public interface ICameraLiveViewListener
{
    void onUpdateLiveView(byte[] data, Map<String, Object> metadata);
}
