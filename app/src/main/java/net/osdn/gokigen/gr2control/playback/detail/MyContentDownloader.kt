package net.osdn.gokigen.gr2control.playback.detail

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import net.osdn.gokigen.gr2control.R
import net.osdn.gokigen.gr2control.camera.ICameraFileInfo
import net.osdn.gokigen.gr2control.camera.playback.IDownloadContentCallback
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl
import net.osdn.gokigen.gr2control.camera.playback.ProgressEvent
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * コンテントのダウンロード
 *
 */
class MyContentDownloader(private val activity: Activity, private val playbackControl: IPlaybackControl) : IDownloadContentCallback
{
    private var downloadDialog: ProgressDialog? = null
    private var outputStream: FileOutputStream? = null
    private var targetFileName = ""
    private var filepath = ""
    private var mimeType = "image/jpeg"
    private var isDownloading = false

    /**
     * ダウンロードの開始
     *
     */
    fun startDownload(fileInfo: ICameraFileInfo?, appendTitle: String, replaceJpegSuffix: String?, isSmallSizeArg: Boolean)
    {
        var isSmallSize = isSmallSizeArg
        if (fileInfo == null)
        {
            Log.v(TAG, "startDownload() ICameraFileInfo is NULL...")
            return
        }
        Log.v(TAG, "startDownload() " + fileInfo.filename)

        // Download the image.
        try
        {
            isDownloading = true
            val calendar = Calendar.getInstance()
            val extendName = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.time)
            targetFileName = fileInfo.originalFilename.uppercase(Locale.getDefault())
            if (replaceJpegSuffix != null)
            {
                targetFileName = targetFileName.replace(JPEG_SUFFIX, replaceJpegSuffix)
            }
            if (targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_1)) {
                mimeType = "image/x-adobe-dng"
                isSmallSize = false
            } else if (targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_2)) {
                mimeType = "image/x-olympus-orf"
                isSmallSize = false
            } else if (targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_3)) {
                mimeType = "image/x-pentax-pef"
                isSmallSize = false
            } else if (targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_4)) {
                mimeType = "image/x-fuji-raf"
                isSmallSize = false
            } else if (targetFileName.uppercase(Locale.getDefault()).contains(MOVIE_SUFFIX)) {
                mimeType = "video/mp4"
                isSmallSize = false
            } else {
                mimeType = "image/jpeg"
            }

            ////// ダイアログの表示
            activity.runOnUiThread {
                downloadDialog = ProgressDialog(activity)
                downloadDialog?.setTitle(activity.getString(R.string.dialog_download_file_title) + appendTitle)
                downloadDialog?.setMessage(activity.getString(R.string.dialog_download_message) + " " + targetFileName)
                downloadDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                downloadDialog?.setCancelable(false)
                downloadDialog?.show()
            }
            val fileName = fileInfo.directoryPath + "/" + fileInfo.filename

            val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + "/" + activity.getString(R.string.app_name2) + "/"
            val suffix = targetFileName.substring(targetFileName.indexOf("."))
            val picName = targetFileName.substring(0, targetFileName.indexOf("."))
            val outputFileName = picName + "_" + extendName + suffix
            filepath = File(directoryPath.lowercase(Locale.getDefault()), outputFileName.lowercase(Locale.getDefault())).path

            try
            {
                val directory = File(directoryPath)
                if (!directory.exists())
                {
                    if (!directory.mkdirs())
                    {
                        Log.v(TAG, "MKDIR FAIL. : $directoryPath")
                    }
                }
                outputStream = FileOutputStream(filepath)
            }
            catch (e: Exception)
            {
                val message = e.message
                activity.runOnUiThread {
                    if (downloadDialog != null)
                    {
                        downloadDialog?.dismiss()
                    }
                    downloadDialog = null
                    isDownloading = false
                    presentMessage(
                        activity.getString(R.string.download_control_save_failed),
                        message
                    )
                }
            }
            Log.v(TAG, "downloadContent : $fileName (small: $isSmallSize)")
            playbackControl.downloadContent(null, fileName, isSmallSize, this)
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            activity.runOnUiThread {
                try
                {
                    if (downloadDialog != null)
                    {
                        downloadDialog?.dismiss()
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                downloadDialog = null
                isDownloading = false
            }
        }
    }

    override fun onProgress(bytes: ByteArray, length: Int, progressEvent: ProgressEvent)
    {
        if (downloadDialog != null)
        {
            val percent = (progressEvent.progress * 100.0f).toInt()
            downloadDialog?.progress = percent
            //downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
            //Log.v(TAG, "DOWNLOAD (" + percent + "%) " + bytes.length);
        }
        try
        {
            if (outputStream != null)
            {
                outputStream?.write(bytes, 0, length)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onCompleted()
    {
        try
        {
            if (outputStream != null)
            {
                outputStream?.flush()
                outputStream?.close()
                outputStream = null
            }
            //if ((!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_1))&&(!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_2))&&(!targetFileName.toUpperCase().endsWith(RAW_SUFFIX_3)))
            run {
                // ギャラリーに受信したファイルを登録する
                val now = System.currentTimeMillis()
                val values = ContentValues()
                val resolver = activity.contentResolver
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                values.put(MediaStore.Images.Media.DATA, filepath)
                values.put(MediaStore.Images.Media.DATE_ADDED, now)
                values.put(MediaStore.Images.Media.DATE_TAKEN, now)
                values.put(MediaStore.Images.Media.DATE_MODIFIED, now)
                val mediaValue =
                    if (mimeType.contains("video")) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val content = resolver.insert(mediaValue, values)
                try
                {
                    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    if (preferences.getBoolean(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false))
                    {
                        activity.runOnUiThread { shareContent(content, mimeType) }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            activity.runOnUiThread {
                if (downloadDialog != null)
                {
                    downloadDialog?.dismiss()
                }
                downloadDialog = null
                isDownloading = false
                val view = activity.findViewById<View>(R.id.fragment1)
                Snackbar.make(view, activity.getString(R.string.download_control_save_success) + " " + targetFileName, Snackbar.LENGTH_SHORT).show()
                //Toast.makeText(activity, activity.getString(R.string.download_control_save_success) + " " + targetFileName, Toast.LENGTH_SHORT).show();
                System.gc()
            }
        }
        catch (e: Exception)
        {
            val message = e.message
            activity.runOnUiThread {
                if (downloadDialog != null)
                {
                    downloadDialog?.dismiss()
                }
                downloadDialog = null
                isDownloading = false
                presentMessage(activity.getString(R.string.download_control_save_failed), message)
            }
        }
        System.gc()
    }

    override fun onErrorOccurred(e: Exception)
    {
        isDownloading = false
        val message = e.message
        try
        {
            if (outputStream != null)
            {
                outputStream?.flush()
                outputStream?.close()
                outputStream = null
            }
        }
        catch (ex: Exception)
        {
            e.printStackTrace()
            ex.printStackTrace()
        }
        activity.runOnUiThread {
            if (downloadDialog != null)
            {
                downloadDialog!!.dismiss()
            }
            downloadDialog = null
            isDownloading = false
            presentMessage(activity.getString(R.string.download_control_download_failed), message)
            System.gc()
        }
        System.gc()
    }

    fun isDownloading(): Boolean
    {
        return (isDownloading)
    }

    /**
     * 共有の呼び出し
     *
     * @param fileUri  ファイルUri
     */
    private fun shareContent(fileUri: Uri?, contentType: String)
    {
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        try
        {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setType(contentType) // "video/mp4"  or "image/jpeg"  or "image/x-adobe-dng"
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            activity.startActivityForResult(intent, 0)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun presentMessage(title: String, message: String?)
    {
        activity.runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title).setMessage(message)
            builder.show()
        }
    }

    companion object
    {
        private val TAG = MyContentDownloader::class.java.simpleName
        private const val RAW_SUFFIX_1 = ".DNG"
        private const val RAW_SUFFIX_2 = ".ORF"
        private const val RAW_SUFFIX_3 = ".PEF"
        private const val RAW_SUFFIX_4 = ".RAF"
        private const val MOVIE_SUFFIX = ".MOV"
        private const val JPEG_SUFFIX = ".JPG"
    }
}
