package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class ChangeToLiveView7th extends FujiXCommandBase
{
    private final int holdId;
    private final IFujiXCommandCallback callback;

    public ChangeToLiveView7th(int holdId, @NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_7TH);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : 0x902b
                (byte)0x2b, (byte)0x90,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                //(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
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
        return (false);
    }

    @Override
    public boolean isRelease()
    {
        return (true);
    }

    @Override
    public boolean dumpLog()
    {
        return (false);
    }
}
