package net.osdn.gokigen.gr2control.camera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
    public static byte[] httpGetBytes(String url, int timeoutMs)
    {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        byte[] receivedData = new byte[0];

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
                return (receivedData);
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
            return (receivedData);
        }

        // 応答を確認する
        BufferedReader reader = null;
        int count = 0;
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            int c;
            while ((c = reader.read()) != -1)
            {
                out.write(c);
                count++;
            }
            receivedData = out.toByteArray();
            Log.v(TAG, "RECEIVED " + count + " BYTES. ");
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
        return (receivedData);
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
}
