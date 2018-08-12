package net.osdn.gokigen.gr2control.camera.playback;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

/**
 *   画像再生・取得用インタフェース
 *
 */
public interface IPlaybackControl
{
    String getRawFileSuffix();
    void downloadContentList(@NonNull IDownloadContentListCallback callback);
    void getContentInfo(@NonNull String  path, @NonNull IContentInfoCallback  callback);
    void updateCameraFileInfo(ICameraFileInfo info);

    void downloadContentScreennail(@NonNull String  path, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadContentThumbnail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadContent(@NonNull String  path, boolean isSmallSize, @NonNull IDownloadContentCallback callback);
}
