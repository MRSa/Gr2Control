package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class RicohGr2StatusChecker implements ICameraStatusWatcher, ICameraStatus
{
    private final String TAG = toString();
    private final String statusCheckUrl = "http://192.168.0.1/v1/props";
    private final String statusSetUrl = "http://192.168.0.1/v1/params/camera";
    private final String grCommandUrl = "http://192.168.0.1/_gr";
    private final int sleepMs;

    private int timeoutMs = 5000;
    private boolean whileFetching = false;
    private RicohGr2StatusHolder statusHolder;

    /**
     *
     *
     */
    RicohGr2StatusChecker(int sleepMs)
    {
        this.sleepMs = sleepMs;
    }

    /**
     *
     *
     */
    @Override
    public void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier)
    {
        Log.v(TAG, "startStatusWatch()");
        try
        {
            this.statusHolder = new RicohGr2StatusHolder(notifier);
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        start(statusCheckUrl);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     */
    @Override
    public void stoptStatusWatch()
    {
        Log.v(TAG, "stoptStatusWatch()");
        whileFetching = false;
    }

    /**
     *
     *
     */
    private void start(@NonNull final String watchUrl)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.");
            return;
        }

        try
        {
            whileFetching = true;
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(TAG, "Start status watch.");
                    while (whileFetching)
                    {
                        try
                        {
                            statusHolder.updateStatus(SimpleHttpClient.httpGet(watchUrl, timeoutMs));
                            Thread.sleep(sleepMs);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Log.v(TAG, "STATUS WATCH STOPPED.");
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public @NonNull List<String> getStatusList(@NonNull final String key)
    {
        try
        {
            if (statusHolder == null)
            {
                return (new ArrayList<>());
            }
            String listKey = key + "List";
            return (statusHolder.getAvailableItemList(listKey));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (new ArrayList<>());
    }

    @Override
    public String getStatus(@NonNull String key)
    {
        try
        {
            if (statusHolder == null)
            {
                return ("");
            }
            return (statusHolder.getItemStatus(key));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("");
    }

    @Override
    public void setStatus(@NonNull final String key, @NonNull final String value)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String response;
                    String postData = key + "=" + value;
                    if (key.equals("exposureMode"))
                    {
                        //  撮影モードを変更するときは、GR専用コマンドを送ることにする。
                        postData = "cmd=" + decideButtonCode(value);
                        response = SimpleHttpClient.httpPost(grCommandUrl, postData, timeoutMs);
                        Log.v(TAG, "CHANGE MODE : " + postData + " resp. (" + response.length() + "bytes.)");
                    } else {
                        // 通常の変更コマンド
                        response = SimpleHttpClient.httpPut(statusSetUrl, postData, timeoutMs);
                        Log.v(TAG, "SET PROPERTY : " + postData + " resp. (" + response.length() + "bytes.)");
                    }
                    //  GR専用コマンドで、画面表示をリフレッシュ
                    response = SimpleHttpClient.httpPost(grCommandUrl, "cmd=mode refresh", timeoutMs);
                    Log.v(TAG, "refresh resp. (" + response.length() + "bytes.)");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        try
        {
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   撮影モードをGRのダイアルコマンドに変更する
     *
     */
    private String decideButtonCode(String exposureMode)
    {
        String buttonCode = "bdial AUTO";
        if (exposureMode == null)
        {
            return (buttonCode);
        }
        switch (exposureMode)
        {
            case "movie":
                buttonCode = "bdial MOVIE";
                break;
            case "M":
                buttonCode = "bdial M";
                break;
            case "TAV":
                buttonCode = "bdial TAV";
                break;
            case "AV":
                buttonCode = "bdial AV";
                break;
            case "TV":
                buttonCode = "bdial TV";
                break;
            case "P":
                buttonCode = "bdial P";
                break;
            case "auto":
                buttonCode = "bdial AUTO";
                break;
        }
        return (buttonCode);
    }
}
