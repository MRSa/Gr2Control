package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadLargeContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;

public class RicohGr2PlaybackControl implements IPlaybackControl
{
    private final String TAG = toString();
    private final String imageListurl = "http://192.168.0.1/v1/photos?limit=3000";

    RicohGr2PlaybackControl()
    {

    }

    @Override
    public void downloadContentList(IDownloadContentListCallback callback)
    {

    }

    @Override
    public void downloadContentScreennail(String path, IDownloadImageCallback callback)
    {

    }

    @Override
    public void downloadContentThumbnail(String path, IDownloadImageCallback callback)
    {

    }

    @Override
    public void downloadImage(String path, float resize, IDownloadImageCallback callback)
    {

    }

    @Override
    public void downloadLargeContent(String path, IDownloadLargeContentCallback callback) {

    }
}
