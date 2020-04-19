package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;


import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;

public class FocusLock extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;
    private final byte pointX;
    private final byte pointY;

    public FocusLock(byte pointX, byte pointY, @NonNull IFujiXCommandCallback callback)
    {
        this.pointX = pointX;
        this.pointY = pointY;
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
        return (IFujiXMessages.SEQ_FOCUS_LOCK);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {

                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : focus_point (0x9026)
                (byte)0x26, (byte)0x90,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                pointY, pointX, (byte)0x02, (byte)0x03,
        });
    }
}
