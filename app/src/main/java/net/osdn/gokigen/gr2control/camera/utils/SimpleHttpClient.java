package net.osdn.gokigen.gr2control.camera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 *
 *
 */
public class SimpleHttpClient
{
    private static final String TAG = SimpleHttpClient.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 10 * 1000; // [ms]
    private static final int BUFFER_SIZE = 4096;

    public SimpleHttpClient()
    {
        Log.v(TAG, "SimpleHttpClient()");
    }

    /**
     *
     *
     *
     */
    public static String httpGet(String url, int timeoutMs)
    {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        String replyString = "";

        int timeout = timeoutMs;
        if (timeoutMs < 0)
        {
            timeout = DEFAULT_TIMEOUT;
        }

        //  HTTP GETメソッドで要求を投げる
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null)
            {
                Log.w(TAG, "httpGet: Response Code Error: " + responseCode + ": " + url);
                return ("");
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpGet: " + url + "  " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            return ("");
        }

        // 応答を確認する
        BufferedReader reader = null;
        try
        {
            StringBuilder responseBuf = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            int c;
            while ((c = reader.read()) != -1)
            {
                responseBuf.append((char) c);
            }
            replyString = responseBuf.toString();
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpGet: exception: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return (replyString);
    }

    /**
     *
     *
     *
     */
    public static void httpGetBytes(String url, int timeoutMs, @NonNull IReceivedMessageCallback callback)
    {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        int timeout = timeoutMs;
        if (timeoutMs < 0)
        {
            timeout = DEFAULT_TIMEOUT;
        }

        //  HTTP GETメソッドで要求を投げる
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null)
            {
                Log.w(TAG, "httpGet: Response Code Error: " + responseCode + ": " + url);
                callback.onErrorOccurred(new NullPointerException());
                callback.onCompleted();
                return;
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpGet: " + url + "  " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            callback.onErrorOccurred(e);
            callback.onCompleted();
            return;
        }

        // 応答を確認する
        try
        {
            int contentLength = httpConn.getContentLength();
            byte[] buffer = new byte[BUFFER_SIZE];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int readBytes = 0;
            int readSize = bufferedInputStream.read(buffer, 0, BUFFER_SIZE);
            while (readSize != -1)
            {
                callback.onReceive(readBytes, contentLength, buffer);
                readBytes += readSize;
                readSize = bufferedInputStream.read(buffer, 0, BUFFER_SIZE);
            }
            Log.v(TAG, "RECEIVED " + readBytes + " BYTES. (contentLength : " + contentLength + ")");
            bufferedInputStream.close();
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpGet: exception: " + e.getMessage());
            e.printStackTrace();
            callback.onErrorOccurred(e);
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        callback.onCompleted();
    }

    /**
     *
     *
     *
     */
    public static Bitmap httpGetBitmap(String url, int timeoutMs)
    {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        Bitmap bmp = null;

        int timeout = timeoutMs;
        if (timeoutMs < 0)
        {
            timeout = DEFAULT_TIMEOUT;
        }

        //  HTTP GETメソッドで要求を投げる
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
                if (inputStream != null)
                {
                    bmp = BitmapFactory.decodeStream(inputStream);
                }
            }
            if (inputStream == null)
            {
                Log.w(TAG, "httpGet: Response Code Error: " + responseCode + ": " + url);
                return (null);
            }
            inputStream.close();
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpGet: " + url + "  " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            return (null);
        }
        return (bmp);
    }

    /**
     *
     *
     *
     */
    public static String httpPost(String url, String postData, int timeoutMs)
    {
        HttpURLConnection httpConn = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        InputStream inputStream = null;

        int timeout = timeoutMs;
        if (timeoutMs < 0)
        {
            timeout = DEFAULT_TIMEOUT;
        }

        //  HTTP Postメソッドで要求を送出
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);

            outputStream = httpConn.getOutputStream();
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(postData);
            writer.flush();
            writer.close();
            writer = null;
            outputStream.close();
            outputStream = null;

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null)
            {
                Log.w(TAG, "httpPost: Response Code Error: " + responseCode + ": " + url);
                return ("");
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "httpPost: IOException: " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            return ("");
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // 応答の読み出し
        BufferedReader reader = null;
        String replyString = "";
        try
        {
            StringBuilder responseBuf = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            int c;
            while ((c = reader.read()) != -1)
            {
                responseBuf.append((char) c);
            }
            replyString = responseBuf.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return (replyString);
    }

    public interface IReceivedMessageCallback
    {
        void onCompleted();
        void onErrorOccurred(Exception  e);
        void onReceive(int readBytes, int length, byte[] data);
    }
}
