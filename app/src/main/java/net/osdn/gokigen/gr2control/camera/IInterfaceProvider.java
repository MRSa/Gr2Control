package net.osdn.gokigen.gr2control.camera;

import net.osdn.gokigen.gr2control.camera.ricohgr2.IRicohGr2InterfaceProvider;

/**
 *
 */
public interface IInterfaceProvider
{
    //IOlympusInterfaceProvider getOlympusInterface();
    //IOlympusLiveViewListener getOlympusLiveViewListener();
    //ISonyInterfaceProvider getSonyInterface();
    IRicohGr2InterfaceProvider getRicohGr2Infterface();

    ICameraConnection.CameraConnectionMethod getCammeraConnectionMethod();
}
