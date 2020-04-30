package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class ChangeToLiveView4th   extends FujiXCommandBase
{
    private final int holdId;
    private final IFujiXCommandCallback callback;

    public ChangeToLiveView4th(int holdId, @NonNull IFujiXCommandCallback callback)
    {
        this.holdId = holdId;
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
        return (IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_4TH);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : two_part (0x1016) : SetDevicePropValue
                (byte)0x16, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                (byte)0x24, (byte)0xdf, (byte)0x00, (byte)0x00,
        });
    }

    @Override
    public byte[] commandBody2()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x02, (byte)0x00,

                // message_header.type : two_part (0x1016) : SetDevicePropValue
                (byte)0x16, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                //(byte)0x07, (byte)0x00, (byte)0x02, (byte)0x00,
                (byte)0x0a, (byte)0x00, (byte)0x02, (byte)0x00,
        });
    }

    @Override
    public int getHoldId()
    {
        return (holdId);
    }

    @Override
    public boolean isHold()
    {
        return (true);
    }

    @Override
    public boolean isRelease()
    {
        return (false);
    }

    @Override
    public boolean dumpLog()
    {
        return (false);
    }
}
