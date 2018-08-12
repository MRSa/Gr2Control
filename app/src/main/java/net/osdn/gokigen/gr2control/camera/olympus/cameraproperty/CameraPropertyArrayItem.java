package net.osdn.gokigen.gr2control.camera.olympus.cameraproperty;


public class CameraPropertyArrayItem
{
    private int iconResource;
    private final String propertyName;
    private final String propertyTitle;
    private final String initialValue;
    private final String initialValueTitle;
    private final int    initialIconResource;
    private String propertyValue;
    private String propertyValueTitle;

    CameraPropertyArrayItem(String name, String title, String valueTitle, String value, int iconId1)
    {
        iconResource = iconId1;
        propertyName = name;
        propertyTitle = title;
        propertyValueTitle = valueTitle;
        propertyValue = value;
        initialValueTitle = valueTitle;
        initialValue = value;
        initialIconResource = iconId1;
    }

    public boolean isChanged()
    {
        return (!propertyValue.equals(initialValue));
    }

    public String getPropertyName()
    {
        return (propertyName);
    }

    public String getPropertyTitle()
    {
        return (propertyTitle);
    }

    public String getInitialValue()
    {
        return (initialValue);
    }

    public int getIconResource()
    {
        return (iconResource);
    }

    public void setIconResource(int iconId)
    {
        iconResource = iconId;
    }

    public String getPropertyValueTitle()
    {
        return (propertyValueTitle);
    }

    public String getPropertyValue()
    {
        return (propertyValue);
    }

    public void setPropertyValue(String valueTitle, String value)
    {
        propertyValueTitle = valueTitle;
        propertyValue = value;
    }

    public void resetValue()
    {
        propertyValue = initialValue;
        propertyValueTitle = initialValueTitle;
        iconResource = initialIconResource;
    }
}
