package net.osdn.gokigen.gr2control.liveview.bitmapconvert;

import android.graphics.Bitmap;

/**
 *   ビットマップ変換
 */
public interface IPreviewImageConverter
{
    Bitmap getModifiedBitmap(Bitmap src);
}
