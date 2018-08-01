package net.osdn.gokigen.gr2control.camera.playback;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import java.util.List;

public interface IDownloadContentListCallback
{
    void onCompleted(List<ICameraFileInfo> contentList);
    void onErrorOccurred( Exception  e);
}
