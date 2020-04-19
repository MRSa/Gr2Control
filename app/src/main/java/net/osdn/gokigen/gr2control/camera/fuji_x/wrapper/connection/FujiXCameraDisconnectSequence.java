package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection;

import android.app.Activity;

import androidx.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.fuji_x.IFujiXInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommunication;


class FujiXCameraDisconnectSequence  implements Runnable
{
    private final String TAG = this.toString();
    private final Activity activity;
    private final IFujiXCommunication command;
    private final IFujiXCommunication async;
    private final IFujiXCommunication liveview;


    FujiXCameraDisconnectSequence(Activity activity, @NonNull IFujiXInterfaceProvider interfaceProvider)
    {
        this.activity = activity;
        this.command = interfaceProvider.getCommandCommunication();
        this.async = interfaceProvider.getAsyncEventCommunication();
        this.liveview = interfaceProvider.getLiveviewCommunication();
    }

    @Override
    public void run()
    {
        try
        {
            liveview.disconnect();
            async.disconnect();
            command.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
