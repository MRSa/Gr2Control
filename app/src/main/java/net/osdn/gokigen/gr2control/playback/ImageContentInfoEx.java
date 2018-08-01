package net.osdn.gokigen.gr2control.playback;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

class ImageContentInfoEx
{
    private final ICameraFileInfo fileInfo;
    private boolean hasRaw;

    ImageContentInfoEx(ICameraFileInfo fileInfo, boolean hasRaw)
    {
        this.fileInfo = fileInfo;
        this.hasRaw = hasRaw;
    }

    void setHasRaw(boolean value)
    {
        hasRaw = value;
    }
    boolean hasRaw()
    {
        return (hasRaw);
    }

    ICameraFileInfo getFileInfo()
    {
        return (fileInfo);
    }
}
