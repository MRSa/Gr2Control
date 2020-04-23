package net.osdn.gokigen.gr2control.camera.playback;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import java.util.List;

public interface ICameraContentListCallback
{
    void onCompleted(List<ICameraFileInfo> contentList);
    //void onCompleted(List<ICameraContent> contentList);
    void onErrorOccurred(Exception  e);
}
