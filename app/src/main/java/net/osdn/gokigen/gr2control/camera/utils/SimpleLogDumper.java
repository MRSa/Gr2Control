package net.osdn.gokigen.gr2control.camera.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SimpleLogDumper
{
    private static final String TAG = SimpleLogDumper.class.getSimpleName();

    /**
     *   デバッグ用：ログにバイト列を出力する
     *
     */
    public static void dump_bytes(String header, byte[] data)
    {
        if (data == null)
        {
            Log.v(TAG, "DATA IS NULL");
            return;
        }
        if (data.length > 8192)
        {
            Log.v(TAG, " --- DUMP DATA IS TOO LONG... " + data.length + " bytes.");
            return;
        }

        int index = 0;
        StringBuffer message;
        message = new StringBuffer();
        for (byte item : data)
        {
            index++;
            message.append(String.format("%02x ", item));
            if (index >= 16)
            {
                Log.v(TAG, header + " " + message);
                index = 0;
                message = new StringBuffer();
            }
        }
        if (index != 0)
        {
            Log.v(TAG, header + " " + message);
        }
        System.gc();
    }

    public static void binaryOutputToFile(@NonNull Activity activity, String fileNamePrefix, byte[] rx_body)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            String extendName = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.getTime());
            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + activity.getString(R.string.app_name2) + "/";
            String outputFileName = fileNamePrefix + "_" + extendName + ".bin";
            String filepath = new File(directoryPath.toLowerCase(), outputFileName.toLowerCase()).getPath();
            FileOutputStream outputStream = new FileOutputStream(filepath);
            outputStream.write(rx_body, 0, rx_body.length);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
