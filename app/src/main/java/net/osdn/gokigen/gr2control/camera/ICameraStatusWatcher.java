package net.osdn.gokigen.gr2control.camera;

import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import androidx.annotation.NonNull;

public interface ICameraStatusWatcher
{
    void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier);
    void stopStatusWatch();
}
