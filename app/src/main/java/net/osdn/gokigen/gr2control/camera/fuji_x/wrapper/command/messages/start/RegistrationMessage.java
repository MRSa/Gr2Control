package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.start;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FujiXCommandBase;

public class RegistrationMessage extends FujiXCommandBase
{
    private final IFujiXCommandCallback callback;

    public RegistrationMessage(@NonNull IFujiXCommandCallback callback)
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
        return (IFujiXMessages.SEQ_REGISTRATION);
    }

    @Override
    public boolean receiveAgainShortLengthMessage()
    {
        return (false);
    }

    @Override
    public boolean useSequenceNumber()
    {
        return (false);
    }

    @Override
    public boolean isIncrementSeqNumber()
    {
        return (false);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[] {
                // header
                (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xf2, (byte)0xe4, (byte)0x53, (byte)0x8f,
                (byte)0xad, (byte)0xa5, (byte)0x48, (byte)0x5d, (byte)0x87, (byte)0xb2, (byte)0x7f, (byte)0x0b,
                (byte)0xd3, (byte)0xd5, (byte)0xde, (byte)0xd0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
/**/
                // device_name 'GOKIGEN___a01Series'
                (byte)0x47, (byte)0x00, (byte)0x4f, (byte)0x00, (byte)0x4b, (byte)0x00, (byte)0x49, (byte)0x00,
                (byte)0x47, (byte)0x00, (byte)0x45, (byte)0x00, (byte)0x4e, (byte)0x00, (byte)0x5f, (byte)0x00,
                (byte)0x5f, (byte)0x00, (byte)0x5f, (byte)0x00, (byte)0x61, (byte)0x00, (byte)0x30, (byte)0x00,
                (byte)0x31, (byte)0x00, (byte)0x53, (byte)0x00, (byte)0x65, (byte)0x00, (byte)0x72, (byte)0x00,
                (byte)0x69, (byte)0x00, (byte)0x65, (byte)0x00, (byte)0x73, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
/**/
        });
    }

    @Override
    public boolean dumpLog()
    {
        return (false);
    }
}
