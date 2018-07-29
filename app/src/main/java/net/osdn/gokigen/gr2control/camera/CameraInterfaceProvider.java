package net.osdn.gokigen.gr2control.camera;

import android.app.Activity;
import android.support.annotation.NonNull;
import net.osdn.gokigen.gr2control.camera.ricohgr2.IRicohGr2InterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper.RicohGr2InterfaceProvider;

/**
 *
 *
 */
public class CameraInterfaceProvider implements IInterfaceProvider
{
    //private final Activity context;
    //private final OlympusInterfaceProvider olympus;
    //private final SonyCameraWrapper sony;
    private final RicohGr2InterfaceProvider ricohGr2;

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
        //this.context = context;
        //olympus = new OlympusInterfaceProvider(context, provider);
        //sony = new SonyCameraWrapper(context, provider);
        ricohGr2 = new RicohGr2InterfaceProvider(context, provider);
    }

/*
    @Override
    public IOlympusInterfaceProvider getOlympusInterface()
    {
        return (olympus);
    }

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

    /**
     *
     *
     */
    @Override
    public IRicohGr2InterfaceProvider getRicohGr2Infterface()
    {
        return (ricohGr2);
    }

    /**
     *   OPCカメラを使用するかどうか  ... 今回はGR2専用
     *
     * @return OPC / SONY / RICOH_GR2  (ICameraConnection.CameraConnectionMethod)
     */
    public ICameraConnection.CameraConnectionMethod getCammeraConnectionMethod()
    {
        return (ICameraConnection.CameraConnectionMethod.RICOH_GR2);
/*
        ICameraConnection.CameraConnectionMethod ret = ICameraConnection.CameraConnectionMethod.OPC;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String connectionMethod = preferences.getString(IPreferencePropertyAccessor.CONNECTION_METHOD, "OPC");
            if (connectionMethod.contains("SONY"))
            {
                ret = ICameraConnection.CameraConnectionMethod.SONY;
            }
            else if (connectionMethod.contains("RICOH_GR2"))
            {
                ret = ICameraConnection.CameraConnectionMethod.RICOH_GR2;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
*/
    }
}
