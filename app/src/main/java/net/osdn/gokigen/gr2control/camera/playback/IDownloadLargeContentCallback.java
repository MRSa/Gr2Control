package net.osdn.gokigen.gr2control.camera.playback;

public interface IDownloadLargeContentCallback
{
    void onCompleted();
    void onErrorOccurred(Exception  e);
    void onProgress(byte[] data, ProgressEvent e);
}
