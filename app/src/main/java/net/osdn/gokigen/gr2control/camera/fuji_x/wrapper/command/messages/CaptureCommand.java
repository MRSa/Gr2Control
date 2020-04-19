package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;


public class CaptureCommand extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;

    public CaptureCommand(@NonNull IFujiXCommandCallback callback)
    {
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
        return (IFujiXMessages.SEQ_CAPTURE);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {

                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : shutter (0x100e)
                (byte)0x0e, (byte)0x10,

                // sequence number
                (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        });
    }
}
