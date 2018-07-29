package net.osdn.gokigen.gr2control.liveview;

import net.osdn.gokigen.gr2control.liveview.message.IMessageDrawer;

/**
 *
 *
 */
interface ILiveImageStatusNotify
{
    void toggleFocusAssist();
    void toggleShowGridFrame();
    void takePicture();
    IMessageDrawer getMessageDrawer();
}
