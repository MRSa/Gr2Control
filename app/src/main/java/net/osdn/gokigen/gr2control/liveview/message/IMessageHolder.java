package net.osdn.gokigen.gr2control.liveview.message;

public interface IMessageHolder
{
    int getSize(IMessageDrawer.MessageArea area);
    int getColor(IMessageDrawer.MessageArea area);
    String getMessage(IMessageDrawer.MessageArea area);

    boolean isLevel();
    float getLevel(IMessageDrawer.LevelArea area);
    int getLevelColor(float value);
    IMessageDrawer getMessageDrawer();
}
