package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.graphics.Bitmap;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.CameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *
 *
 */
public class RicohGr2PlaybackControl implements IPlaybackControl
{
    private final String TAG = toString();
    private final String getPhotoUrl = "http://192.168.0.1/v1/photos/";
    private static final int DEFAULT_TIMEOUT = 5000;
    private final int timeoutValue;

    /*****
         [操作メモ]
            画像の一覧をとる            : http://192.168.0.1/v1/photos?limit=3000
            画像の情報をとる            ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG/info
            サムネール画像をとる(JPEG)  ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=thumb
            サムネール画像をとる(DNG)   ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG?size=view
            サムネール画像をとる(MOV)   ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.MOV?size=view
            デバイス表示用画像をとる     :  http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=view
            画像(JPEG)をダウンロードする ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=full
            画像(DNG)をダウンロードする  ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG?size=full
            動画をダウンロードする      ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.MOV?size=full
     *****/

    RicohGr2PlaybackControl(int timeoutMSec)
    {
        this.timeoutValue  = Math.max(DEFAULT_TIMEOUT, timeoutMSec); // (timeoutMSec < DEFAULT_TIMEOUT) ? DEFAULT_TIMEOUT : timeoutMSec;
    }

    @Override
    public String getRawFileSuffix()
    {
        return (".DNG");
    }

    @Override
    public void downloadContentList(@NonNull ICameraContentListCallback callback)
    {
        List<ICameraFileInfo> fileList = new ArrayList<>();
        String imageListurl = "http://192.168.0.1/v1/photos?limit=3000";
        String contentList;
        try
        {
            contentList = SimpleHttpClient.httpGet(imageListurl, timeoutValue);
            if (contentList == null)
            {
                // ぬるぽ発行
                callback.onErrorOccurred(new NullPointerException());
                return;
            }
        }
        catch (Exception e)
        {
            // 例外をそのまま転送
            callback.onErrorOccurred(e);
            return;
        }
        try
        {
            JSONArray dirsArray = new JSONObject(contentList).getJSONArray("dirs");
            // if (dirsArray != null)
            {
                int size = dirsArray.length();
                for (int index = 0; index < size; index++)
                {
                    JSONObject object = dirsArray.getJSONObject(index);
                    String dirName = object.getString("name");
                    JSONArray filesArray = object.getJSONArray("files");
                    int nofFiles = filesArray.length();
                    for (int fileIndex = 0; fileIndex < nofFiles; fileIndex++)
                    {
                        String fileName = filesArray.getString(fileIndex);
                        fileList.add(new CameraFileInfo(dirName, fileName));
                    }
                }
            }
        }
        catch (Exception e)
        {
            callback.onErrorOccurred(e);
            return;
        }
        callback.onCompleted(fileList);
    }

    @Override
    public void updateCameraFileInfo(ICameraFileInfo info)
    {
        String url = getPhotoUrl + info.getDirectoryPath() + "/" + info.getFilename() + "/info";
        Log.v(TAG, "updateCameraFileInfo() GET URL : " + url);
        try
        {
            String response = SimpleHttpClient.httpGet(url, timeoutValue);
            if ((response == null)||(response.length() < 1))
            {
                return;
            }
            JSONObject object = new JSONObject(response);

            // データを突っ込む
            boolean captured = object.getBoolean("captured");
            String av = getJSONString(object, "av");
            String tv = getJSONString(object, "tv");
            String sv = getJSONString(object,"sv");
            String xv = getJSONString(object,"xv");
            int orientation = object.getInt("orientation");
            String aspectRatio = getJSONString(object,"aspectRatio");
            String cameraModel = getJSONString(object,"cameraModel");
            String latLng = getJSONString(object,"latlng");
            String dateTime = object.getString("datetime");
            info.updateValues(dateTime, av, tv, sv, xv, orientation, aspectRatio, cameraModel, latLng, captured);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private String getJSONString(JSONObject object, String key)
    {
        String value = "";
        try
        {
            value = object.getString(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (value);
    }

    @Override
    public void getContentInfo(@Nullable String path, @NonNull String name, @NonNull IContentInfoCallback callback)
    {
        String url = getPhotoUrl + name + "/info";
        Log.v(TAG, "getContentInfo() GET URL : " + url);
        try
        {
            String response = SimpleHttpClient.httpGet(url, timeoutValue);
            if ((response == null)||(response.length() < 1))
            {
                callback.onErrorOccurred(new NullPointerException());
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadContentScreennail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        //Log.v(TAG, "downloadContentScreennail() : " + path);
        String suffix = "?size=view";
        String url = getPhotoUrl + name + suffix;
        Log.v(TAG, "downloadContentScreennail() GET URL : " + url);
        try
        {
            Bitmap bmp = SimpleHttpClient.httpGetBitmap(url, null, timeoutValue);
            HashMap<String, Object> map = new HashMap<>();
            map.put("Orientation", 0);
            callback.onCompleted(bmp, map);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadContentThumbnail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        //Log.v(TAG, "downloadContentThumbnail() : " + path);
        String suffix = "?size=view";
        if (name.contains(".JPG"))
        {
            suffix = "?size=thumb";
        }
        String url = getPhotoUrl + name + suffix;
        Log.v(TAG, "downloadContentThumbnail() GET URL : " + url);
        try
        {
            Bitmap bmp = SimpleHttpClient.httpGetBitmap(url, null, timeoutValue);
            HashMap<String, Object> map = new HashMap<>();
            map.put("Orientation", 0);
            callback.onCompleted(bmp, map);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            callback.onErrorOccurred(new NullPointerException());
        }
   }

    @Override
    public void downloadContent(@Nullable String path, @NonNull String  name, boolean isSmallSize, @NonNull final IDownloadContentCallback callback)
    {
        Log.v(TAG, "downloadContent() : " + name);
        String suffix = "?size=full";
        if (isSmallSize)
        {
            suffix = "?size=view";
        }
        String url = getPhotoUrl + name + suffix;
        Log.v(TAG, "downloadContent() GET URL : " + url);
        try
        {
            SimpleHttpClient.httpGetBytes(url, null, timeoutValue, new SimpleHttpClient.IReceivedMessageCallback() {
                @Override
                public void onCompleted() {
                    callback.onCompleted();
                }

                @Override
                public void onErrorOccurred(Exception e) {
                    callback.onErrorOccurred(e);
                }

                @Override
                public void onReceive(int readBytes, int length, int size, byte[] data) {
                    float percent = (length == 0) ? 0.0f : ((float) readBytes / (float) length);
                    ProgressEvent event = new ProgressEvent(percent, null);
                    callback.onProgress(data, size, event);
                }
            });
        }
        catch (Throwable e)
        {
            e.printStackTrace();
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
