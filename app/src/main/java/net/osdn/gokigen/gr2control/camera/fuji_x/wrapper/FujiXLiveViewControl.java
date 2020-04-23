package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommunication;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.CameraLiveViewListenerImpl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

import static net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor.FUJI_X_LIVEVIEW_WAIT;
import static net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor.FUJI_X_LIVEVIEW_WAIT_DEFAULT_VALUE;

public class FujiXLiveViewControl implements ILiveViewControl, IFujiXCommunication
{
    private final String TAG = toString();
    private final String ipAddress;
    private final int portNumber;
    private final CameraLiveViewListenerImpl liveViewListener;
    private int waitMs = 0;
    //private static final int DATA_HEADER_OFFSET = 18;
    private static final int BUFFER_SIZE = 2048 * 1280;
    private static final int ERROR_LIMIT = 30;
    private boolean isStart = false;

    FujiXLiveViewControl(@NonNull Activity activity, String ip, int portNumber)
    {
        this.ipAddress = ip;
        this.portNumber = portNumber;
        liveViewListener = new CameraLiveViewListenerImpl();

        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String waitMsStr = preferences.getString(FUJI_X_LIVEVIEW_WAIT, FUJI_X_LIVEVIEW_WAIT_DEFAULT_VALUE);
            Log.v(TAG, "waitMS : " + waitMsStr);
            int wait = Integer.parseInt(waitMsStr);
            if ((wait >= 20)&&(wait <= 800))
            {
                waitMs = wait;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            waitMs = 100;
        }
        Log.v(TAG, "LOOP WAIT : " + waitMs + " ms");
    }

    @Override
    public void startLiveView(boolean isCameraScreen)
    {
        if (isStart)
        {
            // すでに受信スレッド動作中なので抜ける
            return;
        }
        isStart = true;
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Socket socket = new Socket(ipAddress, portNumber);
                    startReceive(socket);
                }
                catch (Exception e)
                {
                    Log.v(TAG, " IP : " + ipAddress + " port : " + portNumber);
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

    @Override
    public void stopLiveView()
    {
        isStart = false;
    }
/*
    private void startReceivePrevious(Socket socket)
    {
        String lvHeader = "[LV]";
        int lvHeaderDumpBytes = 24;

        int errorCount = 0;
        InputStream isr;
        byte[] byteArray;
        try
        {
            isr = socket.getInputStream();
            byteArray = new byte[BUFFER_SIZE + 32];
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v(TAG, "===== startReceive() aborted.");
            return;
        }
        while (isStart)
        {
            try
            {
                boolean findJpeg = false;
                int length_bytes;
                int read_bytes = isr.read(byteArray, 0, BUFFER_SIZE);
                Log.v(TAG, " >>>  READ LIVEVIEW <<< " + read_bytes + " bytes...");
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                if (read_bytes > DATA_HEADER_OFFSET)
                {
                    // 先頭データ(64バイト分)をダンプ
                    dump_bytes("[lv]", byteArray, 64);

                    // メッセージボディの先頭にあるメッセージ長分は読み込む
                    length_bytes = ((((int) byteArray[3]) & 0xff) << 24) + ((((int) byteArray[2]) & 0xff) << 16) + ((((int) byteArray[1]) & 0xff) << 8) + (((int) byteArray[0]) & 0xff);

                    if ((byteArray[18] == (byte)0xff)&&(byteArray[19] == (byte)0xd8))
                    {
                        findJpeg = true;
                        while ((read_bytes < length_bytes) && (read_bytes < BUFFER_SIZE) && (length_bytes <= BUFFER_SIZE))
                        {
                            int append_bytes = isr.read(byteArray, read_bytes, length_bytes - read_bytes);
                            logcat("READ AGAIN : " + append_bytes + " [" + read_bytes + "]");
                            if (append_bytes < 0)
                            {
                                break;
                            }
                            read_bytes = read_bytes + append_bytes;
                        }
                        logcat("READ BYTES : " + read_bytes + "  (" + length_bytes + " bytes, " + waitMs + "ms)");
                    }
                    else
                    {
                        // ウェイトを短めに入れてマーカーを拾うまで待つ
                        Thread.sleep(waitMs/4);
                        Log.v(TAG, " NOT FOUND MARKER...");
                        continue;
                    }
                }

                // 先頭データ(24バイト分)をダンプ
                dump_bytes(lvHeader, byteArray, lvHeaderDumpBytes);

                if (findJpeg)
                {
                    liveViewListener.onUpdateLiveView(Arrays.copyOfRange(byteArray, DATA_HEADER_OFFSET, read_bytes - DATA_HEADER_OFFSET), null);
                    errorCount = 0;
                }
                Thread.sleep(waitMs);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errorCount++;
            }
            if (errorCount > ERROR_LIMIT)
            {
                // エラーが連続でたくさん出たらループをストップさせる
                isStart = false;
            }
        }
        try
        {
            isr.close();
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
*/

    @Override
    public void updateDigitalZoom()
    {

    }

    @Override
    public void updateMagnifyingLiveViewScale(boolean isChangeScale)
    {

    }

    @Override
    public float getMagnifyingLiveViewScale()
    {
        return (1.0f);
    }

    @Override
    public void changeLiveViewSize(String size)
    {

    }

    @Override
    public float getDigitalZoomScale()
    {
        return (1.0f);
    }

    public ILiveViewListener getLiveViewListener()
    {
        return (liveViewListener);
    }

    @Override
    public boolean connect()
    {
        return (true);
    }

    @Override
    public void disconnect()
    {
        isStart = false;
    }

    private void startReceive(Socket socket)
    {
        //String lvHeader = "[LV]";
        //int lvHeaderDumpBytes = 24;

        int errorCount = 0;
        InputStream isr;
        byte[] byteArray;
        try
        {
            isr = socket.getInputStream();
            byteArray = new byte[BUFFER_SIZE + 32];
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v(TAG, "===== startReceive() aborted.");
            return;
        }

        boolean findJpeg = false;
        boolean finishJpeg = false;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        while (isStart)
        {
            try
            {
                int read_bytes = isr.read(byteArray, 0, BUFFER_SIZE);
                int index = 0;
                int lastIndex = read_bytes - 1;
                if (!findJpeg)
                {
                    while (index < (read_bytes - 1))
                    {
                        if ((byteArray[index] == (byte) 0xff) && (byteArray[index + 1] == (byte) 0xd8))
                        {
                            findJpeg = true;
                            //Log.v(TAG, " ----- FIND TOP : " + index + " " + lastIndex);
                            break;
                        }
                        index++;
                    }
                    //Log.v(TAG, " =-=-= ");
                }
                if (findJpeg)
                {
                    while (lastIndex > 0)
                    {
                        if ((byteArray[lastIndex - 1] == (byte) 0xff) && (byteArray[lastIndex] == (byte) 0xd9))
                        {
                            finishJpeg = true;
                            //Log.v(TAG, " ----- FIND BOTTOM : " + index + " " + lastIndex);
                            break;
                        }
                        lastIndex--;
                    }
                    //Log.v(TAG, " ===== ");
                }
                if (finishJpeg)
                {
                    try
                    {
                        //Log.v(TAG, " WRITE BUFFER : " + index + " " + lastIndex);
                        if (index < lastIndex)
                        {
                            byteStream.write(byteArray, index, lastIndex);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    if (!findJpeg)
                    {
                        try
                        {
                            byteStream.write(byteArray, index, read_bytes);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //Log.v(TAG, " ----- CONTINUE : " + findJpeg + " " + finishJpeg + " " + read_bytes);
                    continue;
                }
                {
                    byte[] imageData = byteStream.toByteArray();
                    //dump_bytes("[lv]", imageData, 64);
                    //dump_bytes("[LV]", Arrays.copyOfRange(imageData, imageData.length - 64, imageData.length), 64);
                    liveViewListener.onUpdateLiveView(imageData, null);
                    //liveViewListener.onUpdateLiveView(Arrays.copyOfRange(byteArray, DATA_HEADER_OFFSET, read_bytes - DATA_HEADER_OFFSET), null);
                    errorCount = 0;
                    findJpeg = false;
                    finishJpeg = false;
                    byteStream = new ByteArrayOutputStream();
                }
                Thread.sleep(waitMs);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errorCount++;
            }
            if (errorCount > ERROR_LIMIT)
            {
                // エラーが連続でたくさん出たらループをストップさせる
                isStart = false;
            }
        }
        try
        {
            isr.close();
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
