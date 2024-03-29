package net.osdn.gokigen.gr2control.liveview.gridframe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 *   対角線のグリッド表示
 */
public class GridFrameDrawerDefault implements IGridFrameDrawer
{
    /**
     *
     *
     */
    @Override
    public void drawFramingGrid(Canvas canvas, RectF rect, Paint paint)
    {
        float w = (rect.right - rect.left) / 3.0f;
        float h = (rect.bottom - rect.top) / 3.0f;

        canvas.drawLine(rect.left + w, rect.top, rect.left + w, rect.bottom, paint);
        canvas.drawLine(rect.left + 2.0f * w, rect.top, rect.left + 2.0f * w, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.top + h, rect.right, rect.top + h, paint);
        canvas.drawLine(rect.left, rect.top + 2.0f * h, rect.right, rect.top + 2.0f * h, paint);
        canvas.drawRect(rect, paint);
    }

    @Override
    public int getDrawColor()
    {
        return (Color.argb(130,235,235,235));
    }
}
