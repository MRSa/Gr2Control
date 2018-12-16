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
    private boolean focused = false;
    private boolean focusLocked = false;
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

    private String getStatusString(JSONObject obj, String name)
    {
        try
        {
            return (obj.getString(name));
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        return ("");
    }

    private boolean getBooleanStatus(JSONObject obj, String name)
    {
        try {
            return (obj.getBoolean(name));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return (false);
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
            String result = getStatusString(latestResultObject,"errMsg");
            String av = getStatusString(latestResultObject,"av");
            String tv = getStatusString(latestResultObject,"tv");
            String xv = getStatusString(latestResultObject,"xv");
            String exposureMode = getStatusString(latestResultObject,"exposureMode");
            String meteringMode = getStatusString(latestResultObject,"meteringMode");
            String wbMode = getStatusString(latestResultObject,"WBMode");
            String battery = getStatusString(latestResultObject,"battery");
            boolean focus = getBooleanStatus(latestResultObject,"focused");
            boolean focusLock = getBooleanStatus(latestResultObject,"focusLocked");

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
                if ((focus != focused)||(focusLock != focusLocked))
                {
                    focused = focus;
                    focusLocked = focusLock;
                    notifier.updateFocusedStatus(focused, focusLocked);
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
