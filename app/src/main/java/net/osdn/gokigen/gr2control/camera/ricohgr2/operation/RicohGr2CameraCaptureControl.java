package net.osdn.gokigen.gr2control.camera.ricohgr2.operation;

import android.support.annotation.NonNull;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2MovieShotControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2SingleShotControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;

/**
 *
 *
 */
public class RicohGr2CameraCaptureControl implements ICaptureControl
{
    private final RicohGr2SingleShotControl singleShotControl;
    private final RicohGr2MovieShotControl movieShotControl;
    private final ICameraStatus cameraStatus;
    private final boolean useGrCommand;

    /**
     *
     *
     */
    public RicohGr2CameraCaptureControl(boolean useGrCommand, @NonNull IAutoFocusFrameDisplay frameDisplayer, @NonNull ICameraStatus cameraStatus)
    {
        this.useGrCommand = useGrCommand;
        this.cameraStatus = cameraStatus;
        singleShotControl = new RicohGr2SingleShotControl(frameDisplayer);
        movieShotControl = new RicohGr2MovieShotControl(frameDisplayer);
    }

    /**
     *
     *
     */
    @Override
    public void doCapture(int kind)
    {
        try
        {
            if (cameraStatus.getStatus(ICameraStatus.TAKE_MODE).contains(ICameraStatus.TAKE_MODE_MOVIE))
            {
                movieShotControl.toggleMovie();
            }
            else
            {
                singleShotControl.singleShot(useGrCommand);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
