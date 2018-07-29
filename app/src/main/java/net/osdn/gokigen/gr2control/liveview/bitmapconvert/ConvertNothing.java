package net.osdn.gokigen.gr2control.liveview.bitmapconvert;

import android.graphics.Bitmap;

import net.osdn.gokigen.gr2control.liveview.bitmapconvert.IPreviewImageConverter;

/**
 *
 *
 */
class ConvertNothing implements IPreviewImageConverter
{
    /**
     *   変換後のビットマップを応答する
     *
     * @return 変換後のビットマップ
     */
    @Override
    public Bitmap getModifiedBitmap(Bitmap src)
    {
        return (src);
    }
}
