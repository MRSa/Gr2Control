package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

/**
 *
 *
 */
public class OlyCameraStatusWrapper implements ICameraStatus, ICameraStatusWatcher, OLYCameraStatusListener, OLYCameraPropertyListener
{
    private final String TAG = toString();
    private final OLYCamera camera;
    private ICameraStatusUpdateNotify updateReceiver = null;

    private static final String CAMERA_STATUS_APERTURE_VALUE = "ActualApertureValue";
    private static final String CAMERA_STATUS_SHUTTER_SPEED = "ActualShutterSpeed";
    private static final String CAMERA_STATUS_EXPOSURE_COMPENSATION = "ActualExposureCompensation";
    private static final String CAMERA_STATUS_ISO_SENSITIVITY = "ActualIsoSensitivity";
    private static final String CAMERA_STATUS_RECORDABLEIMAGES = "RemainingRecordableImages";
    private static final String CAMERA_STATUS_MEDIA_BUSY = "MediaBusy";
    private static final String CAMERA_STATUS_MEDIA_ERROR = "MediaError";
    private static final String CAMERA_STATUS_ACTUAL_ISO_SENSITIITY_WARNING = "ActualIsoSensitivityWarning";
    private static final String CAMERA_STATUS_EXPOSURE_WARNING = "ExposureWarning";
    private static final String CAMERA_STATUS_EXPOSURE_METERING_WARNING = "ExposureMeteringWarning";
    private static final String CAMERA_STATUS_HIGH_TEMPERATURE_WARNING = "HighTemperatureWarning";
    private static final String CAMERA_STATUS_DETECT_FACES = "DetectedHumanFaces";
    private static final String CAMERA_STATUS_FOCAL_LENGTH = "ActualFocalLength";
    private static final String CAMERA_STATUS_LEVEL_GAUGE = "LevelGauge";

    private String currentTakeMode = "";
    private String currentMeteringMode = "";
    private String currentWBMode = "";
    private String currentRemainBattery = "";
    private String currentShutterSpeed = "";
    private String currentAperture = "";
    private String currentExposureCompensation = "";

    OlyCameraStatusWrapper(OLYCamera camera)
    {
        this.camera = camera;
    }


    private String convertToOpcKey(@NonNull String key)
    {
        String opcKey = "";
        switch (key)
        {
            case EFFECT:
                opcKey = "COLORTONE";
                break;
            case TAKE_MODE:
                opcKey = "TAKEMODE";
                break;
            case APERATURE:
                opcKey = "APERTURE";
                break;
            case SHUTTER_SPEED:
                opcKey = "SHUTTER";
                break;
            case ISO_SENSITIVITY:
                opcKey = "ISO";
                break;
            case EXPREV:
                opcKey = "EXPREV";
                break;
            case WHITE_BALANCE:
                opcKey = "WB";
                break;
            case AE:
                opcKey = "AE";
                break;
            case IMAGESIZE:
                opcKey = "IMAGESIZE";
                break;
            case MOVIESIZE:
                opcKey = "QUALITY_MOVIE";
                break;
            case DRIVE_MODE:
                opcKey = "TAKE_DRIVE";
                break;
            case FOCUS_MODE:
                opcKey = "TAKE_DRIVE";
                break;
            case AF_MODE:
                opcKey = "FOCUS_STILL";
                break;
            case FLASH_XV:
                break;
        }
        return (opcKey);
    }

    @Override
    public @NonNull List<String> getStatusList(@NonNull String key)
    {
        List<String> array = new ArrayList<>();
        String opcKey = convertToOpcKey(key);
        if (opcKey.length() < 1)
        {
            return (array);
        }
        try
        {
            List<String> values = camera.getCameraPropertyValueList(opcKey);
            for (String value : values)
            {
                array.add(camera.getCameraPropertyValueTitle(value));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (array);
    }

    @Override
    public String getStatus(@NonNull String key)
    {
        String opcKey = convertToOpcKey(key);
        if (opcKey.length() < 1)
        {
            return ("");
        }
        try
        {
            return (camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(opcKey)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("");
    }

    @Override
    public void setStatus(@NonNull String key, @NonNull String value)
    {
        Log.v(TAG, "setStatus : " + key + " " + value);
        try
        {
            String opcKey = convertToOpcKey(key);
            if (opcKey.length() < 1)
            {
                return;
            }
            List<String> values = camera.getCameraPropertyValueList(opcKey);
            for (String item : values)
            {
                String valueTitle = camera.getCameraPropertyValueTitle(item);
                if (value.equals(valueTitle))
                {
                    camera.setCameraPropertyValue(opcKey, item);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier)
    {
        this.updateReceiver = notifier;
        try
        {
            camera.setCameraStatusListener(this);
            camera.setCameraPropertyListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stopStatusWatch()
    {
        this.updateReceiver = null;
    }

    @Override
    public void onUpdateStatus(OLYCamera olyCamera, String name)
    {
        try
        {
            if ((name == null)||(updateReceiver == null))
            {
                return;
            }
            String value;
            switch (name)
            {
                case CAMERA_STATUS_APERTURE_VALUE:
                    value = camera.getActualApertureValue();
                    updateReceiver.updatedAperture(camera.getCameraPropertyValueTitle(value));
                    break;
                case CAMERA_STATUS_SHUTTER_SPEED:
                    value = camera.getActualShutterSpeed();
                    updateReceiver.updatedShutterSpeed(camera.getCameraPropertyValueTitle(value));
                    break;
                case CAMERA_STATUS_EXPOSURE_COMPENSATION:
                    value = camera.getActualExposureCompensation();
                    updateReceiver.updatedExposureCompensation(camera.getCameraPropertyValueTitle(value));
                    break;
                case CAMERA_STATUS_ISO_SENSITIVITY:
                    value = camera.getActualIsoSensitivity();
                    updateReceiver.updateIsoSensitivity(camera.getCameraPropertyValueTitle(value));
                    break;
                case CAMERA_STATUS_EXPOSURE_WARNING:
                case CAMERA_STATUS_EXPOSURE_METERING_WARNING:
                case CAMERA_STATUS_ACTUAL_ISO_SENSITIITY_WARNING:
                case CAMERA_STATUS_HIGH_TEMPERATURE_WARNING:
                    updateReceiver.updateWarning(name);
                    break;
                case CAMERA_STATUS_RECORDABLEIMAGES:
                case CAMERA_STATUS_MEDIA_BUSY:
                case CAMERA_STATUS_MEDIA_ERROR:
                    updateReceiver.updateStorageStatus(name);
                    break;
                case CAMERA_STATUS_DETECT_FACES:
                case CAMERA_STATUS_FOCAL_LENGTH:
                case CAMERA_STATUS_LEVEL_GAUGE:
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkUpdateStatus(OLYCamera olyCamera)
    {
        String takeMode = getPropertyTitle(olyCamera, "TAKEMODE");
        if (!takeMode.equals(currentTakeMode))
        {
            currentTakeMode = takeMode;
            updateReceiver.updatedTakeMode(currentTakeMode);
        }
        String meteringMode = getPropertyTitle(olyCamera, "AE");
        if (!meteringMode.equals(currentMeteringMode))
        {
            currentMeteringMode = meteringMode;
            updateReceiver.updatedMeteringMode(currentMeteringMode);
        }
        String wbMode = getPropertyTitle(olyCamera, "WB");
        if (!wbMode.equals(currentWBMode))
        {
            currentWBMode = wbMode;
            updateReceiver.updatedWBMode(currentWBMode);
        }

        try
        {
            String remainBattery = olyCamera.getCameraPropertyValue("BATTERY_LEVEL");
            if (!remainBattery.equals(currentRemainBattery))
            {
               Map<String, Integer> batteryIconList = new HashMap<String, Integer>() {
                    {
                        put("<BATTERY_LEVEL/UNKNOWN>"       , 0);
                        put("<BATTERY_LEVEL/CHARGE>"        , 100);
                        put("<BATTERY_LEVEL/EMPTY>"         , 0);
                        put("<BATTERY_LEVEL/WARNING>"       , 30);
                        put("<BATTERY_LEVEL/LOW>"           , 50);
                        put("<BATTERY_LEVEL/FULL>"          , 100);
                        put("<BATTERY_LEVEL/EMPTY_AC>"       , 0);
                        put("<BATTERY_LEVEL/SUPPLY_WARNING>", 30);
                        put("<BATTERY_LEVEL/SUPPLY_LOW>"    , 50);
                        put("<BATTERY_LEVEL/SUPPLY_FULL>"   , 100);
                    }
                };
                currentRemainBattery = remainBattery;
                int percentage = batteryIconList.get(remainBattery);
                Log.v(TAG, "currentRemainBattery : " + currentRemainBattery + "(" + percentage + ")");
                updateReceiver.updateRemainBattery(percentage);
            }
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        String shutterSpeed = getPropertyTitle(olyCamera, "SHUTTER");
        if (!shutterSpeed.equals(currentShutterSpeed))
        {
            currentShutterSpeed = shutterSpeed;
            updateReceiver.updatedShutterSpeed(currentShutterSpeed);
        }
        String aperture = getPropertyTitle(olyCamera, "APERTURE");
        if (!aperture.equals(currentAperture))
        {
            currentAperture = aperture;
            updateReceiver.updatedAperture(currentAperture);
        }
        String exposureCompensation = getPropertyTitle(olyCamera, "EXPREV");
        if (!exposureCompensation.equals(currentExposureCompensation))
        {
            currentExposureCompensation = exposureCompensation;
            updateReceiver.updatedExposureCompensation(currentExposureCompensation);
        }
    }

    private String getPropertyTitle(OLYCamera olyCamera, String propertyName)
    {
        String value = "";
        try
        {
            value = olyCamera.getCameraPropertyValueTitle(olyCamera.getCameraPropertyValue(propertyName));
            Log.v(TAG, "getPropertyTitle : " + value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (value);
    }

    @Override
    public void onUpdateCameraProperty(OLYCamera olyCamera, String name)
    {
        try
        {
            checkUpdateStatus(olyCamera);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
