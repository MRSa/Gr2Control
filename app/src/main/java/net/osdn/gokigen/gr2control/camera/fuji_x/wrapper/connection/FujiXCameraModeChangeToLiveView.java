package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.IFujiXRunModeHolder;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.StatusRequestMessage;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView1st;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView2nd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView3rd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView4th;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView5th;


public class FujiXCameraModeChangeToLiveView implements View.OnClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private final IFujiXCommandPublisher publisher;
    private final IFujiXCommandCallback callback;
    private IFujiXRunModeHolder runModeHolder = null;

    public FujiXCameraModeChangeToLiveView(@NonNull IFujiXCommandPublisher publisher, @Nullable IFujiXCommandCallback callback)
    {
        this.publisher = publisher;
        this.callback = callback;
    }

    public void startModeChange(IFujiXRunModeHolder runModeHolder)
    {
        Log.v(TAG, "onClick");
        try
        {
            if (runModeHolder != null)
            {
                this.runModeHolder = runModeHolder;
                this.runModeHolder.transitToRecordingMode(false);
            }
            publisher.enqueueCommand(new ChangeToLiveView1st(this));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        Log.v(TAG, "onClick");
        startModeChange(null);
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] body)
    {
        Log.v(TAG, " " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        //Log.v(TAG, "receivedMessage : " + id + "[" + rx_body.length + " bytes]");
        //int bodyLength = 0;
        try
        {
            switch (id)
            {
                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_1ST:
                    publisher.enqueueCommand(new ChangeToLiveView2nd(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_2ND:
                    publisher.enqueueCommand(new ChangeToLiveView3rd(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_3RD:
                    publisher.enqueueCommand(new ChangeToLiveView4th(this));
                    break;
                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_4TH:
                    publisher.enqueueCommand(new ChangeToLiveView5th(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_5TH:
                    publisher.enqueueCommand(new StatusRequestMessage(this));
                    break;

                case IFujiXMessages.SEQ_STATUS_REQUEST:
                    if (callback != null)
                    {
                        callback.receivedMessage(id, rx_body);
                    }
                    if (runModeHolder != null)
                    {
                        runModeHolder.transitToRecordingMode(true);
                    }
                    Log.v(TAG, "CHANGED LIVEVIEW MODE : DONE.");
                    break;

                default:
                    Log.v(TAG, "RECEIVED UNKNOWN ID : " + id);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
