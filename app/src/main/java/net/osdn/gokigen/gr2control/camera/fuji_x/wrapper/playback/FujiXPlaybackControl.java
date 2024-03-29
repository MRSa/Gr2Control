package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.playback;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.FujiXInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetFullImage;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetImageInfo;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetScreenNail;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.GetThumbNail;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.SetPropertyValue;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContent;
import net.osdn.gokigen.gr2control.camera.playback.IContentInfoCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

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
        Log.v(TAG, " updateCameraFileInfo() : " + info.getDatetime());
    }

    @Override
    public void downloadContentScreennail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean useSmallImage = preferences.getBoolean(IPreferencePropertyAccessor.FUJI_X_GET_SCREENNAIL_AS_SMALL_PICTURE, false);
        if (useSmallImage)
        {
            // small image を表示する
            downloadContentScreennailImpl(path, name, callback);
        }
        else
        {
            // Thumbnail と同じ画像を表示する
            downloadContentThumbnail(path, name, callback);
        }
    }

    private void downloadContentScreennailImpl(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        try
        {
            Log.v(TAG, " ----- downloadContentScreennailImpl() ");
            int start = 0;
            if (name.indexOf("/") == 0)
            {
                start = 1;
            }
            Log.v(TAG, "  downloadContentThumbnail() : " + path + " " + name);
            int index = getIndexNumber(start, name);
            if ((index > 0)&&(index <= imageContentInfo.size()))
            {
                IFujiXCommandPublisher publisher = provider.getCommandPublisher();
                FujiXImageContentInfo contentInfo = imageContentInfo.get(index);
                if (contentInfo.isReceived())
                {
                    if (!contentInfo.isMovie())
                    {
                        // スモール画像を取得する (たぶんこのシーケンスでいけるはず...）
                        publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyReceiver(), 0xd226, 2, 0x0001));
                        publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyReceiver(), 0xd227, 2, 0x0001));
                        publisher.enqueueCommand(new GetScreenNail(index, 0x00800000, new FujiXThumbnailImageReceiver(activity, callback)));
                        publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyReceiver(), 0xd226, 2, 0x0000));
                        publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyReceiver(), 0xd227, 2, 0x0001));
                    }
                    else
                    {
                        // movieの時は、Small画像を使えないのでThumbnailで代用する。
                        publisher.enqueueCommand(new GetThumbNail(index, new FujiXThumbnailImageReceiver(activity, callback)));
                    }
                }
                else
                {
                    // まだ、ファイル情報を受信していない場合は、サムネイルの情報を流用する
                    publisher.enqueueCommand(new GetImageInfo(index, index, contentInfo));
                    publisher.enqueueCommand(new GetThumbNail(index, new FujiXThumbnailImageReceiver(activity, callback)));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadContentThumbnail(@Nullable String path, @NonNull String name, @NonNull IDownloadThumbnailImageCallback callback)
    {
        try
        {
            int start = 0;
            if (name.indexOf("/") == 0)
            {
                start = 1;
            }
            Log.v(TAG, "  downloadContentThumbnail() : " + path + " " + name);
            int index = getIndexNumber(start, name);
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
            if (name.indexOf("/") == 0)
            {
                start = 1;
            }
            int index = getIndexNumber(start, name);
            Log.v(TAG, "  FujiX::downloadContent() : " + path + " " + name + " " + index);
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

    private int getIndexNumber(int start, @NonNull String name)
    {
        String indexStr = name.substring(start, name.indexOf("."));
        int indexNo = -1;
        try
        {
            indexNo = Integer.parseInt(indexStr);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        if (indexNo >= 0)
        {
            return (indexNo);
        }
        indexStr = name.substring(start);
        int size = imageContentInfo.size();
        for (int index = 0; index < size; index++)
        {
            FujiXImageContentInfo info = imageContentInfo.valueAt(index);
            String contentName = info.getOriginalName();
            if (indexStr.matches(contentName))
            {
                return (info.getId());
            }
            Log.v(TAG, " contentName : " + contentName);
        }
        Log.v(TAG, "index is not found : " + name + " " + indexStr);
        return (-1);
    }

    @Override
    public void showPictureStarted()
    {
        try
        {
            Log.v(TAG, "   showPictureStarted() ");

            IFujiXCommandPublisher publisher = provider.getCommandPublisher();
            publisher.flushHoldQueue();
            System.gc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void showPictureFinished()
    {
        try
        {
            Log.v(TAG, "   showPictureFinished() ");

            IFujiXCommandPublisher publisher = provider.getCommandPublisher();
            publisher.flushHoldQueue();
            System.gc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        Log.v(TAG, " FujiXPlaybackControl::getCameraFileInfoList() ");
        List<ICameraFileInfo> fileInfoList = new ArrayList<>();
        try
        {
            int listSize = imageContentInfo.size();
            for(int index = 0; index < listSize; index++)
            {
                FujiXImageContentInfo info = imageContentInfo.valueAt(index);
                fileInfoList.add(info);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (fileInfoList);
    }

}
