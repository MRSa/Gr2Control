package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.playback;

import android.util.Log;

import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.playback.CameraFileInfo;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FujiXImageContentInfo implements ICameraContent, IFujiXCommandCallback, ICameraFileInfo
{
    private final String TAG = toString();
    private final int indexNumber;
    private boolean isReceived = false;
    private boolean isDateValid = false;
    private Date date = null;
    private String realFileName = null;
    private boolean captured;
    private String av;
    private String sv;
    private String tv;
    private String xv;
    private int orientation;
    private String aspectRatio;
    private String cameraModel;
    private String latlng;
    private long fileSize;

    private byte[] rx_body;
    FujiXImageContentInfo(int indexNumber, byte[] rx_body)
    {
        this.indexNumber = indexNumber;
        this.rx_body = rx_body;
        if (this.rx_body != null)
        {
            updateInformation(rx_body);
        }
        else
        {
            date = new Date();
            isDateValid = false;
        }
    }

    @Override
    public String getCameraId()
    {
        return ("FujiX");
    }

    @Override
    public String getCardId()
    {
        return ("sd1");
    }

    @Override
    public String getContentPath()
    {
        return ("");
    }

    @Override
    public String getContentName()
    {
        try
        {
            if ((realFileName != null)&&(realFileName.contains(".MOV")))
            {
                return ("" + indexNumber + ".MOV");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ("" + indexNumber + ".JPG");
    }

    @Override
    public String getOriginalName()
    {
        if (realFileName != null)
        {
            return (realFileName);
        }
        return (getContentName());
    }

    @Override
    public boolean isRaw()
    {
        try
        {
            if ((realFileName != null)&&(realFileName.contains(".RAF")))
            {
                return (true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    @Override
    public boolean isMovie()
    {
        try
        {
            if ((realFileName != null)&&(realFileName.contains(".MOV")))
            {
                return (true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    @Override
    public boolean isDateValid()
    {
        return (isDateValid);
    }

    @Override
    public boolean isContentNameValid()
    {
        return (false);
    }

    @Override
    public Date getCapturedDate()
    {
        return (date);
    }

    @Override
    public void setCapturedDate(Date date)
    {
        try
        {
            this.date = date;
            isDateValid = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] body)
    {
        Log.v(TAG, " " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        Log.v(TAG, "RX : " + indexNumber + "(" + id + ") " + rx_body.length + " bytes.");
        this.rx_body = rx_body;
        updateInformation(rx_body);

    }

    public int getId()
    {
        return (indexNumber);
    }

    boolean isReceived()
    {
        return (isReceived);
    }


    private void updateInformation(byte[] rx_body)
    {
        try
        {
            if (rx_body.length >= 166)
            {
                // データの切り出し
                realFileName = new String(pickupString(rx_body, 65, 12));
                String dateString = new String(pickupString(rx_body, 92, 15));
                //char orientation = Character.(rx_body[151]);
                Log.v(TAG, "[" + indexNumber + "] FILE NAME : " + realFileName + "  DATE : '" + dateString + "'");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
                date = dateFormat.parse(dateString);
                isDateValid = true;
                isReceived = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   文字列を無理やり切り出す...
     *
     */
    private byte[] pickupString(byte[] data, int start, int length)
    {
        byte[] result = new byte[length];
        for (int index = 0; index < length; index++)
        {
            result[index] = data[start + index * 2];
        }
        return (result);
    }

    @Override
    public Date getDatetime()
    {
        return (date);
    }

    @Override
    public String getDirectoryPath()
    {
        return ("");
    }

    @Override
    public String getOriginalFilename()
    {
        return (getOriginalName());
    }

    @Override
    public String getFilename()
    {
        return (getContentName());
    }

    @Override
    public String getAperature()
    {
        return (av);
    }

    @Override
    public String getShutterSpeed()
    {
        return (tv);
    }

    @Override
    public String getIsoSensitivity()
    {
        return (sv);
    }

    @Override
    public String getExpRev()
    {
        return (xv);
    }

    @Override
    public int getOrientation()
    {
        return (orientation);
    }

    @Override
    public String getAspectRatio()
    {
        return (aspectRatio);
    }

    @Override
    public String getModel()
    {
        return (cameraModel);
    }

    @Override
    public String getLatLng()
    {
        return (latlng);
    }

    @Override
    public boolean getCaptured()
    {
        return (captured);
    }

    @Override
    public void updateValues(String dateTime, String av, String tv, String sv, String xv, int orientation, String aspectRatio, String model, String latLng, boolean captured)
    {
        this.av = av;
        this.tv = tv;
        this.sv = sv;
        this.xv = xv;
        this.orientation = orientation;
        this.aspectRatio = aspectRatio;
        this.cameraModel = model;
        this.latlng = latLng;
        this.captured = captured;
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            this.date = df.parse(dateTime);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
