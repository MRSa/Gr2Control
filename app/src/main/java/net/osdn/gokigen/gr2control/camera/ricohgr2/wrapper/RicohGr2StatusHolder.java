package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.util.Log;

import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import org.json.JSONObject;


/**
 *
 *
 */
class RicohGr2StatusHolder
{
    private final String TAG = toString();
    private final ICameraStatusUpdateNotify notifier;

    private String avStatus = "";
    private String tvStatus = "";
    private String xvStatus = "";
    private String exposureModeStatus = "";
    private String meteringModeStatus = "";
    private String wbModeStatus = "";
    private String batteryStatus = "";

    /**
     *
     *
     */
    RicohGr2StatusHolder(ICameraStatusUpdateNotify notifier)
    {
        this.notifier = notifier;
    }

    /**
     *
     *
     */
    void updateStatus(String replyString)
    {
        if ((replyString == null)||(replyString.length() < 1))
        {
            Log.v(TAG, "httpGet() reply is null. ");
            return;
        }

        try
        {
            JSONObject resultObject = new JSONObject(replyString);
            String result = resultObject.getString("errMsg");
            String av = resultObject.getString("av");
            String tv = resultObject.getString("tv");
            String xv = resultObject.getString("xv");
            String exposureMode = resultObject.getString("exposureMode");
            String meteringMode = resultObject.getString("meteringMode");
            String wbMode = resultObject.getString("WBMode");
            String battery = resultObject.getString("battery");

            if (result.contains("OK"))
            {
                if (!avStatus.equals(av))
                {
                    avStatus = av;
                    notifier.updatedAperture(avStatus);
                }
                if (!tvStatus.equals(tv))
                {
                    tvStatus = tv;
                    notifier.updatedShutterSpeed(tvStatus);
                }
                if (!xvStatus.equals(xv))
                {
                    xvStatus = xv;
                    notifier.updatedExposureCompensation(xvStatus);
                }
                if (!exposureModeStatus.equals(exposureMode))
                {
                    exposureModeStatus = exposureMode;
                    notifier.updatedTakeMode(exposureModeStatus);
                }
                if (!meteringModeStatus.equals(meteringMode))
                {
                    meteringModeStatus = meteringMode;
                    notifier.updatedMeteringMode(meteringModeStatus);
                }
                if (!wbModeStatus.equals(wbMode))
                {
                    wbModeStatus = wbMode;
                    notifier.updatedWBMode(wbModeStatus);
                }
                if (!batteryStatus.equals(battery))
                {
                    batteryStatus = battery;
                    notifier.updateRemainBattery(Integer.parseInt(batteryStatus));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
