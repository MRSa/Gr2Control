package net.osdn.gokigen.gr2control.camera;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import net.osdn.gokigen.gr2control.camera.olympus.IOlympusInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.OlympusInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.RicohGr2InterfaceProvider;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;

/**
 *
 *
 */
public class CameraInterfaceProvider implements IInterfaceProvider
{
    //private final Activity context;
    //private final SonyCameraWrapper sony;
    private final OlympusInterfaceProvider olympus;
    private final RicohGr2InterfaceProvider ricohGr2;
    private final Activity context;
    private ICameraConnection.CameraConnectionMethod connectionMethod = ICameraConnection.CameraConnectionMethod.UNKNOWN;

    public static IInterfaceProvider newInstance(@NonNull Activity context, @NonNull ICameraStatusReceiver provider)
    {
        return (new CameraInterfaceProvider(context, provider));
    }

    /**
     *
     *
     */
    private CameraInterfaceProvider(@NonNull Activity context, @NonNull ICameraStatusReceiver provider)
    {
        this.context = context;
        olympus = new OlympusInterfaceProvider(context, provider);
        ricohGr2 = new RicohGr2InterfaceProvider(context, provider);
        //sony = new SonyCameraWrapper(context, provider);
    }

    @Override
    public IOlympusInterfaceProvider getOlympusInterfaceProvider()
    {
        return (olympus);
    }

    /*
    @Override
    public IOlympusLiveViewListener getOlympusLiveViewListener()
    {
        return (olympus.getLiveViewListener());
    }

    @Override
    public ISonyInterfaceProvider getSonyInterface()
    {
        return (sony);
    }
*/

/*
    @Override
    public IRicohGr2InterfaceProvider getRicohGr2Infterface()
    {
        return (ricohGr2);
    }
*/

    /**
     *
     *
     */
    @Override
    public ICameraConnection getCameraConnection()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getOlyCameraConnection());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getRicohGr2CameraConnection());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraButtonControl getButtonControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getButtonControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getButtonControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public IDisplayInjector getDisplayInjector()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getDisplayInjector());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getDisplayInjector());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ILiveViewControl getLiveViewControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getLiveViewControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getLiveViewControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ILiveViewListener getLiveViewListener()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getLiveViewListener());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getLiveViewListener());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public IFocusingControl getFocusingControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getFocusingControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getFocusingControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraInformation getCameraInformation()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getCameraInformation());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getCameraInformation());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public IZoomLensControl getZoomLensControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getZoomLensControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getZoomLensControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICaptureControl getCaptureControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getCaptureControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getCaptureControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraStatus getCameraStatusListHolder()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getCameraStatusListHolder());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getCameraStatusListHolder());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraStatusWatcher getCameraStatusWatcher()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getCameraStatusWatcher());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getCameraStatusWatcher());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public IPlaybackControl getPlaybackControl()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getPlaybackControl());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getPlaybackControl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraHardwareStatus getHardwareStatus()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getHardwareStatus());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getHardwareStatus());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public ICameraRunMode getCameraRunMode()
    {
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = getCammeraConnectionMethodImpl();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                return (olympus.getCameraRunMode());
            }
            else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                return (ricohGr2.getCameraRunMode());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    /**
     *   OPC/GR2/SONY カメラを使用するかどうか
     *
     * @return OPC / SONY / RICOH_GR2  (ICameraConnection.CameraConnectionMethod)
     */
    public ICameraConnection.CameraConnectionMethod getCammeraConnectionMethod()
    {
        return (getCammeraConnectionMethodImpl());
    }

    /**
     *
     *
     */
    @Override
    public void resetCameraConnectionMethod()
    {
        connectionMethod = ICameraConnection.CameraConnectionMethod.UNKNOWN;
    }

    /**
     *
     *
     */
    private ICameraConnection.CameraConnectionMethod getCammeraConnectionMethodImpl()
    {
        if (connectionMethod != ICameraConnection.CameraConnectionMethod.UNKNOWN)
        {
            return (connectionMethod);
        }
        ICameraConnection.CameraConnectionMethod ret;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String connectionMethod = preferences.getString(IPreferencePropertyAccessor.CONNECTION_METHOD, "RICOH_GR2");
            if (connectionMethod.contains("RICOH_GR2"))
            {
                ret = ICameraConnection.CameraConnectionMethod.RICOH_GR2;
            }
            else // if (connectionMethod.contains("OPC"))
            {
                ret = ICameraConnection.CameraConnectionMethod.OPC;
            }
/*
            else if (connectionMethod.contains("SONY"))
            {
                ret = ICameraConnection.CameraConnectionMethod.SONY;
            }
*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = ICameraConnection.CameraConnectionMethod.UNKNOWN;
        }
        connectionMethod = ret;
        return (connectionMethod);
    }
}
