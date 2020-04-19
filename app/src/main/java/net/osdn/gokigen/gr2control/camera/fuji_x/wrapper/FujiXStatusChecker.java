package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;

public class FujiXStatusChecker implements ICameraStatusWatcher, ICameraStatus
{
    private final String TAG = toString();
    private final int sleepMs;

    FujiXStatusChecker(int sleepMs)
    {
        this.sleepMs = sleepMs;

    }

    @NonNull
    @Override
    public List<String> getStatusList(@NonNull String key)
    {
        List<String> statusList = new ArrayList<>();

        return (statusList);
    }

    @Override
    public String getStatus(@NonNull String key)
    {
        return (null);
    }

    @Override
    public void setStatus(@NonNull String key, @NonNull String value)
    {

    }

    @Override
    public void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier)
    {

    }

    @Override
    public void stopStatusWatch()
    {

    }
}
