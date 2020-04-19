package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.CameraLiveViewListenerImpl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;

public class FujiXLiveViewControl  implements ILiveViewControl
{
    private final String TAG = toString();
    private final CameraLiveViewListenerImpl liveViewListener;

    /**
     *
     *
     */
    FujiXLiveViewControl()
    {
        this.liveViewListener = new CameraLiveViewListenerImpl();
    }


    @Override
    public void changeLiveViewSize(String size)
    {

    }

    @Override
    public void startLiveView(boolean isCameraScreen)
    {

    }

    @Override
    public void stopLiveView()
    {

    }

    @Override
    public void updateDigitalZoom()
    {

    }

    @Override
    public void updateMagnifyingLiveViewScale(boolean isChangeScale)
    {

    }

    @Override
    public float getMagnifyingLiveViewScale()
    {
        return 0;
    }

    @Override
    public float getDigitalZoomScale()
    {
        return 0;
    }


    public ILiveViewListener getLiveViewListener()
    {
        return (liveViewListener);
    }
}
