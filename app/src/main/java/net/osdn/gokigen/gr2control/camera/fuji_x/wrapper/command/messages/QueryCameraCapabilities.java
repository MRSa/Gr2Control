package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;

public class QueryCameraCapabilities extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;

    public QueryCameraCapabilities(@NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_QUERY_CAMERA_CAPABILITIES);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : camera_capabilities (0x902b)
                (byte)0x2b, (byte)0x90,

                // sequence number
                (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00,
        });
    }
}
