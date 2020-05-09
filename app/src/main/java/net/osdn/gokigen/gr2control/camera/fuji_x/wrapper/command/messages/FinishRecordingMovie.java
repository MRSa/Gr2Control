package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;

public class FinishRecordingMovie extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;
    private final byte data0;
    private final byte data1;
    private final byte data2;
    private final byte data3;

    public FinishRecordingMovie(@NonNull IFujiXCommandCallback callback, int startSequenceNumber)
    {
        this.callback = callback;

        data0 = ((byte) (0x000000ff & startSequenceNumber));
        data1 = ((byte)((0x0000ff00 & startSequenceNumber) >> 8));
        data2 = ((byte)((0x00ff0000 & startSequenceNumber) >> 16));
        data3 = ((byte)((0xff000000 & startSequenceNumber) >> 24));
    }

    @Override
    public IFujiXCommandCallback responseCallback()
    {
        return (callback);
    }

    @Override
    public int getId()
    {
        return (IFujiXMessages.SEQ_FINISH_MOVIE);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // message_header.index : uint16 (0: terminate, 2: two_part_message, 1: other)
                (byte)0x01, (byte)0x00,

                // message_header.type : stop_recording_movie (0x9021)
                (byte)0x21, (byte)0x90,

                // sequence number
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                // data...
                data0, data1, data2, data3,
        });
    }
}
