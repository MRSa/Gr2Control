package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode;


import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class ChangeToLiveView2nd    extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;

    public ChangeToLiveView2nd(@NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_2ND);
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
                (byte)0x01, (byte)0xdf, (byte)0x00, (byte)0x00,
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
                (byte)0x05, (byte)0x00,

        });
    }

}
