package net.osdn.gokigen.gr2control.camera;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

public interface ICameraStatusWatcher
{
    void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier);
    void stoptStatusWatch();
}
