package net.osdn.gokigen.gr2control.playback.detail

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
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
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * コンテントのダウンロード
 *
 */
class MyContentDownloader(private val activity: Activity, private val playbackControl: IPlaybackControl) : IDownloadContentCallback
{
    private val dumpLog = false
    private var downloadDialog: ProgressDialog? = null
    private var outputStream: OutputStream? = null
    private var targetFileName = ""
    private var filepath = ""
    private var mimeType = "image/jpeg"
    private var isDownloading = false
    private var imageUri : Uri? = null

    private fun getExternalOutputDirectory(): File
    {
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + "/" + activity.getString(R.string.app_name2) + "/"
        val target = File(directoryPath)
        try
        {
            target.mkdirs()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, "  ----- RECORD Directory PATH : $directoryPath -----")
        return (target)
    }

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
            var isVideo = false
            isDownloading = true
            //val calendar = Calendar.getInstance()
            //val extendName = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.time)
            targetFileName = fileInfo.originalFilename.uppercase(Locale.getDefault())
            if (replaceJpegSuffix != null)
            {
                targetFileName = targetFileName.replace(JPEG_SUFFIX, replaceJpegSuffix)
            }
            when {
                targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_1) -> {
                    mimeType = "image/x-adobe-dng"
                    isSmallSize = false
                }
                targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_2) -> {
                    mimeType = "image/x-olympus-orf"
                    isSmallSize = false
                }
                targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_3) -> {
                    mimeType = "image/x-pentax-pef"
                    isSmallSize = false
                }
                targetFileName.uppercase(Locale.getDefault()).contains(RAW_SUFFIX_4) -> {
                    mimeType = "image/x-fuji-raf"
                    isSmallSize = false
                }
                targetFileName.uppercase(Locale.getDefault()).contains(MOVIE_SUFFIX) -> {
                    mimeType = "video/mp4"
                    isSmallSize = false
                    isVideo = true
                }
                else -> {
                    mimeType = "image/jpeg"
                }
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

            val resolver = activity.contentResolver
            val directoryPath = Environment.DIRECTORY_DCIM + File.separator + activity.getString(R.string.app_name2) + File.separator
            val calendar = Calendar.getInstance()
            val extendName = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.time)
            //val periodPosition = targetFileName.indexOf(".")
            //val extension = targetFileName.substring(periodPosition)
            //val baseFileName = targetFileName.substring(0, periodPosition)
            //val fileName = fileInfo.directoryPath + "/" + fileInfo.filename

            val suffix = targetFileName.substring(targetFileName.indexOf("."))
            val picName = targetFileName.substring(0, targetFileName.indexOf("."))
            val outputFileName = picName + "_" + extendName + suffix

            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, outputFileName)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, outputFileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

            val extStorageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, directoryPath)
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                if (isVideo)
                {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                }
                else
                {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                }
            } else {
                values.put(MediaStore.Images.Media.DATA, getExternalOutputDirectory().absolutePath + File.separator + outputFileName)
                if (isVideo)
                {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                else
                {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }

            imageUri = resolver.insert(extStorageUri, values)
            if (imageUri != null)
            {
                ////////////////////////////////////////////////////////////////
                if (dumpLog)
                {
                    val cursor = resolver.query(imageUri!!, null, null, null, null)
                    DatabaseUtils.dumpCursor(cursor)
                    cursor?.close()
                }
                ////////////////////////////////////////////////////////////////

                try
                {
                    outputStream = resolver.openOutputStream(imageUri!!)
                    val path = fileInfo.directoryPath + "/" + fileInfo.filename
                    Log.v(TAG, "downloadContent : $path (small: $isSmallSize)")
                    playbackControl.downloadContent(null, path, isSmallSize, this)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    val message = e.message
                    activity.runOnUiThread {
                        downloadDialog?.dismiss()
                        isDownloading = false
                        downloadDialog = null
                        presentMessage(activity.getString(R.string.download_control_save_failed), message)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            values.put(MediaStore.Images.Media.IS_PENDING, false)
                            if (imageUri != null)
                            {
                                resolver.update(imageUri!!, values, null, null)
                            }
                        }
                    }
                }
            }
            else
            {
                activity.runOnUiThread {
                    downloadDialog?.dismiss()
                    isDownloading = false
                    downloadDialog = null
                    presentMessage(activity.getString(R.string.download_control_save_failed), activity.getString(R.string.resolver_insert_failure))
                }
            }
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
        try
        {
            val percent = (progressEvent.progress * 100.0f).toInt()
            downloadDialog?.progress = percent
            if ((outputStream != null)&&(bytes != null)&&(length > 0))
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
            outputStream?.flush()
            outputStream?.close()
            if (imageUri != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    val values = ContentValues()
                    val resolver = activity.contentResolver
                    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    values.put(MediaStore.Images.Media.DATA, filepath)
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(imageUri!!, values, null, null)
                }
            }
            try
            {
                if (imageUri != null)
                {
                    try
                    {
                        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                        if (preferences.getBoolean(IPreferencePropertyAccessor.SHARE_AFTER_SAVE, false))
                        {
                            activity.runOnUiThread { shareContent(imageUri, mimeType) }
                        }
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
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
