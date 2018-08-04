package net.osdn.gokigen.gr2control.camera.playback;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadImageCallback;

/**
 *   画像再生・取得用インタフェース
 *
 */
public interface IPlaybackControl
{
    void downloadContentList(@NonNull IDownloadContentListCallback callback);
    void getContentInfo(@NonNull String  path, @NonNull IContentInfoCallback  callback);
    void updateCameraFileInfo(ICameraFileInfo info);

    void downloadContentScreennail(@NonNull String  path, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadContentThumbnail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback);
    //void downloadContentScreennail(@NonNull String  path, @NonNull IDownloadThumbnailImageCallback callback);
    //void downloadContentThumbnail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadImage(@NonNull String  path, float resize, @NonNull IDownloadImageCallback  callback);
    void downloadLargeContent(@NonNull String  path, @NonNull IDownloadLargeContentCallback  callback);
}
