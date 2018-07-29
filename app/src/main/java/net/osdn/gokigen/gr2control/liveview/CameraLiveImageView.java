package net.osdn.gokigen.gr2control.liveview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.media.ExifInterface;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.liveview.bitmapconvert.IPreviewImageConverter;
import net.osdn.gokigen.gr2control.liveview.bitmapconvert.ImageConvertFactory;
import net.osdn.gokigen.gr2control.liveview.gridframe.GridFrameFactory;
import net.osdn.gokigen.gr2control.liveview.gridframe.IGridFrameDrawer;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.IImageDataReceiver;
import net.osdn.gokigen.gr2control.liveview.message.IMessageDrawer;
import net.osdn.gokigen.gr2control.liveview.message.IMessageHolder;
import net.osdn.gokigen.gr2control.liveview.message.ShowMessageHolder;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 *   CameraLiveImageView :
 *    (OLYMPUS の ImageCaptureSample そのまま)
 *
 */
public class CameraLiveImageView extends View implements IImageDataReceiver, IAutoFocusFrameDisplay, ILiveImageStatusNotify, IIndicatorControl
{
    private final String TAG = this.toString();

    private static final String EXIF_ORIENTATION = "Orientation";

    private boolean focusAssistFeature = false;
    private boolean showGridFeature = false;
    private ImageView.ScaleType imageScaleType;
    private Bitmap imageBitmap;
    private int imageRotationDegrees;
    private boolean showingFocusFrame = false;
    private IAutoFocusFrameDisplay.FocusFrameStatus focusFrameStatus;
    private RectF focusFrameRect;
    private Timer focusFrameHideTimer;

    private IGridFrameDrawer gridFrameDrawer = null;
    private IPreviewImageConverter bitmapConverter = null;
    private IMessageHolder messageHolder;
    private IStoreImage storeImage = null;

    public CameraLiveImageView(Context context)
    {
        super(context);
        initComponent(context);
    }

    public CameraLiveImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initComponent(context);
    }

    public CameraLiveImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initComponent(context);
    }

    private void initComponent(Context context)
    {
        storeImage = new StoreImage(context);
        messageHolder = new ShowMessageHolder();
        imageScaleType = ImageView.ScaleType.FIT_CENTER;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Gridの表示/非表示
        int framingGridStatus = 0;
        gridFrameDrawer = GridFrameFactory.getGridFrameDrawer(framingGridStatus);
        showGridFeature = preferences.getBoolean(IPreferencePropertyAccessor.SHOW_GRID_STATUS, false);

        int converterType = 0;
        bitmapConverter = ImageConvertFactory.getImageConverter(converterType);

        // ダミーのビットマップデータ読み込み...画面表示のテスト用ロジック
        try
        {
            imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gr2_default);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            imageBitmap = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        imageBitmap = null;
        if (focusFrameHideTimer != null)
        {
            focusFrameHideTimer.cancel();
            focusFrameHideTimer = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCanvas(canvas);

        // メッセージの表示 (Overwrite)
        drawInformationMessages(canvas);

        // レベルゲージの表示
        if (messageHolder.isLevel())
        {
            drawLevelGauge(canvas);
        }
    }

    @Override
    public float getContentSizeWidth() {
        return getIntrinsicContentSizeWidth();
    }

    @Override
    public float getContentSizeHeight() {
        return getIntrinsicContentSizeHeight();
    }


    private float getIntrinsicContentSizeWidth()
    {
        if (imageBitmap == null)
        {
            return (1.0f);
        }
        return (imageBitmap.getWidth());
    }

    private float getIntrinsicContentSizeHeight()
    {
        if (imageBitmap == null)
        {
            return (1.0f);
        }
        return (imageBitmap.getHeight());
    }

    /**
     * Sets a image to view.
     * (OlympusCameraLiveViewListenerImpl.IImageDataReceiver の実装)
     *
     * @param data     A image of live-view.
     * @param metadata A metadata of the image.
     */
    public void setImageData(byte[] data, Map<String, Object> metadata)
    {
        Bitmap bitmap;
        int rotationDegrees;

        if (data != null)
        {
            // Create a bitmap.
            try
            {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            catch (OutOfMemoryError e)
            {
                e.printStackTrace();
                return;
            }

            // Acquire a rotation degree of image.
            int orientation = ExifInterface.ORIENTATION_UNDEFINED;
            if ((metadata != null)&&(metadata.containsKey(EXIF_ORIENTATION)))
            {
                orientation = Integer.parseInt((String) metadata.get(EXIF_ORIENTATION));
            }
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_NORMAL:
                    rotationDegrees = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
                default:
                    rotationDegrees = 0;
                    break;
            }
            imageBitmap = bitmap;
            imageRotationDegrees = rotationDegrees;
        }
        refreshCanvas();
    }

    /**
     * Returns a point which is detected by a motion event.
     *
     * @param event A motion event.
     * @return A point in the view finder. if a point is equal to null, the point is out of the view finder.
     */
    public PointF getPointWithEvent(MotionEvent event)
    {
        if (event == null || imageBitmap == null)
        {
            return null;
        }

        PointF pointOnView = new PointF(event.getX() - getX(), event.getY() - getY()); // Viewの表示位置に補正
        PointF pointOnImage = convertPointFromViewArea(pointOnView);
        float imageWidth;
        float imageHeight;
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
            imageWidth = imageBitmap.getWidth();
            imageHeight = imageBitmap.getHeight();
        } else {
            imageWidth = imageBitmap.getHeight();
            imageHeight = imageBitmap.getWidth();
         }
        return (convertPointOnLiveImageIntoViewfinder(pointOnImage, imageWidth, imageHeight, imageRotationDegrees));
    }

    /**
     *　　ライブビュー座標系の点座標をビューファインダー座標系の点座標に変換
     *
     *
     */
    private PointF convertPointOnLiveImageIntoViewfinder(PointF point, float width, float height, int rotatedDegrees)
    {
        float viewFinderPointX = 0.5f;
        float viewFinderPointY = 0.5f;
        try
        {
            if (rotatedDegrees == 0 || rotatedDegrees == 180) {
                viewFinderPointX = point.x / width;
                viewFinderPointY = point.y / height;
            } else {
                viewFinderPointX = point.y / width;
                viewFinderPointY = point.x / height;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (new PointF(viewFinderPointX, viewFinderPointY));
    }

    /**
     *　　ビューファインダー座標系の矩形座標をライブビュー座標系のの矩形座標に変換
     *
     *
     */
    private RectF convertRectOnViewfinderIntoLiveImage(RectF rect, float width, float height, int rotatedDegrees)
    {
        float top = 0.0f;
        float bottom = 1.0f;
        float left = 0.0f;
        float right = 1.0f;
        try
        {
            if (rotatedDegrees == 0 || rotatedDegrees == 180) {
                top = rect.top * height;
                bottom = rect.bottom * height;
                left = rect.left * width;
                right = rect.right * width;
            } else {
                left = rect.top * height;
                right = rect.bottom * height;
                top = rect.left * width;
                bottom = rect.right * width;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (new RectF(left, top, right, bottom));
    }

    /**
     * Returns whether a image area contains a specified point.
     *
     * @param point The point to examine.
     * @return true if the image is not null or empty and the point is located within the rectangle; otherwise, false.
     */
    public boolean isContainsPoint(PointF point)
    {
        return ((point != null) && (new RectF(0, 0, 1, 1)).contains(point.x, point.y));
    }

    /**
     * Hides the forcus frame.
     */
    public void hideFocusFrame()
    {
        if (focusFrameHideTimer != null)
        {
            focusFrameHideTimer.cancel();
            focusFrameHideTimer = null;
        }
        showingFocusFrame = false;

        refreshCanvas();
    }

    /**
     * Shows the focus frame.
     *
     * @param rect     A rectangle of the focus frame on view area.
     * @param status   A status of the focus frame.
     * @param duration A duration of the focus frame showing.
     */
    @Override
    public void showFocusFrame(RectF rect, FocusFrameStatus status, double duration)
    {
        if (focusFrameHideTimer != null)
        {
            focusFrameHideTimer.cancel();
            focusFrameHideTimer = null;
        }

        showingFocusFrame = true;
        focusFrameStatus = status;
        focusFrameRect = rect;

        refreshCanvas();

        if (duration > 0)
        {
            focusFrameHideTimer = new Timer();
            focusFrameHideTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    hideFocusFrame();
                }
            }, (long) (duration * 1000));
        }
    }

    private void refreshCanvas()
    {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            invalidate();
        }
        else
        {
            postInvalidate();
        }
    }

    /**
     *   ビットマップの表示
     *
     * @param canvas キャンバス
     */
    private void drawCanvas(Canvas canvas)
    {
        // Clears the canvas.
        canvas.drawARGB(255, 0, 0, 0);

        // ビットマップの取得
        Bitmap bitmapToShow;
        if ((focusAssistFeature)&&(bitmapConverter != null))
        {
            // フォーカスアシスト実行時の処理...
            bitmapToShow = bitmapConverter.getModifiedBitmap(imageBitmap);
        }
        else
        {
            bitmapToShow = imageBitmap;
        }
        if (bitmapToShow == null)
        {
            // 表示するビットマップがないときは、すぐに折り返す
            return;
        }

        // Rotates the image.
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        canvas.rotate(imageRotationDegrees, centerX, centerY);

        RectF viewRect = null;

        //  Calculate the viewport of bitmap.
        if (imageScaleType == ImageView.ScaleType.FIT_CENTER)
        {
            viewRect = decideViewRect(canvas, bitmapToShow, imageRotationDegrees);

            // Draws the bitmap.
            Rect imageRect = new Rect(0, 0, bitmapToShow.getWidth(), bitmapToShow.getHeight());
            canvas.drawBitmap(bitmapToShow, imageRect, viewRect, null);
        }
        else
        {
            // Sorry, other scale types are not supported.
            Log.v(TAG, "Sorry, other scale types are not supported. " + imageScaleType);
        }

        // Cancels rotation of the canvas.
        canvas.rotate(-imageRotationDegrees, centerX, centerY);

        // フォーカスフレームを表示する
        if ((focusFrameRect != null) && (showingFocusFrame))
        {
            if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
                drawFocusFrame(canvas, bitmapToShow.getWidth(), bitmapToShow.getHeight());
            } else {
                drawFocusFrame(canvas, bitmapToShow.getHeight(), bitmapToShow.getWidth());
            }
        }

        // グリッド（撮影補助線）の表示
        if ((viewRect != null)&&(showGridFeature)&&(gridFrameDrawer != null))
        {
            drawGridFrame(canvas, viewRect);
        }
    }

    private RectF decideViewRect(Canvas canvas, Bitmap bitmapToShow, int degrees)
    {
        final int srcWidth;
        final int srcHeight;
        if ((degrees == 0) || (degrees == 180))
        {
            srcWidth = bitmapToShow.getWidth();
            srcHeight = bitmapToShow.getHeight();
        }
        else
        {
            // Replaces width and height.
            srcWidth = bitmapToShow.getHeight();
            srcHeight = bitmapToShow.getWidth();
        }

        int maxWidth = canvas.getWidth();
        int maxHeight = canvas.getHeight();

        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;

        float widthRatio = maxWidth / (float) srcWidth;
        float heightRatio = maxHeight / (float) srcHeight;
        float smallRatio = Math.min(widthRatio, heightRatio);

        final int dstWidth;
        final int dstHeight;
        if (widthRatio < heightRatio)
        {
            // Fits to maxWidth with keeping aspect ratio.
            dstWidth = maxWidth;
            dstHeight = (int)(smallRatio * srcHeight);
        }
        else
        {
            // Fits to maxHeight with keeping aspect ratio.
            dstHeight = maxHeight;
            dstWidth = (int)(smallRatio * srcWidth);
        }

        final float halfWidth = dstWidth * 0.5f;
        final float halfHeight = dstHeight * 0.5f;
        if ((degrees == 0) || (degrees == 180))
        {
            return (new RectF(
                    centerX - halfWidth,
                    centerY - halfHeight,
                    centerX - halfWidth + dstWidth,
                    centerY - halfHeight + dstHeight));
        }

        // Replaces the width and height.
        return (new RectF(
                centerX - halfHeight,
                centerY - halfWidth,
                centerX - halfHeight + dstHeight,
                centerY - halfWidth + dstWidth));
    }

    /**
     *   AF枠の表示
     *
     * @param canvas        キャンバス
     * @param imageWidth   幅
     * @param imageHeight  高さ
     */
    private void drawFocusFrame(Canvas canvas, float imageWidth, float imageHeight)
    {
        //Log.v(TAG, "drawFocusFrame() :" + focusFrameStatus);

        //  Calculate the rectangle of focus.
        RectF focusRectOnImage = convertRectOnViewfinderIntoLiveImage(focusFrameRect, imageWidth, imageHeight, imageRotationDegrees);
        RectF focusRectOnView = convertRectFromImageArea(focusRectOnImage);

        // Draw a rectangle to the canvas.
        Paint focusFramePaint = new Paint();
        focusFramePaint.setStyle(Paint.Style.STROKE);
        switch (focusFrameStatus)
        {
            case Running:
                focusFramePaint.setColor(Color.WHITE);
                break;

            case Focused:
                focusFramePaint.setColor(Color.GREEN);
                break;

            case Failed:
                focusFramePaint.setColor(Color.RED);
                break;

            case Errored:
                focusFramePaint.setColor(Color.YELLOW);
                break;
        }
        float focusFrameStrokeWidth = 2.0f;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, focusFrameStrokeWidth, dm);
        focusFramePaint.setStrokeWidth(strokeWidth);
        canvas.drawRect(focusRectOnView, focusFramePaint);
    }

    /**
     *   グリッドの表示
     *
     * @param canvas    キャンバスエリア
     * @param viewRect  表示領域
     */
    private void drawGridFrame(Canvas canvas, RectF viewRect)
    {
        RectF gridRect;
        if ((imageRotationDegrees == 0) || (imageRotationDegrees == 180)) {
            gridRect = new RectF(viewRect);
        } else {
            float height = viewRect.right - viewRect.left;
            float width = viewRect.bottom - viewRect.top;
            float left = (canvas.getWidth() / 2.0f) - (width / 2.0f);
            float top = (canvas.getHeight() / 2.0f) - (height / 2.0f);
            gridRect = new RectF(left, top, left + width, top + height);
        }

        Paint framePaint = new Paint();
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setAntiAlias(true);
        framePaint.setStrokeWidth(0.0f);
        framePaint.setColor(gridFrameDrawer.getDrawColor());
        gridFrameDrawer.drawFramingGrid(canvas, gridRect, framePaint);
    }

    /**
     * 　 画面にメッセージを表示する
     */
    private void drawInformationMessages(Canvas canvas)
    {
        String message;
        RectF viewRect;
        if (imageBitmap != null)
        {
            // ビットマップの表示エリアに合わせて位置をチューニングする
            viewRect = decideViewRect(canvas, imageBitmap, 0);
        }
        else
        {
            // 適当なサイズ...
            viewRect = new RectF(5.0f, 0.0f, canvas.getWidth() - 5.0f, canvas.getHeight() - 55.0f);
        }

        // 画面の中心に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.CENTER);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.CENTER));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.CENTER));
            paint.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float cx = (canvas.getWidth() / 2.0f) - (paint.measureText(message) / 2.0f);
            float cy = (canvas.getHeight() / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f);
            canvas.drawText(message, cx, cy, paint);
        }

        // 画面上部左側に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.UPLEFT);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paintUp = new Paint();
            paintUp.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.UPLEFT));
            paintUp.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.UPLEFT));
            paintUp.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = paintUp.getFontMetrics();
            canvas.drawText(message, viewRect.left + 3.0f, viewRect.top + (fontMetrics.descent - fontMetrics.ascent), paintUp);
        }

        // 画面上部右側に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.UPRIGHT);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paintUp = new Paint();
            paintUp.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.UPRIGHT));
            paintUp.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.UPRIGHT));
            paintUp.setAntiAlias(true);
            float width = paintUp.measureText(message);
            Paint.FontMetrics fontMetrics = paintUp.getFontMetrics();
            canvas.drawText(message, (viewRect.right - 3.0f) - width, viewRect.top + (fontMetrics.descent - fontMetrics.ascent), paintUp);
        }

        // 画面下部左側に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.LOWLEFT);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.LOWLEFT));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.LOWLEFT));
            paint.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            canvas.drawText(message, viewRect.left + 3.0f, viewRect.bottom - fontMetrics.bottom, paint);
        }

        // 画面下部右側に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.LOWRIGHT);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.LOWRIGHT));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.LOWRIGHT));
            paint.setAntiAlias(true);
            float width = paint.measureText(message);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            canvas.drawText(message, (viewRect.right - 3.0f) - width, viewRect.bottom - fontMetrics.bottom, paint);
        }

        // 画面上部中央に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.UPCENTER);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paintUp = new Paint();
            paintUp.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.UPCENTER));
            paintUp.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.UPCENTER));
            paintUp.setAntiAlias(true);
            float width = paintUp.measureText(message) / 2.0f;
            Paint.FontMetrics fontMetrics = paintUp.getFontMetrics();
            canvas.drawText(message, (viewRect.centerX()) - width, viewRect.top + (fontMetrics.descent - fontMetrics.ascent), paintUp);
        }

        // 画面下部中央に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.LOWCENTER);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.LOWCENTER));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.LOWCENTER));
            paint.setAntiAlias(true);
            float width = paint.measureText(message) / 2.0f;
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            canvas.drawText(message, (viewRect.centerX()) - width, viewRect.bottom - fontMetrics.bottom, paint);
        }

        // 画面中央左に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.LEFTCENTER);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.LEFTCENTER));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.LEFTCENTER));
            paint.setAntiAlias(true);
            paint.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK);  // これで文字に影をつけたい
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float cy = (canvas.getHeight() / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f);
            canvas.drawText(message, viewRect.left + 3.0f, cy, paint);
        }

        // 画面中央右に表示する
        message = messageHolder.getMessage(ShowMessageHolder.MessageArea.RIGHTCENTER);
        if ((message != null)&&(message.length() > 0))
        {
            Paint paint = new Paint();
            paint.setColor(messageHolder.getColor(ShowMessageHolder.MessageArea.RIGHTCENTER));
            paint.setTextSize(messageHolder.getSize(ShowMessageHolder.MessageArea.RIGHTCENTER));
            paint.setAntiAlias(true);
            paint.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK);  // これで文字に影をつけたい
            float width = paint.measureText(message);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float cy = (canvas.getHeight() / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f);
            canvas.drawText(message, (viewRect.right - 3.0f) - width, cy, paint);
        }

    }

    /**
     *   レベルゲージ（デジタル水準器）の表示
     *
     */
    private void drawLevelGauge(Canvas canvas)
    {
        // レベルゲージの表示位置
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        int centerX = width / 2;
        int centerY = height / 2;

        float maxBandWidth = width / 3.0f;     // ゲージの最大長 (画面の 1/3 ぐらい)
        float maxBandHeight = height / 3.0f;   // ゲージの最大長 (画面の 1/3 ぐらい)
        int barWidthInitial = 5;               // 表示するゲージの幅（の初期値）
        int barWidth;                          // 実際に表示するゲージの幅

        Paint paint = new Paint();

        // 垂直線
        float verticalValue = messageHolder.getLevel(IMessageDrawer.LevelArea.LEVEL_VERTICAL);
        float verticalSize = verticalValue / 60.0f * maxBandHeight;  // 45度で切り替わるはずだが、一応...
        if (Math.abs(verticalSize) < 1.0f)
        {
            // 線引き限界以下、水平検出とする (この時の線は倍の長さにする)
            verticalSize = 1.0f;
            barWidth = barWidthInitial * 2;
        }
        else
        {
            barWidth = barWidthInitial;
        }
        paint.setStrokeWidth(barWidth);
        paint.setColor(messageHolder.getLevelColor(verticalValue));
        canvas.drawLine((width - barWidth), centerY, (width - barWidth), (centerY + verticalSize), paint);

        // 水平線
        float horizontalValue = messageHolder.getLevel(IMessageDrawer.LevelArea.LEVEL_HORIZONTAL);
        float horizontalSize = horizontalValue / 60.0f * maxBandWidth;  // 45度ぐらいで切り替わるはずだが、一応...
        if (Math.abs(horizontalSize) < 1.0f)
        {
            // 線引き限界以下、水平検出とする (この時の線は倍の長さにする）
            horizontalSize = 1.0f;
            barWidth = barWidthInitial * 2;
        }
        else
        {
            barWidth = barWidthInitial;
        }
        paint.setStrokeWidth(barWidth);
        paint.setColor(messageHolder.getLevelColor(horizontalValue));
        canvas.drawLine(centerX, (height - barWidth), (centerX + horizontalSize),  (height - barWidth), paint);

        // 水平線 (スクリーン内の線)
        float horizontalValue0 = messageHolder.getLevel(IMessageDrawer.LevelArea.LEVEL_HORIZONTAL);
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);
        paint.setColor(messageHolder.getLevelColor(horizontalValue0));
        if ((imageRotationDegrees == 0) || (imageRotationDegrees == 180))
        {
            // 通常状態
            float YY = canvas.getHeight() / 2.0f; // centerY
            float diffY = (float) Math.sin(Math.toRadians(horizontalValue0)) * (float) centerX;
            canvas.drawLine(0, (YY + diffY), width, (YY - diffY), paint);
        }
        else
        {
            // 縦持ち状態
            float XX = canvas.getWidth() / 2.0f; // centerX
            float diffX = (float) Math.sin(Math.toRadians(horizontalValue0)) * (float) centerY;
            canvas.drawLine((XX + diffX), 0, (XX - diffX), canvas.getHeight(), paint);

        }
    }

    /**
     * Converts a point on image area to a point on view area.
     *
     * @param point A point on image area. (e.g. a live preview image)
     * @return A point on view area. (e.g. a touch panel view)
     */
    private PointF convertPointFromImageArea(PointF point)
    {
        if (imageBitmap == null) {
            return new PointF();
        }

        float viewPointX = point.x;
        float viewPointY = point.y;
        float imageSizeWidth;
        float imageSizeHeight;
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180)
        {
            imageSizeWidth = imageBitmap.getWidth();
            imageSizeHeight = imageBitmap.getHeight();
        }
        else
        {
            imageSizeWidth = imageBitmap.getHeight();
            imageSizeHeight = imageBitmap.getWidth();
        }
        float viewSizeWidth = this.getWidth();
        float viewSizeHeight = this.getHeight();
        float ratioX = viewSizeWidth / imageSizeWidth;
        float ratioY = viewSizeHeight / imageSizeHeight;
        float scale;

        switch (imageScaleType) {
            case FIT_XY:
                viewPointX *= ratioX;
                viewPointY *= ratioY;
                break;
            case FIT_CENTER:	// go to next label.
            case CENTER_INSIDE:
                scale = Math.min(ratioX, ratioY);
                viewPointX *= scale;
                viewPointY *= scale;
                viewPointX += (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
                viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
                break;
            case CENTER_CROP:
                scale = Math.max(ratioX, ratioY);
                viewPointX *= scale;
                viewPointY *= scale;
                viewPointX += (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
                viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
                break;
            case CENTER:
                viewPointX += viewSizeWidth / 2.0  - imageSizeWidth  / 2.0f;
                viewPointY += viewSizeHeight / 2.0 - imageSizeHeight / 2.0f;
                break;
            default:
                break;
        }

        return new PointF(viewPointX, viewPointY);
    }

    /**
     * Converts a point on view area to a point on image area.
     *
     * @param point A point on view area. (e.g. a touch panel view)
     * @return A point on image area. (e.g. a live preview image)
     */
    private PointF convertPointFromViewArea(PointF point)
    {
        if (imageBitmap == null)
        {
            return new PointF();
        }

        float imagePointX = point.x;
        float imagePointY = point.y;
        float imageSizeWidth;
        float imageSizeHeight;
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
            imageSizeWidth = imageBitmap.getWidth();
            imageSizeHeight = imageBitmap.getHeight();
        } else {
            imageSizeWidth = imageBitmap.getHeight();
            imageSizeHeight = imageBitmap.getWidth();
        }
        float viewSizeWidth = this.getWidth();
        float viewSizeHeight = this.getHeight();
        float ratioX = viewSizeWidth / imageSizeWidth;
        float ratioY = viewSizeHeight / imageSizeHeight;
        float scale;// = 1.0f;

        switch (imageScaleType) {
            case FIT_XY:
                imagePointX /= ratioX;
                imagePointY /= ratioY;
                break;
            case FIT_CENTER:	// go to next label.
            case CENTER_INSIDE:
                scale = Math.min(ratioX, ratioY);
                imagePointX = imagePointX - (viewSizeWidth  - imageSizeWidth * scale) / 2.0f;
                imagePointY = imagePointY - (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
                imagePointX = imagePointX / scale;
                imagePointY = imagePointY / scale;
                break;
            case CENTER_CROP:
                scale = Math.max(ratioX, ratioY);
                imagePointX -= (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
                imagePointY -= (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
                imagePointX /= scale;
                imagePointY /= scale;
                break;
            case CENTER:
                imagePointX -= (viewSizeWidth - imageSizeWidth)  / 2.0f;
                imagePointY -= (viewSizeHeight - imageSizeHeight) / 2.0f;
                break;
            default:
                break;
        }

        return new PointF(imagePointX, imagePointY);
    }

    /**
     * Converts a rectangle on image area to a rectangle on view area.
     *
     * @param rect A rectangle on image area. (e.g. a live preview image)
     * @return A rectangle on view area. (e.g. a touch panel view)
     */
    private RectF convertRectFromImageArea(RectF rect)
    {
        if (imageBitmap == null)
        {
            return new RectF();
        }

        PointF imageTopLeft =  new PointF(rect.left, rect.top);
        PointF imageBottomRight = new PointF(rect.right, rect.bottom);

        PointF viewTopLeft = convertPointFromImageArea(imageTopLeft);
        PointF viewBottomRight = convertPointFromImageArea(imageBottomRight);

        return (new RectF(viewTopLeft.x, viewTopLeft.y, viewBottomRight.x, viewBottomRight.y));
    }

    @Override
    public void toggleFocusAssist()
    {
        focusAssistFeature = !focusAssistFeature;
    }

    @Override
    public void toggleShowGridFrame()
    {
        showGridFeature = !showGridFeature;
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IPreferencePropertyAccessor.SHOW_GRID_STATUS, showGridFeature);
        editor.apply();
    }

    /**
     *   現在のライブビュー画像を保管する
     *
     *
     */
    @Override
    public void takePicture()
    {
        if ((imageBitmap == null)||(storeImage == null))
        {
            //　保管する画像がなかった...　何もせずに終了する
            //
            return;
        }
        storeImage.doStore(imageBitmap);
    }

    public boolean isShowGrid()
    {
        return (showGridFeature);
    }

    @Override
    public IMessageDrawer getMessageDrawer()
    {
        return (messageHolder.getMessageDrawer());
    }


    /**
     *    IIndicatorControl の実装
     *
     *
     *
     */
    @Override
    public void onAfLockUpdate(boolean isAfLocked)
    {
        //Log.v(TAG, "onAfLockUpdate() : " + isAfLocked);
    }

    @Override
    public void onShootingStatusUpdate(shootingStatus status)
    {
        //Log.v(TAG, "onShootingStatusUpdate() : " + status);
    }

    @Override
    public void onMovieStatusUpdate(shootingStatus status)
    {
        //Log.v(TAG, "onMovieStatusUpdate() : " + status);
    }

    @Override
    public void onBracketingStatusUpdate(String message)
    {
       // Log.v(TAG, "onBracketingStatusUpdate() : " + message);
    }
}
