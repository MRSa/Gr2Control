package net.osdn.gokigen.gr2control.playback.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

/**
 *   コンテントのダウンロード
 *
 */
public class MyContentDownloader implements IDownloadContentCallback
{
    private final String TAG = toString();
    private final Activity activity;
    private final IPlaybackControl playbackControl;
    private static final String RAW_SUFFIX_1 = ".DNG";
    private static final String RAW_SUFFIX_2 = ".ORF";
    private static final String RAW_SUFFIX_3 = ".PEF";
    private static final String RAW_SUFFIX_4 = ".RAF";
    private static final String MOVIE_SUFFIX = ".MOV";
    private static final String JPEG_SUFFIX = ".JPG";
    private ProgressDialog downloadDialog = null;
    private FileOutputStream outputStream = null;
    private String targetFileName = "";
    private String filepath = "";
    private String mimeType = "image/jpeg";
    private boolean isDownloading = false;

    /**
     *   コンストラクタ
     *
     */
    public MyContentDownloader(@NonNull Activity activity, @NonNull final IPlaybackControl playbackControl)
    {
        this.activity = activity;
        this.playbackControl = playbackControl;
    }

    /**
     *   ダウンロードの開始
     *
     */
    public void startDownload(final ICameraFileInfo fileInfo, final String appendTitle, String replaceJpegSuffix, boolean isSmallSize)
    {
        if (fileInfo == null)
        {
            Log.v(TAG, "startDownload() ICameraFileInfo is NULL...");
            return;
        }
        Log.v(TAG, "startDownload() " + fileInfo.getFilename());

        // Download the image.
        try
        {
            isDownloading = true;
            Calendar calendar = Calendar.getInstance();
            String extendName = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.getTime());
            targetFileName = fileInfo.getOriginalFilename().toUpperCase();
            if (replaceJpegSuffix != null)
            {
                targetFileName = targetFileName.replace(JPEG_SUFFIX, replaceJpegSuffix);
            }
            if (targetFileName.toUpperCase().contains(RAW_SUFFIX_1))
            {
                mimeType = "image/x-adobe-dng";
                isSmallSize = false;
            }
            else if (targetFileName.toUpperCase().contains(RAW_SUFFIX_2))
            {
                mimeType = "image/x-olympus-orf";
                isSmallSize = false;
            }
            else if (targetFileName.toUpperCase().contains(RAW_SUFFIX_3))
            {
                mimeType = "image/x-pentax-pef";
                isSmallSize = false;
            }
            else if (targetFileName.toUpperCase().contains(RAW_SUFFIX_4))
            {
                mimeType = "image/x-fuji-raf";
                isSmallSize = false;
            }
            else if (targetFileName.toUpperCase().contains(MOVIE_SUFFIX))
            {
                mimeType =  "video/mp4";
                isSmallSize = false;
            }
            else
            {
                mimeType = "image/jpeg";
            }

            ////// ダイアログの表示
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadDialog = new ProgressDialog(activity);
                    downloadDialog.setTitle(activity.getString(R.string.dialog_download_file_title) + appendTitle);
                    downloadDialog.setMessage(activity.getString(R.string.dialog_download_message) + " " + targetFileName);
                    downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    downloadDialog.setCancelable(false);
                    downloadDialog.show();
                }
            });
            String fileName = fileInfo.getDirectoryPath() + "/" + fileInfo.getFilename();

            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + activity.getString(R.string.app_name2) + "/";
            String suffix = targetFileName.substring(targetFileName.indexOf("."));
            String picName = targetFileName.substring(0, targetFileName.indexOf("."));
            String outputFileName = picName + "_" +  extendName + suffix;
            filepath = new File(directoryPath.toLowerCase(), outputFileName.toLowerCase()).getPath();
            try
            {
                final File directory = new File(directoryPath);
                if (!directory.exists())
                {
                    if (!directory.mkdirs())
                    {
                        Log.v(TAG, "MKDIR FAIL. : " + directoryPath);
                    }
                }
                outputStream = new FileOutputStream(filepath);
            }
            catch (Exception e)
            {
                final String message = e.getMessage();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog != null) {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        isDownloading = false;
                        presentMessage(activity.getString(R.string.download_control_save_failed), message);
                    }
                });
            }
            Log.v(TAG, "downloadContent : " + fileName + " (small: " + isSmallSize + ")");
            playbackControl.downloadContent(null, fileName, isSmallSize, this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        if (downloadDialog != null) {
                            downloadDialog.dismiss();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    downloadDialog = null;
                    isDownloading = false;
                }
            });
        }
    }

    @Override
    public void onProgress(byte[] bytes, int length, ProgressEvent progressEvent)
    {
        if (downloadDialog != null)
        {
            int percent = (int)(progressEvent.getProgress() * 100.0f);
            downloadDialog.setProgress(percent);
            //downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
            //Log.v(TAG, "DOWNLOAD (" + percent + "%) " + bytes.length);
        }
        try
        {
            if (outputStream != null)
            {
                outputStream.write(bytes, 0, length);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted()
    {
        try
        {
            if (outputStream != null)
            {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            }
            //if ((!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_1))&&(!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_2))&&(!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_3)))
            {
                // ギャラリーに受信したファイルを登録する
                long now = System.currentTimeMillis();
                ContentValues values = new ContentValues();
                ContentResolver resolver = activity.getContentResolver();
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
                values.put(MediaStore.Images.Media.DATA, filepath);
                values.put(MediaStore.Images.Media.DATE_ADDED, now);
                values.put(MediaStore.Images.Media.DATE_TAKEN, now);
                values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
                Uri mediaValue = mimeType.contains("video") ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                final Uri content = resolver.insert(mediaValue, values);
                try
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    if (preferences.getBoolean(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false))
                    {
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                shareContent(content, mimeType);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if (downloadDialog != null)
                    {
                        downloadDialog.dismiss();
                    }
                    downloadDialog = null;
                    isDownloading = false;
                    View view = activity.findViewById(R.id.fragment1);
                    Snackbar.make(view, activity.getString(R.string.download_control_save_success) + " " + targetFileName, Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(activity, activity.getString(R.string.download_control_save_success) + " " + targetFileName, Toast.LENGTH_SHORT).show();
                    System.gc();
                }
            });
        }
        catch (Exception e)
        {
            final String message = e.getMessage();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (downloadDialog != null)
                    {
                        downloadDialog.dismiss();
                    }
                    downloadDialog = null;
                    isDownloading = false;
                    presentMessage(activity.getString(R.string.download_control_save_failed), message);
                }
            });
        }
        System.gc();
    }

    @Override
    public void onErrorOccurred(Exception e)
    {
        isDownloading = false;
        final String message = e.getMessage();
        try
        {
            if (outputStream != null)
            {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            }
        }
        catch (Exception ex)
        {
            e.printStackTrace();
            ex.printStackTrace();
        }
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (downloadDialog != null)
                {
                    downloadDialog.dismiss();
                }
                downloadDialog = null;
                isDownloading = false;
                presentMessage(activity.getString(R.string.download_control_download_failed), message);
                System.gc();
            }
        });
        System.gc();
    }

    public boolean isDownloading()
    {
        return (isDownloading);
    }

    /**
     *   共有の呼び出し
     *
     * @param fileUri  ファイルUri
     */
    private void shareContent(final Uri fileUri, final String contentType)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType(contentType);   // "video/mp4"  or "image/jpeg"  or "image/x-adobe-dng"
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            activity.startActivityForResult(intent, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void presentMessage(final String title, final String message)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title).setMessage(message);
                builder.show();
            }
        });
    }
}
