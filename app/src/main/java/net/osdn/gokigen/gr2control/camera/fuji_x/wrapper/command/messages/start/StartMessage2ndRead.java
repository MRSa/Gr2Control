package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.start;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXCameraConnectSequence;

public class StartMessage2ndRead extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;

    public StartMessage2ndRead(@NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_START_2ND_READ);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {

                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : single_part (0x1015) : 0xd212 (status_request)
                (byte)0x15, (byte)0x10,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data ...
                (byte)0x12, (byte)0xd2, (byte)0x00, (byte)0x00,
        });
    }
}
