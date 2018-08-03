package net.osdn.gokigen.gr2control.camera.playback;

import android.graphics.Bitmap;

import java.util.Map;

public interface IDownloadImageCallback
{
    void onCompleted(byte[]  data, Map<String, Object> metadata);
    void onErrorOccurred(Exception  e);
    void onProgress(ProgressEvent e);
}
