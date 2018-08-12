package net.osdn.gokigen.gr2control.camera.olympus.operation.takepicture;

import android.util.Log;

import net.osdn.gokigen.gr2control.liveview.IIndicatorControl;

import java.util.HashMap;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;


/**
 *   ビデオ撮影の開始・終了制御クラス。
 *
 */
public class MovieRecordingControl implements OLYCamera.CompletedCallback
{
    private final String TAG = toString();
   // private final Context context;
    private final OLYCamera camera;
    private final IIndicatorControl indicator;
    private IIndicatorControl.shootingStatus isRecordingStart = IIndicatorControl.shootingStatus.Unknown;

    /**
     *   コンストラクタ
     *
     */
    //public MovieRecordingControl(Context context, OLYCamera camera, IIndicatorControl indicator)
    public MovieRecordingControl(OLYCamera camera, IIndicatorControl indicator)
    {
        //this.context = context;
        this.camera = camera;
        this.indicator = indicator;
    }

    /**
     *   動画撮影の開始と終了
     *
     */
    public void movieControl()
    {
        try
        {
            Log.v(TAG, "MovieRecordingControl::movieControl()");
            if (camera.isTakingPicture())
            {
                // スチル撮影中の場合は、何もしない（モード異常なので）
                Log.v(TAG, "NOW TAKING PICTURE(STILL) : COMMAND IGNORED");
                return;
            }

            if (!camera.isRecordingVideo())
            {
                // ムービー撮影の開始指示
                camera.startRecordingVideo(new HashMap<String, Object>(), this);
                isRecordingStart = IIndicatorControl.shootingStatus.Starting;
            }
            else
            {
                // ムービー撮影の終了指示
                camera.stopRecordingVideo(this);
                isRecordingStart = IIndicatorControl.shootingStatus.Stopping;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   処理完了
     *
     */
    @Override
    public void onCompleted()
    {
        try
        {
            Log.v(TAG, "MovieRecordingControl::onCompleted()");
            // 撮影終了をバイブレータで知らせる
            //statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE);
            indicator.onMovieStatusUpdate(isRecordingStart);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   エラー発生
     *
     * @param e 例外情報
     */
    @Override
    public void onErrorOccurred(OLYCameraKitException e)
    {
        // 撮影失敗をバイブレータで知らせる
        //statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT);
        {
            //// 撮影失敗の表示をToastで行う
            //Toast.makeText(context, R.string.video_failure, Toast.LENGTH_SHORT).show();
            Log.v(TAG, "MovieControl::onErrorOccurred()");
            isRecordingStart = IIndicatorControl.shootingStatus.Unknown;
            indicator.onMovieStatusUpdate(isRecordingStart);
        }
        e.printStackTrace();
    }
}
