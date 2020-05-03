package net.osdn.gokigen.gr2control.liveview;

import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;

public class LiveViewFujiXKeyPanelClickListener implements View.OnClickListener, View.OnLongClickListener
{
    private final String TAG = toString();
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

    }

    @Override
    public boolean onLongClick(View v)
    {
        return (false);
    }
}
