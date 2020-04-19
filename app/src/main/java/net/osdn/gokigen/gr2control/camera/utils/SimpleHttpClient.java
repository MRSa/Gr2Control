package net.osdn.gokigen.gr2control.camera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *
 *
 *
 */
public class SimpleHttpClient
{
    private static final String TAG = SimpleHttpClient.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 10 * 1000; // [ms]
    private static final int BUFFER_SIZE = 131072 * 2; // 256kB

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
    public static void httpGetBytes(String url, Map<String, String> setProperty, int timeoutMs, @NonNull IReceivedMessageCallback callback)
    {
        httpCommandBytes(url, "GET", null, setProperty, null, timeoutMs, callback);
    }

    /**
     *
     *
     *
     */
    public static void httpPostBytes(String url, String postData, Map<String, String> setProperty, int timeoutMs, @NonNull IReceivedMessageCallback callback)
    {
        httpCommandBytes(url, "POST", postData, setProperty, null, timeoutMs, callback);
    }

    private static void httpCommandBytes(@NonNull String url, @NonNull String requestMethod, @Nullable String postData, @Nullable Map<String, String> setProperty, @Nullable String contentType, int timeoutMs, @NonNull IReceivedMessageCallback callback)
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

        //  HTTP メソッドで要求を送出
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod(requestMethod);
            if (setProperty != null)
            {
                for (String key : setProperty.keySet())
                {
                    String value = setProperty.get(key);
                    httpConn.setRequestProperty(key, value);
                }
            }
            if (contentType != null)
            {
                httpConn.setRequestProperty("Content-Type", contentType);
            }
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            if (postData == null)
            {
                httpConn.connect();
            }
            else
            {
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
            }

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null)
            {
                Log.w(TAG, " http " + requestMethod + " Response Code Error: " + responseCode + ": " + url);
                callback.onErrorOccurred(new NullPointerException());
                callback.onCompleted();
                return;
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "http " + requestMethod + " " + url + "  " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            callback.onErrorOccurred(e);
            callback.onCompleted();
            return;
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

        // 応答を確認する
        try
        {
            int contentLength = httpConn.getContentLength();
            if (contentLength < 0)
            {
                // コンテンツ長が取れない場合の処理...
                try
                {
                    Map<String, List<String>> headers = httpConn.getHeaderFields();

                    /*
                    // 応答ヘッダをすべてダンプするロジック...
                    for (String key : headers.keySet())
                    {
                        final List<String> valueList = headers.get(key);
                        Log.v(TAG, " " + key + " : " + getValue(valueList));
                    }
                    */

                    // コンテンツ長さが取れない場合は、HTTP応答ヘッダから取得する
                    List<String> valueList = headers.get("X-FILE_SIZE");
                    try
                    {
                        if (valueList != null)
                        {
                            contentLength = Integer.parseInt(getValue(valueList));
                        }
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int readBytes = 0;
            int readSize = inputStream.read(buffer, 0, BUFFER_SIZE);
            while (readSize != -1)
            {
                callback.onReceive(readBytes, contentLength, readSize, buffer);
                readBytes += readSize;
                readSize = inputStream.read(buffer, 0, BUFFER_SIZE);
            }
            Log.v(TAG, "RECEIVED " + readBytes + " BYTES. (contentLength : " + contentLength + ")");
            inputStream.close();
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


    public static String getValue(List<String> valueList)
    {
        // 応答ヘッダの値切り出し用...
        boolean isFirst = true;
        final StringBuilder values = new StringBuilder();
        for (String value : valueList)
        {
            values.append(value);
            if (isFirst)
            {
                isFirst = false;
            }
            else
            {
                values.append(" ");
            }
        }
        return (values.toString());
    }

    public static Bitmap httpGetBitmap(String url, Map<String, String> setProperty, int timeoutMs)
    {
        return (httpCommandBitmap(url, "GET", null, setProperty, null, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static Bitmap httpPostBitmap(String url, String postData, int timeoutMs)
    {
        return (httpCommandBitmap(url, "POST", postData, null, null, timeoutMs));
    }

    /**
     *
     *
     *
     */
    private static Bitmap httpCommandBitmap(@NonNull String url, @NonNull String requestMethod, @Nullable String postData, @Nullable Map<String, String> setProperty, @Nullable String contentType, int timeoutMs)
    {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        Bitmap bmp = null;

        int timeout = timeoutMs;
        if (timeoutMs < 0)
        {
            timeout = DEFAULT_TIMEOUT;
        }

        //  HTTP メソッドで要求を送出
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod(requestMethod);
            if (setProperty != null)
            {
                for (String key : setProperty.keySet())
                {
                    String value = setProperty.get(key);
                    httpConn.setRequestProperty(key, value);
                }
            }
            if (contentType != null)
            {
                httpConn.setRequestProperty("Content-Type", contentType);
            }
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);

            if (postData == null)
            {
                httpConn.connect();
            }
            else
            {
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
            }

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
                Log.w(TAG, "http: (" + requestMethod + ") Response Code Error: " + responseCode + ": " + url);
                return (null);
            }
            inputStream.close();
        }
        catch (Exception e)
        {
            Log.w(TAG, "http: (" + requestMethod + ") " + url + "  " + e.getMessage());
            e.printStackTrace();
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
            return (null);
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
        return (bmp);
    }

    /**
     *
     *
     *
     */
    public static String httpPost(String url, String postData, int timeoutMs)
    {
        return (httpCommand(url, "POST", postData, null, null, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static String httpGetWithHeader(String url, Map<String, String> headerMap, String contentType, int timeoutMs)
    {
        return (httpCommand(url, "GET", null, headerMap, contentType, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static String httpPostWithHeader(String url, String postData, Map<String, String> headerMap, String contentType, int timeoutMs)
    {
        return (httpCommand(url, "POST", postData, headerMap, contentType, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static String httpPutWithHeader(String url, String putData, Map<String, String> headerMap, String contentType, int timeoutMs)
    {
        return (httpCommand(url, "PUT", putData, headerMap, contentType, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static String httpPut(String url, String postData, int timeoutMs)
    {
        return (httpCommand(url, "PUT", postData, null, null, timeoutMs));
    }

    /**
     *
     *
     *
     */
    public static String httpOptions(String url, String optionsData, int timeoutMs)
    {
        return (httpCommand(url, "OPTIONS", optionsData, null, null, timeoutMs));
    }

    /**
     *
     *
     *
     */
    private static String httpCommand(String url, String requestMethod, String postData, Map<String, String> setProperty, String contentType, int timeoutMs)
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

        //  HTTP メソッドで要求を送出
        try
        {
            final URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod(requestMethod);
            if (setProperty != null)
            {
                for (String key : setProperty.keySet())
                {
                    String value = setProperty.get(key);
                    httpConn.setRequestProperty(key, value);
                }
            }
            if (contentType != null)
            {
                httpConn.setRequestProperty("Content-Type", contentType);
            }
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);

            if (postData == null)
            {
                httpConn.connect();
            }
            else
            {
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
            }
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null)
            {
                Log.w(TAG, "http " + requestMethod + " : Response Code Error: " + responseCode + ": " + url);
/*
                inputStream = httpConn.getInputStream();
                if (inputStream != null)
                {
                    // DUMP RESPONSE DETAIL
                    Log.w(TAG, " RESP : " + readFromInputStream(inputStream));
                }
*/
                return ("");
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "http " + requestMethod + " : IOException: " + e.getMessage());
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
        return (readFromInputStream(inputStream));
/*
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
*/
    }

    private static String readFromInputStream(InputStream inputStream)
    {
        BufferedReader reader = null;
        String replyString = "";
        if (inputStream == null)
        {
            return ("");
        }
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
        void onReceive(int readBytes, int length, int size, byte[] data);
    }
}
