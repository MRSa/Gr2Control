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
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback1st;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback2nd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback3rd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback4th;

public class FujiXCameraModeChangeToPlayback implements View.OnClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private final IFujiXCommandPublisher publisher;
    private final IFujiXCommandCallback callback;
    private IFujiXRunModeHolder runModeHolder = null;

    public FujiXCameraModeChangeToPlayback(@NonNull IFujiXCommandPublisher publisher, @Nullable IFujiXCommandCallback callback)
    {
        this.publisher = publisher;
        this.callback = callback;
    }

    public void startModeChange(IFujiXRunModeHolder runModeHolder)
    {
        Log.v(TAG, "startModeChange()");
        try
        {
            if (runModeHolder != null)
            {
                this.runModeHolder = runModeHolder;
                this.runModeHolder.transitToPlaybackMode(false);
            }
            publisher.enqueueCommand(new ChangeToPlayback1st(this));
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
                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_1ST:
                    publisher.enqueueCommand(new ChangeToPlayback2nd(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_2ND:
                    publisher.enqueueCommand(new ChangeToPlayback3rd(this));
                    //publisher.enqueueCommand(new StatusRequestMessage(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_3RD:
                    publisher.enqueueCommand(new ChangeToPlayback4th(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_4TH:
                    publisher.enqueueCommand(new StatusRequestMessage(this));
                    break;

                case IFujiXMessages.SEQ_STATUS_REQUEST:
                    if (callback != null)
                    {
                        callback.receivedMessage(id, rx_body);
                    }
                    if (runModeHolder != null)
                    {
                        runModeHolder.transitToPlaybackMode(true);
                    }
                    Log.v(TAG, " CHANGED PLAYBACK MODE : DONE.");
                    break;

                default:
                    Log.v(TAG, " RECEIVED UNKNOWN ID : " + id);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
