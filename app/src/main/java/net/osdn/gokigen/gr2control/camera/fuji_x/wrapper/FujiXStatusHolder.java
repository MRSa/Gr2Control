package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import android.util.Log;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;

import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXCameraProperties;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXCameraProperties.FOCUS_LOCK;

class FujiXStatusHolder
{
    private final String TAG = toString();
    private SparseIntArray statusHolder;
    private SparseArrayCompat<String> statusNameArray;

    FujiXStatusHolder()
    {
        statusHolder = new SparseIntArray();
        statusHolder.clear();

        statusNameArray = new SparseArrayCompat<>();
        prepareStatusNameArray();
    }

    private void prepareStatusNameArray()
    {
        statusNameArray.clear();
        statusNameArray.append(IFujiXCameraProperties.BATTERY_LEVEL, IFujiXCameraProperties.BATTERY_LEVEL_STR);
        statusNameArray.append(IFujiXCameraProperties.WHITE_BALANCE, IFujiXCameraProperties.WHITE_BALANCE_STR);
        statusNameArray.append(IFujiXCameraProperties.APERTURE, IFujiXCameraProperties.APERTURE_STR);
        statusNameArray.append(IFujiXCameraProperties.FOCUS_MODE, IFujiXCameraProperties.FOCUS_MODE_STR);
        statusNameArray.append(IFujiXCameraProperties.SHOOTING_MODE, IFujiXCameraProperties.SHOOTING_MODE_STR);
        statusNameArray.append(IFujiXCameraProperties.FLASH, IFujiXCameraProperties.FLASH_STR);
        statusNameArray.append(IFujiXCameraProperties.EXPOSURE_COMPENSATION, IFujiXCameraProperties.EXPOSURE_COMPENSATION_STR);
        statusNameArray.append(IFujiXCameraProperties.SELF_TIMER, IFujiXCameraProperties.SELF_TIMER_STR);
        statusNameArray.append(IFujiXCameraProperties.FILM_SIMULATION, IFujiXCameraProperties.FILM_SIMULATION_STR);
        statusNameArray.append(IFujiXCameraProperties.IMAGE_FORMAT, IFujiXCameraProperties.IMAGE_FORMAT_STR);
        statusNameArray.append(IFujiXCameraProperties.RECMODE_ENABLE, IFujiXCameraProperties.RECMODE_ENABLE_STR);
        statusNameArray.append(IFujiXCameraProperties.F_SS_CONTROL, IFujiXCameraProperties.F_SS_CONTROL_STR);
        statusNameArray.append(IFujiXCameraProperties.ISO, IFujiXCameraProperties.ISO_STR);
        statusNameArray.append(IFujiXCameraProperties.MOVIE_ISO, IFujiXCameraProperties.MOVIE_ISO_STR);
        statusNameArray.append(IFujiXCameraProperties.FOCUS_POINT, IFujiXCameraProperties.FOCUS_POINT_STR);
        statusNameArray.append(IFujiXCameraProperties.DEVICE_ERROR, IFujiXCameraProperties.DEVICE_ERROR_STR);
        statusNameArray.append(IFujiXCameraProperties.IMAGE_FILE_COUNT, IFujiXCameraProperties.IMAGE_FILE_COUNT_STR);
        statusNameArray.append(IFujiXCameraProperties.SDCARD_REMAIN_SIZE, IFujiXCameraProperties.SDCARD_REMAIN_SIZE_STR);
        statusNameArray.append(FOCUS_LOCK, IFujiXCameraProperties.FOCUS_LOCK_STR);
        statusNameArray.append(IFujiXCameraProperties.MOVIE_REMAINING_TIME, IFujiXCameraProperties.MOVIE_REMAINING_TIME_STR);
        statusNameArray.append(IFujiXCameraProperties.SHUTTER_SPEED, IFujiXCameraProperties.SHUTTER_SPEED_STR);
        statusNameArray.append(IFujiXCameraProperties.IMAGE_ASPECT,IFujiXCameraProperties.IMAGE_ASPECT_STR);
        statusNameArray.append(IFujiXCameraProperties.BATTERY_LEVEL_2, IFujiXCameraProperties.BATTERY_LEVEL_2_STR);

        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_DF00, IFujiXCameraProperties.UNKNOWN_DF00_STR);
        statusNameArray.append(IFujiXCameraProperties.PICTURE_JPEG_COUNT, IFujiXCameraProperties.PICTURE_JPEG_COUNT_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D400, IFujiXCameraProperties.UNKNOWN_D400_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D401, IFujiXCameraProperties.UNKNOWN_D401_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D52F, IFujiXCameraProperties.UNKNOWN_D52F_STR);


    }


    void updateValue(ICameraStatusUpdateNotify notifier, int id, byte data0, byte data1, byte data2, byte data3)
    {
        try
        {
            int value = ((((int) data3) & 0xff) << 24) + ((((int) data2) & 0xff) << 16) + ((((int) data1) & 0xff) << 8) + (((int) data0) & 0xff);
            int currentValue = statusHolder.get(id, -1);
            //Log.v(TAG, "STATUS  ID: " + id + "  value : " + value + " (" + currentValue + ")");
            statusHolder.put(id, value);
            if (currentValue != value)
            {
                //Log.v(TAG, "STATUS  ID: " + id + " value : " + currentValue + " -> " + value);
                if (notifier != null)
                {
                    updateDetected(notifier, id, currentValue, value);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateDetected(@NonNull ICameraStatusUpdateNotify notifier, int id, int previous, int current)
    {
        try
        {
            String idName = statusNameArray.get(id, "Unknown");
            Log.v(TAG, String.format(Locale.US,"<< UPDATE STATUS >> id: 0x%04x[%s] 0x%08x(%d) -> 0x%08x(%d)", id, idName, previous, previous, current, current));
            //Log.v(TAG, "updateDetected(ID: " + id + " [" + idName + "] " + previous + " -> " + current + " )");

            if (id == FOCUS_LOCK)
            {
                if (current == 1)
                {
                    // focus Lock
                    notifier.updateFocusedStatus(true, true);
                }
                else
                {
                    // focus unlock
                    notifier.updateFocusedStatus(false, false);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   認識したカメラのステータス名称のリストを応答する
     *
     */
    private List<String> getAvailableStatusNameList()
    {
        ArrayList<String> selection = new ArrayList<>();
        try
        {
            for (int index = 0; index < statusHolder.size(); index++)
            {
                int key = statusHolder.keyAt(index);
                selection.add(statusNameArray.get(key, String.format(Locale.US, "0x%04x", key)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (selection);

    }

    List<String> getAvailableItemList(String listKey)
    {
        if (listKey == null)
        {
            // アイテム名の一覧を応答する
            return (getAvailableStatusNameList());
        }

        /////  選択可能なステータスの一覧を取得する : でも以下はアイテム名の一覧... /////
        ArrayList<String> selection = new ArrayList<>();
        try
        {
            for (int index = 0; index < statusHolder.size(); index++)
            {
                int key = statusHolder.keyAt(index);
                selection.add(statusNameArray.get(key));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (selection);
    }

    String getItemStatus(String key)
    {
        try
        {
            int strIndex = key.indexOf("x");
            Log.v(TAG, "getItemStatus() : " + key + " [" + strIndex + "]");
            if (strIndex >= 1)
            {
                key = key.substring(strIndex + 1);
                try
                {
                    int id = Integer.parseInt(key, 16);
                    int value = statusHolder.get(id);
                    return (String.format(Locale.US,"0x%08x (%d)", value, value));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            for (int index = 0; index < statusNameArray.size(); index++)
            {
                int id = statusNameArray.keyAt(index);
                String strKey = statusNameArray.valueAt(index);
                if (key.contentEquals(strKey))
                {
                    int value = statusHolder.get(id);
                    return (String.format(Locale.US,"0x%08x (%d)", value, value));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("? [" + key + "]");
    }

}
