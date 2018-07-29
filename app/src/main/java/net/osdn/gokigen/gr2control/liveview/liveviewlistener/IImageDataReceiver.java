package net.osdn.gokigen.gr2control.liveview.liveviewlistener;

import java.util.Map;

public interface IImageDataReceiver
{
    void setImageData(byte[] data, Map<String, Object> metadata);
}
