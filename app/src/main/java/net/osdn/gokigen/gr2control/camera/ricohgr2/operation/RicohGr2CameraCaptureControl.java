package net.osdn.gokigen.gr2control.camera.ricohgr2.operation;

import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2MovieShotControl;
import net.osdn.gokigen.gr2control.camera.ricohgr2.operation.takepicture.RicohGr2SingleShotControl;
import net.osdn.gokigen.gr2control.liveview.IAutoFocusFrameDisplay;

import androidx.annotation.NonNull;

/**
 *
 *
 */
public class RicohGr2CameraCaptureControl implements ICaptureControl
{
    private final RicohGr2SingleShotControl singleShotControl;
    private final RicohGr2MovieShotControl movieShotControl;
    private final ICameraStatus cameraStatus;
    private final boolean captureAfterAf;

    private boolean useGR2command = false;

    /**
     *
     *
     */
    public RicohGr2CameraCaptureControl(boolean captureAfterAf, @NonNull IAutoFocusFrameDisplay frameDisplayer, @NonNull ICameraStatus cameraStatus)
    {
        this.captureAfterAf = captureAfterAf;
        this.cameraStatus = cameraStatus;
        singleShotControl = new RicohGr2SingleShotControl(frameDisplayer);
        movieShotControl = new RicohGr2MovieShotControl(frameDisplayer);
    }

    public void setUseGR2Command(boolean useGR2command)
    {
        this.useGR2command = useGR2command;
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
                singleShotControl.singleShot(useGR2command, captureAfterAf);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
