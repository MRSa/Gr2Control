package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.IFujiXRunModeHolder;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommand;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXMessages;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.StatusRequestMessage;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback1st;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback2nd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback3rd;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback6th;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback7th;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlaybackZero;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback4th;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToPlayback5th;

public class FujiXCameraModeChangeToPlayback implements View.OnClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private static final int COMMANDID_CHANGE_TO_PLAYBACK = 200;
    private final IFujiXCommandPublisher publisher;
    private final IFujiXCommandCallback callback;
    private IFujiXRunModeHolder runModeHolder = null;

    public FujiXCameraModeChangeToPlayback(@NonNull IFujiXCommandPublisher publisher, @Nullable IFujiXCommandCallback callback)
    {
        this.publisher = publisher;
        this.callback = callback;
    }

    public void startModeChange(@Nullable IFujiXRunModeHolder runModeHolder)
    {
        Log.v(TAG, " startModeChange() : FujiXCameraModeChangeToPlayback");
        try
        {
            int seqNumber = 8;
            if (runModeHolder != null)
            {
                this.runModeHolder = runModeHolder;
                this.runModeHolder.transitToPlaybackMode(false);
                seqNumber = runModeHolder.getStartLiveViewSequenceNumber();
            }
            publisher.enqueueCommand(new ChangeToPlaybackZero(COMMANDID_CHANGE_TO_PLAYBACK, seqNumber, this));
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
        try
        {
            switch (id)
            {
                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_ZERO:
                    enqueueCommand(id, rx_body, new ChangeToPlayback1st(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_1ST:
                    enqueueCommand(id, rx_body, new ChangeToPlayback3rd(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_2ND:
                    enqueueCommand(id, rx_body, new ChangeToPlayback7th(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_3RD:
                    enqueueCommand(id, rx_body, new ChangeToPlayback4th(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_4TH:
                    enqueueCommand(id, rx_body, new ChangeToPlayback2nd(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_5TH:
                    enqueueCommand(id, rx_body, new ChangeToPlayback6th(COMMANDID_CHANGE_TO_PLAYBACK,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_6TH:
                    enqueueCommand(id, rx_body, new StatusRequestMessage(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_PLAYBACK_7TH:
                    enqueueCommand(id, rx_body, new ChangeToPlayback5th(COMMANDID_CHANGE_TO_PLAYBACK,this));
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
                    Log.v(TAG, " - - - - - CHANGED PLAYBACK MODE : DONE.");
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

    private void enqueueCommand(int id, byte[] rx_body, IFujiXCommand command)
    {
        //Log.v(TAG, "  --- receivedMessage : " + id + "[" + rx_body.length + " bytes]");
        publisher.enqueueCommand(command);
    }

}
