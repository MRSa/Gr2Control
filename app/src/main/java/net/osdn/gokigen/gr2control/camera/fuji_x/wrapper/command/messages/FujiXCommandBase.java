package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommand;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;

public class FujiXCommandBase implements IFujiXCommand
{
    @Override
    public int getId()
    {
        return (IFujiXMessages.SEQ_DUMMY);
    }

    @Override
    public boolean receiveAgainShortLengthMessage()
    {
        return (true);
    }

    @Override
    public boolean useSequenceNumber()
    {
        return (true);
    }

    @Override
    public boolean isIncrementSeqNumber()
    {
        return (true);
    }

    @Override
    public int receiveDelayMs()
    {
        return (100);
    }

    @Override
    public byte[] commandBody()
    {
        return (new byte[12]);
    }

    @Override
    public byte[] commandBody2()
    {
        return (null);
    }

    @Override
    public IFujiXCommandCallback responseCallback()
    {
        return (null);
    }

    @Override
    public boolean dumpLog()
    {
        return (true);
    }
}
