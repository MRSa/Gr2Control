package net.osdn.gokigen.gr2control.playback.detail;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

public class ImageContentInfoEx
{
    private final ICameraFileInfo fileInfo;
    private String rawSuffix;
    private boolean hasRaw;
    private boolean selected;

    public ImageContentInfoEx(ICameraFileInfo fileInfo, boolean hasRaw, String rawSuffix)
    {
        this.fileInfo = fileInfo;
        this.hasRaw = hasRaw;
        this.rawSuffix = rawSuffix;
        this.selected = false;
    }

    public void setHasRaw(boolean value, String rawSuffix)
    {
        hasRaw = value;
        this.rawSuffix = rawSuffix;
    }

    public boolean hasRaw()
    {
        return (hasRaw);
    }

    public String getRawSuffix()
    {
        return (rawSuffix);
    }

    public ICameraFileInfo getFileInfo()
    {
        return (fileInfo);
    }

    public void setSelected(boolean isSelected)
    {
        selected = isSelected;
    }

    public boolean isSelected()
    {
        return (selected);
    }

}
