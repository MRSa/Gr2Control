package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.CameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadLargeContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RicohGr2PlaybackControl implements IPlaybackControl
{
    private final String TAG = toString();
    private final String getPhotoUrl = "http://192.168.0.1/v1/photos/";
    private static final int DEFAULT_TIMEOUT = 5000;

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


    RicohGr2PlaybackControl()
    {

    }

    @Override
    public void downloadContentList(@NonNull IDownloadContentListCallback callback)
    {
        List<ICameraFileInfo> fileList = new ArrayList<>();
        String imageListurl = "http://192.168.0.1/v1/photos?limit=3000";
        String contentList;
        try
        {
            contentList = SimpleHttpClient.httpGet(imageListurl, DEFAULT_TIMEOUT);
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
            if (dirsArray != null)
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
    public void downloadContentScreennail(@NonNull String path, @NonNull IDownloadImageCallback callback)
    {
        Log.v(TAG, "downloadContentScreennail() : " + path);


    }

    @Override
    public void downloadContentThumbnail(@NonNull String path, @NonNull IDownloadThumbnailImageCallback callback)
    {
        //Log.v(TAG, "downloadContentThumbnail() : " + path);
        String suffix = "?size=view";
        if (path.contains(".JPG"))
        {
            suffix = "?size=thumb";
        }
        String url = getPhotoUrl + path + suffix;
        Log.v(TAG, "downloadContentThumbnail() GET URL : " + url);
        Bitmap bmp = SimpleHttpClient.httpGetBitmap(url, DEFAULT_TIMEOUT);
        HashMap<String, Object> map = new HashMap<>();
        map.put("Orientation", 0);
        callback.onCompleted(bmp, map);
   }

    @Override
    public void downloadImage(@NonNull String path, float resize, @NonNull IDownloadImageCallback callback)
    {
        Log.v(TAG, "downloadImage() " + path + " [" + resize + "]");

    }

    @Override
    public void downloadLargeContent(@NonNull String path, @NonNull IDownloadLargeContentCallback callback)
    {
        Log.v(TAG, "downloadLargeContent() : " + path);

    }
}
