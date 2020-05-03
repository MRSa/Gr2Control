package net.osdn.gokigen.gr2control.liveview;

import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.CommandGeneric;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.IFujiXCameraCommands;
import net.osdn.gokigen.gr2control.camera.utils.SimpleLogDumper;

public class LiveViewFujiXKeyPanelClickListener implements View.OnClickListener, View.OnLongClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private static final boolean isDumpLog = false;
    private final IInterfaceProvider interfaceProvider;
    private final Vibrator vibrator;

    LiveViewFujiXKeyPanelClickListener(@NonNull IInterfaceProvider interfaceProvider, @Nullable Vibrator vibrator)
    {
        this.interfaceProvider = interfaceProvider;
        this.vibrator = vibrator;
    }

    @Override
    public void onClick(View v)
    {
        boolean isVibrate = true;
        try
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.button_fuji_x_sv_minus:
                    updateValue(IFujiXCameraCommands.SHUTTER_SPEED, 0);
                    break;
                case R.id.button_fuji_x_sv_plus:
                    updateValue(IFujiXCameraCommands.SHUTTER_SPEED, 1);
                    break;
                case R.id.button_fuji_x_tv_minus:
                    updateValue(IFujiXCameraCommands.APERTURE, 0);
                    break;
                case R.id.button_fuji_x_tv_plus:
                    updateValue(IFujiXCameraCommands.APERTURE, 1);
                    break;
                case R.id.button_fuji_x_xv_minus:
                    updateValue(IFujiXCameraCommands.EXPREV, 0);
                    break;
                case R.id.button_fuji_x_xv_plus:
                    updateValue(IFujiXCameraCommands.EXPREV, 1);
                    break;
                default:
                    isVibrate = false;
                    break;
            }
            vibrate(isVibrate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        boolean isVibrate = false;
        boolean ret = false;
        try
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.button_fuji_x_sv_minus:
                    break;
                case R.id.button_fuji_x_sv_plus:
                    break;
                case R.id.button_fuji_x_tv_minus:
                    break;
                case R.id.button_fuji_x_tv_plus:
                    break;
                case R.id.button_fuji_x_xv_minus:
                    break;
                case R.id.button_fuji_x_xv_plus:
                    break;
                default:
                    break;
            }
            vibrate(isVibrate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
    }

    /**
     *  値を更新する
     *
     */
    private void updateValue(int id, int value)
    {
        try
        {
            IFujiXCommandPublisher publisher = interfaceProvider.getFujiXInterfaceProvider().getCommandPublisher();
            publisher.enqueueCommand(new CommandGeneric(this, id, 4, value));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ぶるぶるさせる
     *
     */
    private void vibrate(boolean isVibrate)
    {
        if ((vibrator != null)&&(isVibrate))
        {
            vibrator.vibrate(30);
        }
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        if (isDumpLog)
        {
            SimpleLogDumper.dump_bytes("" + id, rx_body);
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
    {
        Log.v(TAG, " onReceiveProgress() : " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }
}
