package net.osdn.gokigen.gr2control.camera.olympus.wrapper.playback;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.CameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraFileInfo;

public class OlyCameraPlaybackControl implements IPlaybackControl
{
    private final String TAG = toString();
    private final OLYCamera camera;
    private List<OLYCamera> list;

    public OlyCameraPlaybackControl(@NonNull OLYCamera camera)
    {
        this.camera = camera;
    }

    @Override
    public String getRawFileSuffix()
    {
        return (".ORF");
    }

    @Override
    public void downloadContentList(@NonNull final IDownloadContentListCallback callback)
    {
        try
        {
            camera.downloadContentList(new OLYCamera.DownloadContentListCallback() {
                @Override
                public void onCompleted(List<OLYCameraFileInfo> list)
                {
                    List<ICameraFileInfo> list2 = new ArrayList<>();
                    for (OLYCameraFileInfo fileInfo : list)
                    {
                        CameraFileInfo cameraFileInfo = new CameraFileInfo(fileInfo.getDirectoryPath(), fileInfo.getFilename());
                        cameraFileInfo.setDate(fileInfo.getDatetime());
                        list2.add(cameraFileInfo);
                    }
                    callback.onCompleted(list2);
                }

                @Override
                public void onErrorOccurred(Exception e)
                {
                    callback.onErrorOccurred(e);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
    }

    @Override
    public void getContentInfo(@NonNull String path, @NonNull IContentInfoCallback callback)
    {
        try
        {
            // ここは使っていないから何もしない
            Log.v(TAG, "getContentInfo() : " + path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        callback.onErrorOccurred(new NullPointerException());
    }

    @Override
    public void updateCameraFileInfo(ICameraFileInfo info)
    {
        try
        {
            Log.v(TAG, "updateCameraFileInfo() : " + info.getFilename());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadContentScreennail(@NonNull String path, @NonNull final IDownloadThumbnailImageCallback callback)
    {
        try
        {
            camera.downloadContentScreennail(path, new OLYCamera.DownloadImageCallback() {
                @Override
                public void onProgress(OLYCamera.ProgressEvent progressEvent)
                {
                    // なにもしない
                }

                @Override
                public void onCompleted(byte[] bytes, Map<String, Object> map)
                {
                    try
                    {
                        callback.onCompleted(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), map);
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                        callback.onErrorOccurred(new NullPointerException());
                    }
                }

                @Override
                public void onErrorOccurred(Exception e)
                {
                    callback.onErrorOccurred(e);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
    }

    @Override
    public void downloadContentThumbnail(@NonNull String path, @NonNull final IDownloadThumbnailImageCallback callback)
    {
        try
        {
            camera.downloadContentThumbnail(path, new OLYCamera.DownloadImageCallback() {
                @Override
                public void onProgress(OLYCamera.ProgressEvent progressEvent)
                {
                    // なにもしない
                }

                @Override
                public void onCompleted(byte[] bytes, Map<String, Object> map)
                {
                    try
                    {
                        callback.onCompleted(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), map);
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                        callback.onErrorOccurred(new NullPointerException());
                    }
                }

                @Override
                public void onErrorOccurred(Exception e)
                {
                    callback.onErrorOccurred(e);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
    }

    @Override
    public void downloadContent(@NonNull String path, boolean isSmallSize, @NonNull final IDownloadContentCallback callback)
    {
        try
        {
            camera.downloadLargeContent(path, new OLYCamera.DownloadLargeContentCallback() {
                @Override
                public void onProgress(byte[] bytes, OLYCamera.ProgressEvent progressEvent)
                {
                    try
                    {
                        callback.onProgress(bytes, bytes.length, new ProgressEvent(progressEvent.getProgress(), null));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCompleted()
                {
                    try
                    {
                        callback.onCompleted();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorOccurred(Exception e)
                {
                    try
                    {
                        callback.onErrorOccurred(e);
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
    }
}
