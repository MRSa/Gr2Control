package net.osdn.gokigen.gr2control.camera.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleLiveviewSlicer
{
    private static final String TAG = SimpleLiveviewSlicer.class.getSimpleName();
    public static final class Payload
    {
        // jpeg data container
        final byte[] jpegData;

        // padding data container
        final byte[] paddingData;

        /**
         * Constructor
         */
        private Payload(byte[] jpeg, byte[] padding)
        {
            this.jpegData = jpeg;
            this.paddingData = padding;
        }
        public byte[] getJpegData()
        {
            return (jpegData);
        }
    }

    private static final int CONNECTION_TIMEOUT = 2000; // [msec]
    private HttpURLConnection mHttpConn;
    private InputStream mInputStream;

    public void open(String liveviewUrl)
    {
        try
        {
            if ((mInputStream != null)||(mHttpConn != null))
            {
                Log.v(TAG, "Slicer is already open.");
                return;
            }

            final URL urlObj = new URL(liveviewUrl);
            mHttpConn = (HttpURLConnection) urlObj.openConnection();
            mHttpConn.setRequestMethod("GET");
            mHttpConn.setConnectTimeout(CONNECTION_TIMEOUT);
            mHttpConn.connect();
            if (mHttpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                mInputStream = mHttpConn.getInputStream();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        try
        {
            if (mInputStream != null)
            {
                mInputStream.close();
                mInputStream = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if (mHttpConn != null)
            {
                mHttpConn.disconnect();
                mHttpConn = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Payload nextPayload()
    {
        Payload payload = null;
        try
        {
            while ((mInputStream != null)&&(payload == null))
            {
                // Common Header
                int readLength = 1 + 1 + 2 + 4;
                byte[] commonHeader = readBytes(mInputStream, readLength);
                if ((commonHeader == null)||(commonHeader.length != readLength))
                {
                    Log.v(TAG, "Cannot read stream for common header.");
                    payload = null;
                    break;
                }
                if (commonHeader[0] != (byte) 0xFF)
                {
                    Log.v(TAG, "Unexpected data format. (Start byte)");
                    payload = null;
                    break;
                }
                switch (commonHeader[1])
                {
                    case (byte) 0x12:
                        // This is information header for streaming. skip this packet.
                        readLength = 4 + 3 + 1 + 2 + 118 + 4 + 4 + 24;
                        //commonHeader = null;
                        readBytes(mInputStream, readLength);
                        break;

                    case (byte) 0x01:
                    case (byte) 0x11:
                        payload = readPayload();
                        break;

                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.gc();
        }
        return (payload);
    }

    private Payload readPayload()
    {
        try
        {
            if (mInputStream != null)
            {
                // Payload Header
                int readLength = 4 + 3 + 1 + 4 + 1 + 115;
                byte[] payloadHeader = readBytes(mInputStream, readLength);
                if ((payloadHeader == null)||(payloadHeader.length != readLength))
                {
                    throw new EOFException("Cannot read stream for payload header.");
                }
                if (payloadHeader[0] != (byte) 0x24 || payloadHeader[1] != (byte) 0x35
                        || payloadHeader[2] != (byte) 0x68
                        || payloadHeader[3] != (byte) 0x79)
                {
                    throw new EOFException("Unexpected data format. (Start code)");
                }
                int jpegSize = bytesToInt(payloadHeader, 4, 3);
                int paddingSize = bytesToInt(payloadHeader, 7, 1);

                // Payload Data
                byte[] jpegData = readBytes(mInputStream, jpegSize);
                byte[] paddingData = readBytes(mInputStream, paddingSize);

                return (new Payload(jpegData, paddingData));
            }
        }
        catch (EOFException eo)
        {
            eo.printStackTrace();
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    private static int bytesToInt(byte[] byteData, int startIndex, int count)
    {
        int ret = 0;
        try
        {
            for (int i = startIndex; i < startIndex + count; i++)
            {
                ret = (ret << 8) | (byteData[i] & 0xff);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
    }

    private static byte[] readBytes(InputStream in, int length)
    {
        byte[] ret;
        try
        {
            ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true)
            {
                int trialReadlen = Math.min(buffer.length, length - tmpByteArray.size());
                int readlen = in.read(buffer, 0, trialReadlen);
                if (readlen < 0)
                {
                    break;
                }
                tmpByteArray.write(buffer, 0, readlen);
                if (length <= tmpByteArray.size())
                {
                    break;
                }
            }
            ret = tmpByteArray.toByteArray();
            tmpByteArray.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = null;
        }
        return (ret);
    }

    /**
     *   先頭のjpegマーカーが出てくるまで読み飛ばす
     *
     */
    private void skipJpegMarkStart(InputStream stream)
    {
        int searchIndex = 0;
        int[] startmarker = { 0x0d, 0x0a, 0x0d, 0x0a, 0xff, 0xd8 };
        while (true)
        {
            try
            {
                int data = stream.read();
                if (data == startmarker[searchIndex])
                {
                    searchIndex++;
                    if (searchIndex >= startmarker.length)
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     *
     *
     */
    public Payload nextPayloadForMotionJpeg()
    {
        int searchIndex = 0;
        //int[] endmarker = { 0xff, 0xd9, 0x0d, 0x0a, 0x0d, 0x0a };
        int[] endmarker = { 0xff, 0xd9 };
        Payload payload = null;
        try
        {
            while ((mInputStream != null)&&(payload == null))
            {
                skipJpegMarkStart(mInputStream);
                ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
                // 先頭にJPEGのマークを詰める
                tmpByteArray.write(0xff);
                tmpByteArray.write(0xd8);
                while (true)
                {
                    try
                    {
                        // 1byteづつの読み込み... 本当は複数バイト読み出しで処理したい
                        int data = mInputStream.read();
                        tmpByteArray.write(data);
                        if (data == endmarker[searchIndex])
                        {
                            searchIndex++;
                            if (searchIndex >= endmarker.length)
                            {
                                break;
                            }
                        }
                        else
                        {
                            searchIndex = 0;
                        }
                    }
                    catch (Throwable e)
                    {
                        Log.v(TAG, "INPUT STREAM EXCEPTION : " + e.getLocalizedMessage());
                        // e.printStackTrace();
                        return (null);
                    }
                }
                payload = new Payload(tmpByteArray.toByteArray(), null);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (payload);
    }
}
