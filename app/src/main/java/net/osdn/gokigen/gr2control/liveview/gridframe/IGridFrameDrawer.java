package net.osdn.gokigen.gr2control.liveview.gridframe;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 *   撮影補助線の描画クラス
 *
 */
public interface IGridFrameDrawer
{
    void drawFramingGrid(Canvas canvas, RectF rect, Paint paint);
    int getDrawColor();
}
