package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class ChangeToPlayback3rd extends FujiXCommandBase
{
    private final int holdId;
    private final IFujiXCommandCallback callback;

    public ChangeToPlayback3rd(int holdId, @NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_3RD);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : 0x1015
                (byte)0x15, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                (byte)0x12, (byte)0xd2, (byte)0x00, (byte)0x00,
                //(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
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
