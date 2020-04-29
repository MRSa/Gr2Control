package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

public interface IFujiXRunModeHolder
{
    void transitToRecordingMode(boolean isFinished);
    void transitToPlaybackMode(boolean isFinished);

    int getStartLiveViewSequenceNumber();
}
