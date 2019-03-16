package net.osdn.gokigen.gr2control.camera.olympus.cameraproperty;


class CameraPropertyArrayItem
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

    boolean isChanged()
    {
        return (!propertyValue.equals(initialValue));
    }

    String getPropertyName()
    {
        return (propertyName);
    }

    String getPropertyTitle()
    {
        return (propertyTitle);
    }

    String getInitialValue()
    {
        return (initialValue);
    }

    int getIconResource()
    {
        return (iconResource);
    }

    void setIconResource(int iconId)
    {
        iconResource = iconId;
    }

    String getPropertyValueTitle()
    {
        return (propertyValueTitle);
    }

    String getPropertyValue()
    {
        return (propertyValue);
    }

    void setPropertyValue(String valueTitle, String value)
    {
        propertyValueTitle = valueTitle;
        propertyValue = value;
    }

    void resetValue()
    {
        propertyValue = initialValue;
        propertyValueTitle = initialValueTitle;
        iconResource = initialIconResource;
    }
}
