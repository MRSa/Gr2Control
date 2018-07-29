package net.osdn.gokigen.gr2control.liveview.message;

import android.graphics.Color;

/**
 *
 *
 * Created by MRSa on 2017/03/01.
 */
public class ShowMessageHolder implements IMessageDrawer, IMessageHolder
{
    /**
     *
     */
    private class messageHolder
    {
        private String message = "";
        private int color = Color.BLUE;
        private int textSize = 16;

        String getMessage()
        {
            return message;
        }

        void setMessage(String message)
        {
            this.message = message;
        }

        int getColor()
        {
            return color;
        }

        void setColor(int color)
        {
            this.color = color;
        }

        int getTextSize()
        {
            return textSize;
        }

        void setTextSize(int textSize)
        {
            this.textSize = textSize;
        }
    }

    private messageHolder upperLeft = new messageHolder();
    private messageHolder upperRight = new messageHolder();
    private messageHolder center = new messageHolder();
    private messageHolder lowerLeft = new messageHolder();
    private messageHolder lowerRight = new messageHolder();
    private messageHolder upperCenter = new messageHolder();
    private messageHolder lowerCenter = new messageHolder();
    private messageHolder leftCenter = new messageHolder();
    private messageHolder rightCenter = new messageHolder();
    private float level_horizontal = Float.NaN;
    private float level_vertical = Float.NaN;

    private float LEVELGAUGE_THRESHOLD_MIDDLE = 2.0f;
    private float LEVELGAUGE_THRESHOLD_OVER = 15.0f;

    /**
     *   コンストラクタ
     *
     */
    public ShowMessageHolder()
    {
        center.setTextSize(24);
    }

    /**
     *
     *
     */
    private messageHolder decideHolder(MessageArea area)
    {
        messageHolder target;
        switch (area)
        {
            case CENTER:
                target = center;
                break;

            case UPLEFT:
                target = upperLeft;
                break;

            case UPRIGHT:
                target = upperRight;
                break;

            case LOWLEFT:
                target = lowerLeft;
                break;

            case LOWRIGHT:
                target = lowerRight;
                break;

            case UPCENTER:
                target = upperCenter;
                break;

            case LOWCENTER:
                target = lowerCenter;
                break;

            case LEFTCENTER:
                target = leftCenter;
                break;

            case RIGHTCENTER:
                target = rightCenter;
                break;

            default:
                target = null;
                break;
        }
        return (target);
    }

    /**
     *
     *
     */
    @Override
    public void setMessageToShow(MessageArea area, int color, int size, String message)
    {
        messageHolder target = decideHolder(area);
        if (target != null)
        {
            target.setColor(color);
            target.setTextSize(size);
            target.setMessage(message);
        }
    }

    /**
     *
     *
     */
    @Override
    public void setLevelToShow(LevelArea area, float value)
    {
        if (area == LevelArea.LEVEL_HORIZONTAL)
        {
            level_horizontal = value;
        }
        else if (area == LevelArea.LEVEL_VERTICAL)
        {
           level_vertical = value;
        }
    }

    /**
     *
     *
     */
    @Override
    public int getSize(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        if (target != null)
        {
            return (target.getTextSize());
        }
        return (0);
    }

    /**
     *
     *
     */
    @Override
    public int getColor(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        if (target != null)
        {
            return (target.getColor());
        }
        return (0);
    }

    /**
     *
     *
     */
    @Override
    public String getMessage(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        if (target != null)
        {
            return (target.getMessage());
        }
        return ("");
    }

    /**
     *
     *
     */
    @Override
    public boolean isLevel()
    {
        return (!((Float.isNaN(level_horizontal))||(Float.isNaN(level_vertical))));
    }

    /**
     *
     *
     */
    @Override
    public float getLevel(LevelArea area)
    {
        float value;
        if (area == LevelArea.LEVEL_HORIZONTAL)
        {
            value = level_horizontal;
        }
        else //  if (area == LevelArea.LEVEL_VERTICAL)
        {
            value = level_vertical;
        }
        return (value);
    }

    /**
     *   傾きの色を取得する
     *
     */
    @Override
    public int getLevelColor(float value)
    {
        value = Math.abs(value);

        if (value < LEVELGAUGE_THRESHOLD_MIDDLE)
        {
            return (Color.GREEN);
        }
        if (value > LEVELGAUGE_THRESHOLD_OVER)
        {
            return (Color.RED);
        }
        return (Color.YELLOW);
    }

    /**
     *
     *
     */
    @Override
    public IMessageDrawer getMessageDrawer()
    {
        return (this);
    }
}
