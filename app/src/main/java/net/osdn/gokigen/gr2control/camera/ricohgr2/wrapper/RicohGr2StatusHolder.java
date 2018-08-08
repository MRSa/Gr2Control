package net.osdn.gokigen.gr2control.camera.ricohgr2.wrapper;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
class RicohGr2StatusHolder
{
    private final String TAG = toString();
    private final ICameraStatusUpdateNotify notifier;

    private JSONObject latestResultObject = null;
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
    List<String> getAvailableItemList(@NonNull String key)
    {
        List<String> itemList = new ArrayList<>();
        try
        {
            JSONArray array = latestResultObject.getJSONArray(key);
            if (array == null)
            {
                return (itemList);
            }
            int nofItems = array.length();
            for (int index = 0; index < nofItems; index++)
            {
                try
                {
                    itemList.add(array.getString(index));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (itemList);
    }

    String getItemStatus(@NonNull String key)
    {
        try
        {
            return (latestResultObject.getString(key));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("");
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
            latestResultObject = new JSONObject(replyString);
            String result = latestResultObject.getString("errMsg");
            String av = latestResultObject.getString("av");
            String tv = latestResultObject.getString("tv");
            String xv = latestResultObject.getString("xv");
            String exposureMode = latestResultObject.getString("exposureMode");
            String meteringMode = latestResultObject.getString("meteringMode");
            String wbMode = latestResultObject.getString("WBMode");
            String battery = latestResultObject.getString("battery");

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
            System.gc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
