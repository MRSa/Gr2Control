package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import net.osdn.gokigen.gr2control.camera.ICameraRunMode;

public class RicohGr2RunMode implements ICameraRunMode
{
    @Override
    public void changeRunMode(boolean isRecording)
    {
        // 何もしない...
    }

    @Override
    public boolean isRecordingMode()
    {
        return (true);
    }
}
