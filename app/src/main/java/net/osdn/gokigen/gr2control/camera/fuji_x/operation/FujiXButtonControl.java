package net.osdn.gokigen.gr2control.camera.fuji_x.operation;

import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;

public class FujiXButtonControl implements ICameraButtonControl
{
    public FujiXButtonControl()
    {


    }

    @Override
    public boolean pushedButton(String code, boolean isLongPress)
    {
        return (false);
    }
}
