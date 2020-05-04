package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.utils.SimpleHttpClient;
import net.osdn.gokigen.gr2control.camera.utils.SimpleLiveviewSlicer;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.CameraLiveViewListenerImpl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

import androidx.annotation.NonNull;

/**
 *
 *
 */
public class RicohGr2LiveViewControl implements ILiveViewControl
{
    private final String TAG = toString();
    private final CameraLiveViewListenerImpl liveViewListener;
    private String cameraDisplayUrl = "http://192.168.0.1/v1/display";    //  カメラの画面をコピーする場合...
    private String liveViewUrl = "http://192.168.0.1/v1/liveview";         //  何も表示しない（ライブビューモード）の場合...
    private float cropScale = 1.0f;
    private boolean whileFetching = false;
    private static final int FETCH_ERROR_MAX = 30;
    private boolean useGR2command = false;

    /**
     *
     *
     */
    RicohGr2LiveViewControl()
    {
        liveViewListener = new CameraLiveViewListenerImpl();
    }

/*
    public void setLiveViewAddress(@NonNull String address, @NonNull String page)
    {
        cameraDisplayUrl = "http://" + address + "/" + page;
    }
*/

    public void setUseGR2Command(boolean useGR2command)
    {
        this.useGR2command = useGR2command;
    }

    @Override
    public void changeLiveViewSize(String size)
    {
        //
    }

    @Override
    public void startLiveView(final boolean useCameraScreen)
    {
        final boolean isCameraScreen = useGR2command && useCameraScreen;
        Log.v(TAG, "startLiveView()");
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (isCameraScreen)
                        {
                            start(cameraDisplayUrl);
                        }
                        else
                        {
                            start(liveViewUrl);
                        }
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

    @Override
    public void stopLiveView()
    {
        Log.v(TAG, "stopLiveView()");
        whileFetching = false;
    }


    private void start(@NonNull final String streamUrl)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.");
        }
        whileFetching = true;

        // A thread for retrieving liveview data from server.
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(TAG, "Starting retrieving streaming data from server.");
                    SimpleLiveviewSlicer slicer = null;
                    int continuousNullDataReceived = 0;
                    try
                    {
                        // Create Slicer to open the stream and parse it.
                        slicer = new SimpleLiveviewSlicer();
                        slicer.open(streamUrl);

                        while (whileFetching)
                        {
                            final SimpleLiveviewSlicer.Payload payload = slicer.nextPayloadForMotionJpeg();
                            if (payload == null)
                            {
                                //Log.v(TAG, "Liveview Payload is null.");
                                continuousNullDataReceived++;
                                if (continuousNullDataReceived > FETCH_ERROR_MAX)
                                {
                                    Log.d(TAG, " FETCH ERROR MAX OVER ");
                                    break;
                                }
                                continue;
                            }
                            //if (mJpegQueue.size() == 2)
                            //{
                            //    mJpegQueue.remove();
                            //}
                            //mJpegQueue.add(payload.getJpegData());
                            liveViewListener.onUpdateLiveView(payload.getJpegData(), null);
                            continuousNullDataReceived = 0;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            if (slicer != null)
                            {
                                slicer.close();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        //mJpegQueue.clear();
                        if ((!whileFetching)&&(continuousNullDataReceived > FETCH_ERROR_MAX))
                        {
                            // 再度ライブビューのスタートをやってみる。
                            whileFetching = false;
                            //continuousNullDataReceived = 0;
                            start(streamUrl);
                        }
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


    @Override
    public void updateDigitalZoom()
    {

    }

    /**
     *   デジタルズーム倍率の設定値を応答する
     *
     */
    @Override
    public float getDigitalZoomScale()
    {
        return (1.0f);
    }

    /**
     *   クロップサイズを変更する
     *
     */
    @Override
    public void updateMagnifyingLiveViewScale(final boolean isChangeScale)
    {
        //
        try
        {
            if (isChangeScale)
            {
                if (cropScale == 1.0f)
                {
                    cropScale = 1.25f;
                }
                else if (cropScale == 1.25f)
                {
                    cropScale = 1.68f;
                }
                else
                {
                    cropScale = 1.0f;
                }
            }
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String cropSize = "CROP_SIZE_ORIGINAL";
                        int timeoutMs = 5000;
                        String grCmdUrl = "http://192.168.0.1/_gr";
                        String postData;
                        String result;
                        if (isChangeScale)
                        {
                            postData = "mpget=CROP_SHOOTING";
                            result = SimpleHttpClient.httpPost(grCmdUrl, postData, timeoutMs);
                            if ((result == null) || (result.length() < 1))
                            {
                                Log.v(TAG, "reply is null.");
                                cropScale = 1.0f;
                            } else if (result.contains("SIZE_M")) {
                                cropSize = "CROP_SIZE_S";
                                cropScale = 1.68f;
                            } else if (result.contains("SIZE_S")) {
                                cropSize = "CROP_SIZE_ORIGINAL";
                                cropScale = 1.0f;
                            } else {
                                cropSize = "CROP_SIZE_M";
                                cropScale = 1.25f;
                            }
                        }
                        postData = "mpset=CROP_SHOOTING " + cropSize;
                        result = SimpleHttpClient.httpPost(grCmdUrl, postData, timeoutMs);
                        Log.v(TAG, "RESULT1 : " + result);

                        postData = "cmd=mode refresh";
                        result = SimpleHttpClient.httpPost(grCmdUrl, postData, timeoutMs);
                        Log.v(TAG, "RESULT2 : " + result);
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
     *   ライブビュー拡大倍率の設定値を応答する
     *
     */
    @Override
    public float getMagnifyingLiveViewScale()
    {
        return (cropScale);
    }

    public ILiveViewListener getLiveViewListener()
    {
        return (liveViewListener);
    }
}
