package net.osdn.gokigen.gr2control.camera.playback;

import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadImageCallback;

/**
 *   画像再生・取得用インタフェース
 *
 */
public interface IPlaybackControl
{
    void downloadContentList(IDownloadContentListCallback callback);
    void downloadContentScreennail(String  path, IDownloadImageCallback callback);
    void downloadContentThumbnail(String  path, IDownloadImageCallback  callback);
    void downloadImage(String  path, float resize, IDownloadImageCallback  callback);
    void downloadLargeContent(String  path, IDownloadLargeContentCallback  callback);
}
