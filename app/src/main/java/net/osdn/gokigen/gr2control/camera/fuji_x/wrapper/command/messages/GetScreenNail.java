package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;



import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;

public class GetScreenNail  extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;
    private final byte lower;
    private final byte upper;
    private final byte data0;
    private final byte data1;
    private final byte data2;
    private final byte data3;

    public GetScreenNail(int indexNumber, int imageSize, @NonNull IFujiXCommandCallback callback)
    {
        this.lower = ((byte) (0x000000ff & indexNumber));
        this.upper = ((byte)((0x0000ff00 & indexNumber) >> 8));

        data0 = ((byte) (0x000000ff & imageSize));
        data1 = ((byte)((0x0000ff00 & imageSize) >> 8));
        data2 = ((byte)((0x00ff0000 & imageSize) >> 16));
        data3 = ((byte)((0xff000000 & imageSize) >> 24));
        this.callback = callback;
    }

    @Override
    public IFujiXCommandCallback responseCallback()
    {
        return (callback);
    }

    @Override
    public int getId()
    {
        return (IFujiXMessages.SEQ_FULL_IMAGE);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {

                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : full_image (0x101b)
                (byte)0x1b, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ... (index number)
                lower, upper, (byte)0x00, (byte)0x00,

                // 現物合わせ１  : 0～　
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // 現物合わせ２  : ～0x01000000 bytes
                data0, data1, data2, data3,
        });
    }
    @Override
    public boolean dumpLog()
    {
        return (false);
    }
}
