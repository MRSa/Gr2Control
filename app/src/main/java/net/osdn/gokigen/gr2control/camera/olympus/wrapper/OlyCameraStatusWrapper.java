package net.osdn.gokigen.gr2control.camera.olympus.wrapper;

import android.support.annotation.NonNull;
import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.liveview.ICameraStatusUpdateNotify;

import java.util.ArrayList;
import java.util.List;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

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
/*
    private static final String CAMERA_STATUS_DETECT_FACES = "DetectedHumanFaces";
    private static final String CAMERA_STATUS_FOCAL_LENGTH = "ActualFocalLength";
    private static final String CAMERA_STATUS_LEVEL_GAUGE = "LevelGauge";
*/

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


    @Override
    public @NonNull List<String> getStatusList(@NonNull String key)
    {
        List<String> array = new ArrayList<>();

        // OPC用に変更...
/*
        String BATTERY = "battery";
        String STATE = "state";
        String FOCUS_MODE = "focusMode";
        String AF_MODE = "AFMode";

        String RESOLUTION = "reso";
        String DRIVE_MODE = "shootMode";
        String WHITE_BALANCE = "WBMode";
        String AE = "meteringMode";

        String EFFECT = "effect";
        String TAKE_MODE = "exposureMode";
        String IMAGESIZE = "stillSize";
        String MOVIESIZE = "movieSize";

        String APERATURE = "av";
        String SHUTTER_SPEED = "tv";
        String ISO_SENSITIVITY = "sv";
        String EXPREV = "xv";
        String FLASH_XV = "flashxv";
*/
        return (array);
    }

    @Override
    public String getStatus(@NonNull String key)
    {
        return ("");
    }

    @Override
    public void setStatus(@NonNull String key, @NonNull String value)
    {

    }

    @Override
    public void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier)
    {
        this.updateReceiver = notifier;
        try
        {
            camera.setCameraStatusListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stoptStatusWatch()
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
                default:
                    checkUpdateStatus(olyCamera);
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
        String remainBattery = getPropertyTitle(olyCamera, "BATTERY_LEVEL");
        if (!remainBattery.equals(currentRemainBattery))
        {
            currentRemainBattery = remainBattery;
            int percentage = 0;

            Log.v(TAG, "currentRemainBattery : " + currentRemainBattery);
/*
            UNKNOWN	未検出
            CHARGE	充電中
            EMPTY	電池残量0
            WARNING	電池残量小
            LOW	電池残量中間
            FULL	電池残量フル
            EMPTY_AC	電池残量0/給電
            SUPPLY_WARNING	電池残量小/給電
            SUPPLY_LOW	電池残量中間/給電
            SUPPLY_FULL	電池残量フル/給電
 */
            updateReceiver.updateRemainBattery(percentage);
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
