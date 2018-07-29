package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

/**
 *
 *
 */
public class RicohGr2StatusChecker implements ICameraStatusWatcher
{
    private final String TAG = toString();
    private final String statusCheckUrl = "http://192.168.0.1/v1/props";
    private final int sleepMs;

    private int timeoutMs = 6000;
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
}
