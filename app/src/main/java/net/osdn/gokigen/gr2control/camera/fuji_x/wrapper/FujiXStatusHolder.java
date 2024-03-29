package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper;

import android.util.Log;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.FragmentActivity;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.FujiXReplyMessageReceiver;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.SetPropertyValue;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXBatteryMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXCameraProperties;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXFSSControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXFilmSimulation;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXFlashMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXFocusingMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXImageAspectMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXImageFormatMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXShootingMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXTimerMode;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXWhiteBalanceMode;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class FujiXStatusHolder
{
    private final String TAG = toString();
    private final FragmentActivity activity;
    private final IFujiXCommandPublisher publisher;
    private static final boolean logcat = true;
    private SparseIntArray statusHolder;
    private SparseArrayCompat<String> statusNameArray;

    FujiXStatusHolder(@NonNull FragmentActivity activity, @NonNull IFujiXCommandPublisher publisher)
    {
        statusHolder = new SparseIntArray();
        statusHolder.clear();

        this.activity = activity;
        this.publisher = publisher;
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
        statusNameArray.append(IFujiXCameraProperties.FOCUS_LOCK, IFujiXCameraProperties.FOCUS_LOCK_STR);
        statusNameArray.append(IFujiXCameraProperties.MOVIE_REMAINING_TIME, IFujiXCameraProperties.MOVIE_REMAINING_TIME_STR);
        statusNameArray.append(IFujiXCameraProperties.SHUTTER_SPEED, IFujiXCameraProperties.SHUTTER_SPEED_STR);
        statusNameArray.append(IFujiXCameraProperties.IMAGE_ASPECT, IFujiXCameraProperties.IMAGE_ASPECT_STR);
        statusNameArray.append(IFujiXCameraProperties.BATTERY_LEVEL_2, IFujiXCameraProperties.BATTERY_LEVEL_2_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_DF00, IFujiXCameraProperties.UNKNOWN_DF00_STR);
        statusNameArray.append(IFujiXCameraProperties.PICTURE_JPEG_COUNT, IFujiXCameraProperties.PICTURE_JPEG_COUNT_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D400, IFujiXCameraProperties.UNKNOWN_D400_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D401, IFujiXCameraProperties.UNKNOWN_D401_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D52F, IFujiXCameraProperties.UNKNOWN_D52F_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_D245, IFujiXCameraProperties.UNKNOWN_D245_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_DF41, IFujiXCameraProperties.UNKNOWN_DF41_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_DF26, IFujiXCameraProperties.UNKNOWN_DF26_STR);
        statusNameArray.append(IFujiXCameraProperties.UNKNOWN_DF27, IFujiXCameraProperties.UNKNOWN_DF27_STR);
    }

    void updateValue(@Nullable ICameraStatusUpdateNotify notifier, int id, byte data0, byte data1, byte data2, byte data3)
    {
        try
        {
            int value = ((((int) data3) & 0xff) << 24) + ((((int) data2) & 0xff) << 16) + ((((int) data1) & 0xff) << 8) + (((int) data0) & 0xff);
            int currentValue = statusHolder.get(id, -1);
            //logcat(String.format(Locale.US, " STATUS [id: 0x%04x] 0x%08x(%d) -> 0x%08x(%d)", id, currentValue, currentValue, value, value));
            if (currentValue != value)
            {
                statusHolder.put(id, value);
                if (notifier != null)
                {
                    //logcat(String.format(Locale.US," STATUS UPDATE [id: 0x%04x] 0x%08x(%d) -> 0x%08x(%d)", id, currentValue, currentValue, value, value));
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
            //String idName = statusNameArray.get(id, "Unknown");
            //logcat(String.format(Locale.US, " << UPDATE STATUS >> id: 0x%04x[%s] 0x%08x(%d) -> 0x%08x(%d)", id, idName, previous, previous, current, current));
            switch (id)
            {
                case IFujiXCameraProperties.FOCUS_LOCK:
                    updateFocusedStatus(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.SHOOTING_MODE:
                    updatedTakeMode(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.BATTERY_LEVEL:
                case IFujiXCameraProperties.BATTERY_LEVEL_2:
                    updateBatteryLevel(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.FOCUS_MODE:
                    updateFocusMode(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.APERTURE:
                    updateAperture(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.SHUTTER_SPEED:
                    updateShutterSpeed(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.WHITE_BALANCE:
                    updateWhiteBalance(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.EXPOSURE_COMPENSATION:
                    updateExposureCompensation(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.ISO:
                    updateIsoSensitivity(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.DEVICE_ERROR:
                    updateDeviceError(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.SELF_TIMER:
                    updateSelfTimer(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.SDCARD_REMAIN_SIZE:
                    updateSdCardRemainSize(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.MOVIE_REMAINING_TIME:
                    updateMovieRemainTime(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.FOCUS_POINT:
                    updateFocusPoint(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.F_SS_CONTROL:
                    updateFSSControl(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.IMAGE_ASPECT:
                    updateImageAspect(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.FILM_SIMULATION:
                    updateFilmSimulation(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.FLASH:
                    updateFlashMode(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.IMAGE_FORMAT:
                    updateImageFormat(notifier, previous, current);
                    break;
                case IFujiXCameraProperties.UNKNOWN_D245:
                case IFujiXCameraProperties.UNKNOWN_DF41:
                case IFujiXCameraProperties.UNKNOWN_DF26:
                case IFujiXCameraProperties.UNKNOWN_DF27:
                default:
                    updateUnknownValue(id, previous, current);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateImageFormat(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value;
        switch (current)
        {
            case IFujiXImageFormatMode.IMAGE_FORMAT_RAW:
                value = "RAW";
                break;
            case IFujiXImageFormatMode.IMAGE_FORMAT_FINE:
                value = "FINE";
                break;
            case IFujiXImageFormatMode.IMAGE_FORMAT_NORMAL:
                value = "NORMAL";
                break;
            case IFujiXImageFormatMode.IMAGE_FORMAT_FINE_RAW:
                value = "RAW+F";
                break;
            case IFujiXImageFormatMode.IMAGE_FORMAT_NORMAL_RAW:
                value = "RAW+N";
                break;
            default:
                value = "UNKNOWN : " + current;
                break;
        }
        logcat(" Image Format : " + value);
    }

    private void updateFlashMode(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value;
        switch (current)
        {
            case IFujiXFlashMode.FLASH_AUTO:
                value = "AUTO";
                break;
            case IFujiXFlashMode.FLASH_OFF:
                value = "OFF";
                break;
            case IFujiXFlashMode.FLASH_FILL:
                value = "FILL";
                break;
            case IFujiXFlashMode.FLASH_REDEYE_AUTO:
                value = "REDEYE_AUTO";
                break;
            case IFujiXFlashMode.FLASH_REDEYE_FILL:
                value = "REDEYE_FILL";
                break;
            case IFujiXFlashMode.FLASH_EXTERNAL_SYNC:
                value = "EXTERNAL_SYNC";
                break;
            case IFujiXFlashMode.FLASH_ON:
                value = "ON";
                break;
            case IFujiXFlashMode.FLASH_REDEYE:
                value = "REDEYE";
                break;
            case IFujiXFlashMode.FLASH_REDEYE_ON:
                value = "REDEYE_ON";
                break;
            case IFujiXFlashMode.FLASH_REDEYE_SYNC:
                value = "REDEYE_SYNC";
                break;
            case IFujiXFlashMode.FLASH_REDEYE_REAR:
                value = "REDEYE_REAR";
                break;
            case IFujiXFlashMode.FLASH_SLOW_SYNC:
                value = "SLOW_SYNC";
                break;
            case IFujiXFlashMode.FLASH_REAR_SYNC:
                value = "REAR_SYNC";
                break;
            case IFujiXFlashMode.FLASH_COMMANDER:
                value = "COMMANDER";
                break;
            case IFujiXFlashMode.FLASH_DISABLE:
                value = "DISABLE";
                break;
            case IFujiXFlashMode.FLASH_ENABLE:
                value = "ENABLE";
                break;
            default:
                value = " UNKNOWN : " + current;
                break;
        }
        logcat(" FLASH MODE : " + value);
    }

    private void updateFilmSimulation(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value;
        switch (current)
        {
            case IFujiXFilmSimulation.FILM_SIMULATION_PROVIA:
                value = IFujiXFilmSimulation.FILM_SIMULATION_PROVIA_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_VELVIA:
                value = IFujiXFilmSimulation.FILM_SIMULATION_VELVIA_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ASTIA:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ASTIA_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME:
                value = IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_SEPIA:
                value = IFujiXFilmSimulation.FILM_SIMULATION_SEPIA_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_HI:
                value = IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_HI_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_STD:
                value = IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_STD_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_Y_FILTER:
                value = IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_Y_FILTER_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_R_FILTER:
                value = IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_R_FILTER_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_G_FILTER:
                value = IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_G_FILTER_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_CHROME:
                value = IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_CHROME_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ACROS:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ACROS_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ACROS_Y:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ACROS_Y_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ACROS_R:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ACROS_R_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ACROS_G:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ACROS_G_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ETERNA:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ETERNA_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_NEGATIVE:
                value = IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_NEGATIVE_STR;
                break;
            case IFujiXFilmSimulation.FILM_SIMULATION_ETERNA_BLEACH_BYPASS:
                value = IFujiXFilmSimulation.FILM_SIMULATION_ETERNA_BLEACH_BYPASS_STR;
                break;
            default:
                value = "??? " + current;
                break;
        }
        logcat(" FILM SIMULATION : " + value);
    }

    private void updateImageAspect(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value;
        switch (current)
        {
            case IFujiXImageAspectMode.IMAGE_ASPECT_S_3x2:
              value = "S:3x2";
              break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_S_16x9:
              value = "S:16x9";
              break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_S_1x1:
              value = "S:1x1";
              break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_M_3x2:
                value = "M:3x2";
                break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_M_16x9:
                value = "M:16x9";
                break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_M_1x1:
                value = "M:1x1";
                break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_L_3x2:
                value = "L:3x2";
                break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_L_16x9:
                value = "L:16x9";
                break;
            case IFujiXImageAspectMode.IMAGE_ASPECT_L_1x1:
                value = "L:1x1";
                break;
            default:
                value = "? " + current;
                break;
        }
        logcat(" Image Aspect : " + value);
    }


    private void updateFSSControl(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value;
        switch (current)
        {
            case IFujiXFSSControl.F_SS_CTRL_BOTH:
                value = "BOTH";
                break;
            case IFujiXFSSControl.F_SS_CTRL_F:
                value = "F";
                break;
            case IFujiXFSSControl.F_SS_CTRL_SS:
                value = "SS";
                break;
            case IFujiXFSSControl.F_SS_CTRL_NONE:
                value = "NONE";
                break;
            default:
                value = "? " + current;
                break;
        }
        logcat(" F_SS : " + value);
    }

    private void updateUnknownValue(int id, int previous, int current)
    {
        logcat(String.format(Locale.US,"<< UPDATE UNKNOWN STATUS >> id: 0x%04x 0x%08x(%d) -> 0x%08x(%d)", id, previous, previous, current, current));
    }

    private void updateFocusPoint(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        logcat(String.format(Locale.ENGLISH, " Focus Point : %x ", current));
    }

    private void updateMovieRemainTime(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        logcat(" MOVIE REMAIN : " + current);
    }

    private void updateSdCardRemainSize(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        logcat(" SDCARD REMAIN : " + current);
    }

    private void updateSelfTimer(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        int value = -1;
        try
        {
            switch (current)
            {
                case IFujiXTimerMode.TIMER_OFF:
                    value = 0;
                    break;

                case IFujiXTimerMode.TIMER_1SEC:
                    value = 1;
                    break;

                case IFujiXTimerMode.TIMER_2SEC:
                    value = 2;
                    break;

                case IFujiXTimerMode.TIMER_5SEC:
                    value = 5;
                    break;

                case IFujiXTimerMode.TIMER_10SEC:
                    value = 10;
                    break;

                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (value <= 0)
        {
            logcat(" SELF TIMER IS OFF (" + value + ")");
        }
        else
        {
            logcat(" SELF TIMER IS " + value + " sec.");
        }
    }

    private void updateDeviceError(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        if (current != 0)
        {
            notifier.updateWarning("ERROR " + current);
        }
    }

    private void updateIsoSensitivity(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String iso = "";
        try
        {
            int isoValue = (0x0000ffff & current);
            if ((0xffff0000 & current) != 0)
            {
                iso = "AUTO_" + isoValue;
            }
            else if (isoValue > 12800)
            {
                iso = "H" + isoValue;

            }
            else if (isoValue < 200)
            {
                iso = "L" + isoValue;
            }
            else
            {
                iso = "" + isoValue;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        logcat(" ISO SENSITIVITY : " + iso);
        notifier.updateIsoSensitivity(iso);
    }

    private void updateExposureCompensation(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String exposureCompensation = "";
        try
        {
            float value = ((float) current / 1000.0f);
            exposureCompensation = String.format(Locale.ENGLISH, "%+1.1f", value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifier.updatedExposureCompensation(exposureCompensation);
    }

    private void updateWhiteBalance(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String value = "";
        try
        {
            switch (current)
            {
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_AUTO:
                    value = "Auto";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_FINE:
                    value = "Fine";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_INCANDESCENT:
                    value = "Incandescent";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_FLUORESCENT_1:
                    value = "Fluorescent 1";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_FLUORESCENT_2:
                    value = "Fluorescent 2";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_FLUORESCENT_3:
                    value = "Fluorescent 3";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_SHADE:
                    value = "Shade";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_UNDERWATER:
                    value = "Underwater";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_TEMPERATURE:
                    value = "Kelvin";
                    break;
                case IFujiXWhiteBalanceMode.WHITE_BALANCE_CUSTOM:
                    value = "Custom";
                    break;
                default:
                    value = "Unknown";
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifier.updatedWBMode(value);
    }

    private void updateShutterSpeed(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String shutterSpeed = "--";
        try
        {
            if ((0x80000000 & current) != 0)
            {
                int value = 0x0fffffff & current;
                shutterSpeed = ("1/" + (value / 1000));
            }
            else
            {
                shutterSpeed = (current + "");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifier.updatedShutterSpeed(shutterSpeed);
    }

    private void updateAperture(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String aperature = "---";
        try
        {
            float value = ((float) current / 100.0f);
            aperature = String.format(Locale.ENGLISH, "%1.1f", value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifier.updatedAperture(aperature);
    }

    private void updateFocusMode(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String mode = "";

        // Focus Mode
        if (current == IFujiXFocusingMode.FOCUS_MANUAL)
        {
            mode = "MF";
        }
        else if (current == IFujiXFocusingMode.FOCUS_CONTINUOUS_AUTO)
        {
            mode = "AF-C";
        }
        else if (current == IFujiXFocusingMode.FOCUS_SINGLE_AUTO)
        {
            mode = "AF-S";
        }
        else
        {
            mode = mode + current;
        }
        logcat(" Focus Mode : " + mode);
    }

    private void updateFocusedStatus(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
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

    private void updatedTakeMode(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        String mode = "";
        switch (current)
        {
            case IFujiXShootingMode.SHOOTING_MANUAL:
                mode = "M";
                break;
            case IFujiXShootingMode.SHOOTING_PROGRAM:
                mode = "P";
                break;
            case IFujiXShootingMode.SHOOTING_APERTURE:
                mode = "A";
                break;
            case IFujiXShootingMode.SHOOTING_SHUTTER:
                mode = "S";
                break;
            case IFujiXShootingMode.SHOOTING_CUSTOM:
                mode = "C";
                break;
            case IFujiXShootingMode.SHOOTING_AUTO:
                mode = "a";
                break;
            default:
                mode = mode + current;
                break;
        }
        notifier.updatedTakeMode(mode);
    }

    private void updateBatteryLevel(@NonNull ICameraStatusUpdateNotify notifier, int previous, int current)
    {
        int level;
        if ((current == IFujiXBatteryMode.BATTERY_CRITICAL) || (current == IFujiXBatteryMode.BATTERY_126S_CRITICAL))
        {
            level = 0;
        }
        else if (current == IFujiXBatteryMode.BATTERY_126S_ONE_BAR)
        {
            level = 20;
        }
        else if ((current == IFujiXBatteryMode.BATTERY_ONE_BAR) || (current == IFujiXBatteryMode.BATTERY_126S_TWO_BAR))
        {
            level = 40;
        }
        else if (current == IFujiXBatteryMode.BATTERY_126S_THREE_BAR)
        {
            level = 60;
        }
        else if ((current == IFujiXBatteryMode.BATTERY_TWO_BAR) || (current == IFujiXBatteryMode.BATTERY_126S_FOUR_BAR))
        {
            level = 80;
        }
        else // if ((current == IFujiXBatteryMode.BATTERY_FULL) || (current == IFujiXBatteryMode.BATTERY_126S_FULL))
        {
            level = 100;
        }
        notifier.updateRemainBattery(level);
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
        if ((listKey == null)||(listKey.isEmpty()))
        {
            // アイテム名の一覧を応答する
            return (getAvailableStatusNameList());
        }
        if (listKey.matches(ICameraStatus.EFFECT))
        {
            return (getAvailableEffectItemList());
        }
        if (listKey.matches(ICameraStatus.AE))
        {
            return (new ArrayList<>());
        }
        if (listKey.matches(ICameraStatus.WHITE_BALANCE))
        {
            return (getAvailableResourceArrayItemList(R.array.fuji_x_white_balance));
        }
        if (listKey.matches(ICameraStatus.ISO_SENSITIVITY))
        {
            return (getAvailableResourceArrayItemList(R.array.fuji_x_iso_sensitivity));
        }
        if (listKey.matches(ICameraStatus.FLASH_XV))
        {
            return (getAvailableResourceArrayItemList(R.array.fuji_x_flash_control));
        }
        if (listKey.matches(ICameraStatus.SELF_TIMER))
        {
            return (getAvailableResourceArrayItemList(R.array.fuji_x_timer_control));
        }

        /////  選択可能なステータスの一覧を取得する : でも以下はアイテム名の一覧... /////
        /*
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
        */
        return (new ArrayList<>());
    }

    private List<String> getAvailableEffectItemList()
    {
        // EFFECTの選択肢をリストにして返す  : Film Simulation にした。
        ArrayList<String> selection = new ArrayList<>();
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_PROVIA_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_VELVIA_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ASTIA_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_SEPIA_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_HI_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_PRO_NEG_STD_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_Y_FILTER_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_R_FILTER_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_MONOCHROME_G_FILTER_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_CHROME_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ACROS_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ACROS_Y_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ACROS_R_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ACROS_G_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ETERNA_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_CLASSIC_NEGATIVE_STR);
        selection.add(IFujiXFilmSimulation.FILM_SIMULATION_ETERNA_BLEACH_BYPASS_STR);
        return(selection);
    }

    private List<String> getAvailableResourceArrayItemList(int id)
    {
        // リソースのの選択肢をリストにして返す
        ArrayList<String> selection = new ArrayList<>();

        String[] items = activity.getResources().getStringArray(id);
        for (String item : items)
        {
            Log.v(TAG, " SELECTION : " + item);
            selection.add(item);
        }
        return (selection);
    }

    private String getCurrentEffectStatus()
    {
        // EFFECTの現在状態を応答する : FILM Simulationにした
        try
        {
            int value = statusHolder.get(IFujiXCameraProperties.FILM_SIMULATION);
            List<String> itemList = getAvailableEffectItemList();
            if ((value >= IFujiXFilmSimulation.FILM_SIMULATION_MIN)&&(value < itemList.size()))
            {
                return (itemList.get(value - 1));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("");
    }

    private String getCurrentWhiteBalanceStatus()
    {
        // White Balanceの現在状態を取得する
        try
        {
            int value = statusHolder.get(IFujiXCameraProperties.WHITE_BALANCE);
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_white_balance);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_white_balance_value);
            int index = 0;
            for (String itemValue : itemValues)
            {
                int itemValueInt = Integer.parseInt(itemValue);
                if (itemValueInt == value)
                {
                    return (items[index]);
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("");
    }

    private String getCurrentIsoSensitivityStatus()
    {
        String iso = "";
        try
        {
            int current = statusHolder.get(IFujiXCameraProperties.ISO);
            int isoValue = (0x0000ffff & current);
            if ((0xffff0000 & current) != 0)
            {
                iso = "AUTO_" + isoValue;
            }
            else if (isoValue > 12800)
            {
                iso = "H" + isoValue;

            }
            else if (isoValue < 200)
            {
                iso = "L" + isoValue;
            }
            else
            {
                iso = "" + isoValue;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        logcat(" ISO SENSITIVITY : " + iso);
        return (iso);
    }

    private String getCurrentFlashStatus()
    {
        String flash = "";
        try
        {
            int current = statusHolder.get(IFujiXCameraProperties.FLASH);
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_flash_control);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_flash_control_value);
            int index = 0;
            for (String itemValue : itemValues)
            {
                int itemValueInt = Integer.parseInt(itemValue, 16);
                if (itemValueInt == current)
                {
                    return (items[index]);
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (flash);
    }

    private String getCurrentSelfTimerStatus()
    {
        String selfTimer = "";
        try
        {
            int current = statusHolder.get(IFujiXCameraProperties.SELF_TIMER);
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_timer_control);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_timer_control_value);
            int index = 0;
            for (String itemValue : itemValues)
            {
                int itemValueInt = Integer.parseInt(itemValue, 16);
                if (itemValueInt == current)
                {
                    return (items[index]);
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (selfTimer);
    }

    private void setEffectItem(String value)
    {
        try
        {
            int index = 1;
            List<String> itemList = getAvailableEffectItemList();
            for (String item : itemList)
            {
                //Log.v(TAG, " " + item + " vs " + value);
                if (item.equals(value))
                {
                    //  見つかった！ この値に設定する (Film Simulation)
                    //Log.v(TAG, " SET FILM SIMULATION : " + index);
                    publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyMessageReceiver(" Set Film Simulation", false), IFujiXCameraProperties.FILM_SIMULATION, 2, index));
                    return;
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setCurrentWhiteBalanceStatus(String value)
    {
        // White Balanceを設定する
        try
        {
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_white_balance);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_white_balance_value);
            int index = 0;
            for (String item : items)
            {
                if (item.matches(value))
                {
                    // 見つかった！ この値を設定する
                    String itemValue = itemValues[index];
                    int itemValueInt = Integer.parseInt(itemValue);
                    publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyMessageReceiver(" Set White Balance", true), IFujiXCameraProperties.WHITE_BALANCE, 4, itemValueInt));
                    return;
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setCurrentIsoSensitivityStatus(String value)
    {
        // ISO感度を設定する
        try
        {
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_iso_sensitivity);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_iso_sensitivity_value);
            int index = 0;
            for (String item : items)
            {
                //Log.v(TAG, " ===== ISO : " + value + " " + item + " ===== ");
                if (item.matches(value))
                {
                    // 見つかった！ この値を設定する
                    String itemValue = itemValues[index];
                    int itemValueInt = (int) Long.parseLong(itemValue,16);
                    logcat(" SET ISO SENSITIVITY : " + value + " " + itemValueInt);
                    publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyMessageReceiver(" Set ISO", true), IFujiXCameraProperties.ISO, 4, itemValueInt));
                    return;
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setCurrentFlashStatus(String value)
    {
        // ISO感度を設定する
        try
        {
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_flash_control);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_flash_control_value);
            int index = 0;
            for (String item : items)
            {
                Log.v(TAG, " ===== FLASH : " + value + " " + item + " ===== ");
                if (item.matches(value))
                {
                    // 見つかった！ この値を設定する
                    String itemValue = itemValues[index];
                    int itemValueInt = (int) Long.parseLong(itemValue,16);
                    logcat(" SET FLASH : " + value + " " + itemValueInt);
                    publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyMessageReceiver(" Set FLASH", true), IFujiXCameraProperties.FLASH, 4, itemValueInt));
                    return;
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setSelfTimerStatus(String value)
    {
        // セルフタイマーを設定する
        try
        {
            String[] items = activity.getResources().getStringArray(R.array.fuji_x_timer_control);
            String[] itemValues = activity.getResources().getStringArray(R.array.fuji_x_timer_control_value);
            int index = 0;
            for (String item : items)
            {
                Log.v(TAG, " ===== SELF-TIMER : " + value + " " + item + " ===== ");
                if (item.matches(value))
                {
                    // 見つかった！ この値を設定する
                    String itemValue = itemValues[index];
                    int itemValueInt = (int) Long.parseLong(itemValue,16);
                    logcat(" SET SELF-TIMER : " + value + " " + itemValueInt);
                    publisher.enqueueCommand(new SetPropertyValue(new FujiXReplyMessageReceiver(" Set SELF-TIMER", true), IFujiXCameraProperties.SELF_TIMER, 4, itemValueInt));
                    return;
                }
                index++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    String getItemStatus(String key)
    {
        if (key.matches(ICameraStatus.EFFECT))
        {
            return (getCurrentEffectStatus());
        }
        if (key.matches(ICameraStatus.WHITE_BALANCE))
        {
            return (getCurrentWhiteBalanceStatus());
        }
        if (key.matches(ICameraStatus.ISO_SENSITIVITY))
        {
            return (getCurrentIsoSensitivityStatus());
        }
        if (key.matches(ICameraStatus.FLASH_XV))
        {
            return (getCurrentFlashStatus());
        }
        if (key.matches(ICameraStatus.SELF_TIMER))
        {
            return (getCurrentSelfTimerStatus());
        }
        if (key.matches(ICameraStatus.TAKE_MODE))
        {
            return ("");
        }
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
                    logcat("getItemStatus() value : " + value + " key : " + key + " [" + id + "]");
                    // dumpStatus();
                    return (value + "");
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
                    return (value + "");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("? [" + key + "]");
    }

    void setItemStatus(@NonNull String key, @NonNull String value)
    {
        try
        {
            logcat(" setStatus(" + key + ", " + value + ")");
            if (key.matches(ICameraStatus.EFFECT))
            {
                setEffectItem(value);
            }
            else if (key.matches(ICameraStatus.WHITE_BALANCE))
            {
                setCurrentWhiteBalanceStatus(value);
            }
            else if (key.matches(ICameraStatus.ISO_SENSITIVITY))
            {
                setCurrentIsoSensitivityStatus(value);
            }
            else if (key.matches(ICameraStatus.FLASH_XV))
            {
                setCurrentFlashStatus(value);
            }
            else if (key.matches(ICameraStatus.SELF_TIMER))
            {
                setSelfTimerStatus(value);
            }
            // その他の設定...
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void dumpStatus()
    {
        try
        {
            Log.v(TAG, " - - - status - - - ");
            for (int index = 0; index < statusHolder.size(); index++)
            {
                int key = statusHolder.keyAt(index);
                int value = statusHolder.get(key);
                Log.v(TAG, String.format("id : 0x%x value : %d (0x%x) ", key, value, value));
            }
            Log.v(TAG, " - - - status - - - ");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void logcat(String message)
    {
        if (logcat)
        {
            Log.v(TAG, message);
        }
    }
}
