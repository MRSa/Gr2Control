package net.osdn.gokigen.gr2control.liveview.bitmapconvert;

/**
 *
 *
 */
public class ImageConvertFactory
{
    private static final int CONVERT_TYPE_0 = 0;
    private static final int CONVERT_TYPE_1 = 1;
    private static final int CONVERT_TYPE_2 = 2;
    private static final int CONVERT_TYPE_3 = 3;
    private static final int CONVERT_TYPE_4 = 4;
    private static final int CONVERT_TYPE_5 = 5;
    private static final int CONVERT_TYPE_6 = 6;

    public static IPreviewImageConverter getImageConverter(int id)
    {
        return (new ConvertNothing());
    }
}
