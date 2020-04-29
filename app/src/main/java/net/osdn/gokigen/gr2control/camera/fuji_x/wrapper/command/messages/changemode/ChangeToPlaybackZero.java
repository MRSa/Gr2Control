package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class ChangeToPlaybackZero extends FujiXCommandBase
{
    private final int holdId;
    private final IFujiXCommandCallback callback;

    private final byte data0;
    private final byte data1;
    private final byte data2;
    private final byte data3;

    public ChangeToPlaybackZero(int holdId, int value, @NonNull IFujiXCommandCallback callback)
    {
        this.holdId = holdId;
        this.callback = callback;

        data0 = ((byte) (0x000000ff & value));
        data1 = ((byte)((0x0000ff00 & value) >> 8));
        data2 = ((byte)((0x00ff0000 & value) >> 16));
        data3 = ((byte)((0xff000000 & value) >> 24));
    }

    @Override
    public IFujiXCommandCallback responseCallback()
    {
        return (callback);
    }

    @Override
    public int getId()
    {
        return (IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_ZERO);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {

                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                ////////
                (byte)0x18, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                data0, data1, data2, data3,
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
}
