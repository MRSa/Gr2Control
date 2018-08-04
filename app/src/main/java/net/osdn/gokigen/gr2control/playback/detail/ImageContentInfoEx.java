package net.osdn.gokigen.gr2control.playback.detail;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

public class ImageContentInfoEx
{
    private final ICameraFileInfo fileInfo;
    private boolean hasRaw;

    public ImageContentInfoEx(ICameraFileInfo fileInfo, boolean hasRaw)
    {
        this.fileInfo = fileInfo;
        this.hasRaw = hasRaw;
    }

    public void setHasRaw(boolean value)
    {
        hasRaw = value;
    }
    public boolean hasRaw()
    {
        return (hasRaw);
    }

    public ICameraFileInfo getFileInfo()
    {
        return (fileInfo);
    }
}
