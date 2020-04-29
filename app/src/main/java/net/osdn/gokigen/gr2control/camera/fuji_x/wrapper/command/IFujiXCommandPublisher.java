package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

import androidx.annotation.NonNull;

public interface IFujiXCommandPublisher
{
    boolean isConnected();
    boolean enqueueCommand(@NonNull IFujiXCommand command);

    boolean flushHoldQueue();

    void start();
    void stop();
}
