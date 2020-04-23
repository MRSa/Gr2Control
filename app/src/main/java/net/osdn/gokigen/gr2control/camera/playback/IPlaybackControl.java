package net.osdn.gokigen.gr2control.camera.playback;


import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *   画像再生・取得用インタフェース
 *
 */
public interface IPlaybackControl
{
    String getRawFileSuffix();
    void downloadContentList(@NonNull ICameraContentListCallback callback);
    void getContentInfo(@Nullable String  path, @NonNull String  name, @NonNull IContentInfoCallback  callback);

    void updateCameraFileInfo(ICameraFileInfo info);

    void downloadContentScreennail(@Nullable String  path, @NonNull String  name, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadContentThumbnail(@Nullable String path, @NonNull String  name, @NonNull IDownloadThumbnailImageCallback callback);
    void downloadContent(@Nullable String  path, @NonNull String  name, boolean isSmallSize, @NonNull IDownloadContentCallback callback);

    void showPictureStarted();
    void showPictureFinished();

}
