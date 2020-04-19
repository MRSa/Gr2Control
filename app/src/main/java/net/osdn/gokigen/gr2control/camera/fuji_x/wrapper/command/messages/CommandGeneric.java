package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;

public class CommandGeneric extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;
    private final int bodySize;
    private final byte id0;
    private final byte id1;
    private final byte data0;
    private final byte data1;
    private final byte data2;
    private final byte data3;
    private final byte data4;
    private final byte data5;
    private final byte data6;
    private final byte data7;

    public CommandGeneric(@NonNull IFujiXCommandCallback callback, int id)
    {
        this.callback = callback;
        this.bodySize = 0;

        id0 = ((byte) (0x000000ff & id));
        id1 = ((byte)((0x0000ff00 & id) >> 8));

        data0 = 0;
        data1 = 0;
        data2 = 0;
        data3 = 0;

        data4 = 0;
        data5 = 0;
        data6 = 0;
        data7 = 0;
    }

    public CommandGeneric(@NonNull IFujiXCommandCallback callback, int id, int bodySize, int value)
    {
        this.callback = callback;
        this.bodySize = bodySize;

        id0 = ((byte) (0x000000ff & id));
        id1 = ((byte)((0x0000ff00 & id) >> 8));

        data0 = ((byte) (0x000000ff & value));
        data1 = ((byte)((0x0000ff00 & value) >> 8));
        data2 = ((byte)((0x00ff0000 & value) >> 16));
        data3 = ((byte)((0xff000000 & value) >> 24));

        data4 = ((byte) (0x000000ff & value));
        data5 = ((byte)((0x0000ff00 & value) >> 8));
        data6 = ((byte)((0x00ff0000 & value) >> 16));
        data7 = ((byte)((0xff000000 & value) >> 24));
    }

    public CommandGeneric(@NonNull IFujiXCommandCallback callback, int id, int bodySize, int value, int value2)
    {
        this.callback = callback;
        this.bodySize = bodySize;

        id0 = ((byte) (0x000000ff & id));
        id1 = ((byte)((0x0000ff00 & id) >> 8));

        data0 = ((byte) (0x000000ff & value));
        data1 = ((byte)((0x0000ff00 & value) >> 8));
        data2 = ((byte)((0x00ff0000 & value) >> 16));
        data3 = ((byte)((0xff000000 & value) >> 24));

        data4 = ((byte) (0x000000ff & value2));
        data5 = ((byte)((0x0000ff00 & value2) >> 8));
        data6 = ((byte)((0x00ff0000 & value2) >> 16));
        data7 = ((byte)((0xff000000 & value2) >> 24));
    }


    @Override
    public IFujiXCommandCallback responseCallback()
    {
        return (callback);
    }

    @Override
    public int getId()
    {
        return (999);
    }

    @Override
    public byte[] commandBody()
    {
        if (bodySize == 2)
        {
            return (new byte[]{

                    // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                    (byte) 0x01, (byte) 0x00,

                    // message_header.type
                    id0, id1,

                    // sequence number
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,

                    // data ...
                    data0, data1,
            });
        }
        else if (bodySize == 4)
        {
            return (new byte[]{

                    // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                    (byte) 0x01, (byte) 0x00,

                    // message_header.type
                    id0, id1,

                    // sequence number
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,

                    // data ...
                    data0, data1, data2, data3,
            });
        }
        else if (bodySize == 8)
        {
            return (new byte[]{

                    // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                    (byte) 0x01, (byte) 0x00,

                    // message_header.type
                    id0, id1,

                    // sequence number
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,

                    // data ...
                    data0, data1, data2, data3, data4, data5, data6, data7,
            });
        }
        else //  ボディ長が 2, 4, 8 以外の場合...
        {
            return (new byte[]{

                    // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                    (byte) 0x01, (byte) 0x00,

                    // message_header.type
                    id0, id1,

                    // sequence number
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            });
        }
    }
}
