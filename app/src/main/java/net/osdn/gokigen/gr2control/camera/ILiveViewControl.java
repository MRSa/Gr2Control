package net.osdn.gokigen.gr2control.camera;

/**
 *
 *
 */
public interface ILiveViewControl
{
    void changeLiveViewSize(String size);

    void startLiveView();
    void stopLiveView();
    void updateDigitalZoom();
    void updateMagnifyingLiveViewScale(boolean isChangeScale);
    float getMagnifyingLiveViewScale();
    float getDigitalZoomScale();
}
