package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.playback;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.FujiXInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetFullImage;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetImageInfo;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetThumbNail;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContent;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;

import java.util.ArrayList;
import java.util.List;

import static net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXCameraProperties.IMAGE_FILE_COUNT_STR_ID;

public class FujiXPlaybackControl implements IPlaybackControl, IFujiXCommandCallback
{
    private final String TAG = toString();
    private final Activity activity;
    private final FujiXInterfaceProvider provider;
    //private List<ICameraContent> imageInfo;
    private SparseArray<FujiXImageContentInfo> imageContentInfo;

    private int indexNumber = 0;
    private ICameraContentListCallback finishedCallback = null;

    public FujiXPlaybackControl(Activity activity, FujiXInterfaceProvider provider)
    {
        this.activity = activity;
        this.provider = provider;
        this.imageContentInfo = new SparseArray<>();
    }

    @Override
    public String getRawFileSuffix() {
        return (null);
    }

    @Override
    public void downloadContentList(@NonNull final ICameraContentListCallback callback)
    {
        try
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getCameraContents(callback);
                }
            });
            thread.start();
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
        // showFileInformation

    }

    @Override
    public void updateCameraFileInfo(ICameraFileInfo info)
    {
        //  なにもしない
    }

    @Override
    public void downloadContentScreennail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        // Thumbnail と同じ画像を表示する
        downloadContentThumbnail(path, name, callback);
    }

    @Override
    public void downloadContentThumbnail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        try
        {
            int start = 0;
            if (path.indexOf("/") == 0)
            {
                start = 1;
            }
            String indexStr = path.substring(start, path.indexOf("."));
            Log.v(TAG, "downloadContentThumbnail() : " + path + " " + indexStr);
            int index = Integer.parseInt(indexStr);
            if ((index > 0)&&(index <= imageContentInfo.size()))
            {
                IFujiXCommandPublisher publisher = provider.getCommandPublisher();
                FujiXImageContentInfo contentInfo = imageContentInfo.get(index);
                if (!contentInfo.isReceived())
                {
                    publisher.enqueueCommand(new GetImageInfo(index, index, contentInfo));
                }
                publisher.enqueueCommand(new GetThumbNail(index, new FujiXThumbnailImageReceiver(activity, callback)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadContent(@Nullable String path, @NonNull String name, boolean isSmallSize, @NonNull IDownloadContentCallback callback)
    {
        try
        {
            int start = 0;
            if (path.indexOf("/") == 0)
            {
                start = 1;
            }
            String indexStr = path.substring(start, path.indexOf("."));
            Log.v(TAG, "FujiX::downloadContent() : " + path + " " + indexStr);
            int index = Integer.parseInt(indexStr);
            //FujiXImageContentInfo contentInfo = imageContentInfo.get(index);   // 特にデータを更新しないから大丈夫か？
            if ((index > 0)&&(index <= imageContentInfo.size()))
            {
                IFujiXCommandPublisher publisher = provider.getCommandPublisher();
                publisher.enqueueCommand(new GetFullImage(index, new FujiXFullImageReceiver(callback)));
            }
        }
        catch (Exception e)
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

    private void getCameraContents(ICameraContentListCallback callback)
    {
        int nofFiles = -1;
        try {
            finishedCallback = callback;
            ICameraStatus statusListHolder = provider.getCameraStatusListHolder();
            if (statusListHolder != null) {
                String count = statusListHolder.getStatus(IMAGE_FILE_COUNT_STR_ID);
                nofFiles = Integer.parseInt(count);
                Log.v(TAG, "getCameraContents() : " + nofFiles + " (" + count + ")");
            }
            Log.v(TAG, "getCameraContents() : DONE.");
            if (nofFiles > 0)
            {
                // 件数ベースで取得する(情報は、後追いで反映させる...この方式だと、キューに積みまくってるが、、、)
                checkImageFiles(nofFiles);
            }
            else
            {
                // 件数が不明だったら、１件づつインデックスの情報を取得する
                checkImageFileAll();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            finishedCallback.onErrorOccurred(e);
            finishedCallback = null;
        }
    }

    /**
     *   最初から取得可能なイメージ情報を(件数ベースで)取得する
     *
     */
    private void checkImageFiles(int nofFiles)
    {
        try
        {
            imageContentInfo.clear();
            //IFujiXCommandPublisher publisher = provider.getCommandPublisher();
            //for (int index = nofFiles; index > 0; index--)
            for (int index = 1; index <= nofFiles; index++)
            {
                // ファイル数分、仮のデータを生成する
                imageContentInfo.append(index, new FujiXImageContentInfo(index, null));

                //ファイル名などを取得する (メッセージを積んでおく...でも遅くなるので、ここではやらない方がよいかな。）
                //publisher.enqueueCommand(new GetImageInfo(index, index, info));
            }

            // インデックスデータがなくなったことを検出...データがそろったとして応答する。
            Log.v(TAG, "IMAGE LIST : " + imageContentInfo.size() + " (" + nofFiles + ")");
            finishedCallback.onCompleted(getCameraFileInfoList());
            finishedCallback = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   最初から取得可能なイメージ情報をすべて取得する
     *
     */
    private void checkImageFileAll()
    {
        try
        {
            imageContentInfo.clear();
            indexNumber = 1;
            IFujiXCommandPublisher publisher = provider.getCommandPublisher();
            publisher.enqueueCommand(new GetImageInfo(indexNumber, indexNumber, this));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] body)
    {
        Log.v(TAG, " " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        // イメージ数の一覧が取得できなかった場合にここで作る。
        if (rx_body.length < 16)
        {
            // インデックスデータがなくなったことを検出...データがそろったとして応答する。
            Log.v(TAG, "IMAGE LIST : " + imageContentInfo.size());
            finishedCallback.onCompleted(getCameraFileInfoList());
            finishedCallback = null;
            return;
        }
        try
        {
            Log.v(TAG, "RECEIVED IMAGE INFO : " + indexNumber);

            // 受信データを保管しておく
            imageContentInfo.append(indexNumber, new FujiXImageContentInfo(indexNumber, rx_body));

            // 次のインデックスの情報を要求する
            indexNumber++;
            IFujiXCommandPublisher publisher = provider.getCommandPublisher();
            publisher.enqueueCommand(new GetImageInfo(indexNumber, indexNumber, this));
        }
        catch (Exception e)
        {
            // エラーになったら、そこで終了にする
            e.printStackTrace();
            finishedCallback.onCompleted(getCameraFileInfoList());
            finishedCallback = null;
        }
    }

    private List<ICameraContent> getCameraContentList()
    {
        /// ダサいけど...コンテナクラスを詰め替えて応答する
        List<ICameraContent> contentList = new ArrayList<>();
        int listSize = imageContentInfo.size();
        for(int index = 0; index < listSize; index++)
        {
            contentList.add(imageContentInfo.valueAt(index));
        }
        return (contentList);
    }

    private List<ICameraFileInfo> getCameraFileInfoList()
    {
        List<ICameraFileInfo> fileInfoList = new ArrayList<>();
        try
        {
            int listSize = imageContentInfo.size();
            for(int index = 0; index < listSize; index++)
            {
                FujiXImageContentInfo info = imageContentInfo.valueAt(index);
                fileInfoList.add(info.getCameraFileInfo());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (fileInfoList);
    }

}
