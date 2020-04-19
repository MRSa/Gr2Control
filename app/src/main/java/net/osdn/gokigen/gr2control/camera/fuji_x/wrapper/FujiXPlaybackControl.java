package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;

public class FujiXPlaybackControl implements IPlaybackControl
{
    private final String TAG = toString();
    private static final int DEFAULT_TIMEOUT = 5000;
    private final int timeoutValue;

    FujiXPlaybackControl(int timeoutMSec)
    {
        this.timeoutValue  = Math.max(DEFAULT_TIMEOUT, timeoutMSec); // (timeoutMSec < DEFAULT_TIMEOUT) ? DEFAULT_TIMEOUT : timeoutMSec;
    }


    @Override
    public String getRawFileSuffix()
    {
        return null;
    }

    @Override
    public void downloadContentList(@NonNull IDownloadContentListCallback callback)
    {

    }

    @Override
    public void getContentInfo(@NonNull String path, @NonNull IContentInfoCallback callback)
    {

    }

    @Override
    public void updateCameraFileInfo(ICameraFileInfo info)
    {

    }

    @Override
    public void downloadContentScreennail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback)
    {

    }

    @Override
    public void downloadContentThumbnail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback)
    {

    }

    @Override
    public void downloadContent(@NonNull String path, boolean isSmallSize, @NonNull IDownloadContentCallback callback)
    {

    }
}
