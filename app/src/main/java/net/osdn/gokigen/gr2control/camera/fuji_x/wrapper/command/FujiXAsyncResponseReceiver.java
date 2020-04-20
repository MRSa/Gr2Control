package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.net.Socket;

public class FujiXAsyncResponseReceiver implements IFujiXCommunication
{
    private final String TAG = toString();
    private final String ipAddress;
    private final int portNumber;
    private static final int BUFFER_SIZE = 1280 + 8;
    private static final int WAIT_MS = 250;   // 250ms
    private static final int ERROR_LIMIT = 30;
    private IFujiXCommandCallback receiver = null;
    private boolean isStart = false;

    public FujiXAsyncResponseReceiver(@NonNull String ip, int portNumber)
    {
        this.ipAddress = ip;
        this.portNumber = portNumber;
    }

    public void setEventSubscriber(@NonNull IFujiXCommandCallback receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public boolean connect()
    {
        start();
        return (true);
    }

    @Override
    public void disconnect()
    {
        isStart = false;
    }

    public void start()
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

    public void stop()
    {
        isStart = false;
    }

    private void startReceive(Socket socket)
    {
        int errorCount = 0;
        InputStream isr;
        byte[] byte_array;
        try
        {
            isr = socket.getInputStream();
            byte_array = new byte[BUFFER_SIZE];

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v(TAG, "===== startReceive() aborted.");
            return;
        }
        Log.v(TAG, "startReceive() start.");
        while (isStart)
        {
            try
            {
                int read_bytes = isr.read(byte_array, 0, BUFFER_SIZE);
                Log.v(TAG, "RECEIVE ASYNC  : " + read_bytes + " bytes.");
                if (receiver != null)
                {
                    try
                    {
                        receiver.receivedMessage(0, byte_array);
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }
                Thread.sleep(WAIT_MS);
                errorCount = 0;
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
        Log.v(TAG, "startReceive() end.");
    }
}
