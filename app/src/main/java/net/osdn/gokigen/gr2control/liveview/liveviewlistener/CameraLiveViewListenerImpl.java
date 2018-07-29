package net.osdn.gokigen.gr2control.liveview.liveviewlistener;

import net.osdn.gokigen.gr2control.camera.ICameraLiveViewListener;

import java.util.Map;

public class CameraLiveViewListenerImpl implements ILiveViewListener, ICameraLiveViewListener
{
    private IImageDataReceiver imageView = null;

    /**
     * コンストラクタ
     */
    public CameraLiveViewListenerImpl()
    {

    }

    /**
     * 更新するImageViewを拾う
     *
     */
    @Override
    public void setCameraLiveImageView(IImageDataReceiver target)
    {
        imageView = target;
    }

    /**
     * LiveViewの画像データを更新する
     *
     */
    @Override
    public void onUpdateLiveView(byte[] data, Map<String, Object> metadata)
    {
        if (imageView != null)
        {
            imageView.setImageData(data, metadata);
        }
    }
}
