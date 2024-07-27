package net.osdn.gokigen.gr2control.camera.olympus.wrapper.playback;

import android.graphics.BitmapFactory;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.CameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    public void downloadContentList(@NonNull final ICameraContentListCallback callback)
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
    public void getContentInfo(@Nullable String path, @NonNull String name, @NonNull IContentInfoCallback callback)
    {
        try
        {
            // ここは使っていないから何もしない
            Log.v(TAG, "getContentInfo() : " + name);
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
    public void downloadContentScreennail(@Nullable String path, @NonNull String name, @NonNull final IDownloadThumbnailImageCallback callback)
    {
        try
        {
            camera.downloadContentScreennail(name, new OLYCamera.DownloadImageCallback() {
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
    public void downloadContentThumbnail(@Nullable String path, @NonNull String name, @NonNull final IDownloadThumbnailImageCallback callback)
    {
        try
        {
            camera.downloadContentThumbnail(name, new OLYCamera.DownloadImageCallback() {
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
    public void downloadContent(@Nullable String path,  @NonNull String name, boolean isSmallSize, @NonNull final IDownloadContentCallback callback)
    {
        try
        {
            if (isSmallSize)
            {
                //
                downloadSmallImage(path, name, callback);
                return;
            }

            camera.downloadLargeContent(name, new OLYCamera.DownloadLargeContentCallback() {
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

    private void downloadSmallImage(@Nullable String path, @NonNull String name, @NonNull final IDownloadContentCallback callback)
    {
        try
        {
            camera.downloadImage(name, OLYCamera.IMAGE_RESIZE_1920, new OLYCamera.DownloadImageCallback() {
                @Override
                public void onProgress(OLYCamera.ProgressEvent progressEvent)
                {
                    callback.onProgress(new byte[0], 0, new ProgressEvent(progressEvent.getProgress(), null));
                }

                @Override
                public void onCompleted(byte[] bytes, Map<String, Object> map)
                {
                    try
                    {
                        callback.onProgress(bytes, bytes.length, new ProgressEvent(99.0f, null));
                        callback.onCompleted();
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
        catch (Throwable t)
        {
            t.printStackTrace();
            callback.onErrorOccurred(new NullPointerException());
        }

    }

    @Override
    public void showPictureStarted()
    {

    }

    @Override
    public void showPictureFinished()
    {

    }
}
