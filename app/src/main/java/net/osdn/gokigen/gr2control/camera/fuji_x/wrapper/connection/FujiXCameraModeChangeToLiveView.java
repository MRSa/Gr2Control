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
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveView6th;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.changemode.ChangeToLiveViewZero;


public class FujiXCameraModeChangeToLiveView implements View.OnClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private static final int COMMANDID_CHANGE_TO_LIVEVIEW = 100;
    private final IFujiXCommandPublisher publisher;
    private final IFujiXCommandCallback callback;
    private IFujiXRunModeHolder runModeHolder = null;
    private int changedLiveviewSeqNumber = 8;

    public FujiXCameraModeChangeToLiveView(@NonNull IFujiXCommandPublisher publisher, @Nullable IFujiXCommandCallback callback)
    {
        this.publisher = publisher;
        this.callback = callback;
    }

    public void startModeChange(IFujiXRunModeHolder runModeHolder)
    {
        Log.v(TAG, " startModeChange() : FujiXCameraModeChangeToLiveView");
        try
        {
            if (runModeHolder != null)
            {
                this.runModeHolder = runModeHolder;
                this.runModeHolder.transitToRecordingMode(false);
            }
            publisher.enqueueCommand(new ChangeToLiveViewZero(COMMANDID_CHANGE_TO_LIVEVIEW,this));
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
                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_ZERO:
                    publisher.enqueueCommand(new ChangeToLiveView1st(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_1ST:
                    publisher.enqueueCommand(new ChangeToLiveView2nd(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_2ND:
                    publisher.enqueueCommand(new ChangeToLiveView3rd(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_3RD:
                    publisher.enqueueCommand(new ChangeToLiveView4th(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    break;
                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_4TH:
                    publisher.enqueueCommand(new ChangeToLiveView6th(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_5TH:
                    //publisher.enqueueCommand(new ChangeToLiveView6th(COMMANDID_CHANGE_TO_LIVEVIEW,this));

                    //  Liveview切り替え時のシーケンス番号を記憶する
                    changedLiveviewSeqNumber = getSequenceNumber(rx_body);
                    publisher.enqueueCommand(new StatusRequestMessage(this));
                    break;

                case IFujiXMessages.SEQ_CHANGE_TO_LIVEVIEW_6TH:
                    publisher.enqueueCommand(new ChangeToLiveView5th(COMMANDID_CHANGE_TO_LIVEVIEW,this));
                    //publisher.enqueueCommand(new StatusRequestMessage(this));
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
                    Log.v(TAG, " - - - - - CHANGED LIVEVIEW MODE : DONE.");
                    break;

                default:
                    Log.v(TAG, "  RECEIVED UNKNOWN ID : " + id);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int getSequenceNumber(byte[] byte_array)
    {
        int seqNumber = 8;  // 起動時の初期値...
        try
        {
            if (byte_array.length > 11)
            {
                seqNumber = ((((int) byte_array[11]) & 0xff) << 24) + ((((int) byte_array[10]) & 0xff) << 16) + ((((int) byte_array[9]) & 0xff) << 8) + (((int) byte_array[8]) & 0xff);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (seqNumber);
    }

    public int getChangedSequenceNumber()
    {
        return (changedLiveviewSeqNumber);
    }

}
