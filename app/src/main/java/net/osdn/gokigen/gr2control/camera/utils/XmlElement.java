package net.osdn.gokigen.gr2control.camera.utils;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class XmlElement
{
    private static final String TAG = XmlElement.class.getSimpleName();
    private static final XmlElement NULL_ELEMENT = new XmlElement();

    private String tagName = "";
    private String tagValue;

    private LinkedList<XmlElement> childElements;
    private Map<String, String> attributes;
    private XmlElement parentElement;

    private XmlElement()
    {
        //Log.v(TAG, "XmlElement()");
        parentElement = null;
        childElements = new LinkedList<>();
        attributes = new HashMap<>();
        tagValue = "";
    }

    public XmlElement getParent()
    {
        return (parentElement);
    }
    public String getTagName()
    {
        //Log.v(TAG, "XmlElement Tag [" + tagName + "]");
        return (tagName);
    }

    private void setTagName(String name)
    {
        tagName = name;
    }

    public String getValue()
    {
        //Log.v(TAG, "XmlElement Value [" + tagValue + "]");
        return (tagValue);
    }
    private void setValue(String value)
    {
        tagValue = value;
    }

    private void putChild(XmlElement childItem)
    {
        childElements.add(childItem);
        childItem.setParent(this);
    }

    public XmlElement findChild(String name)
    {
        for (final XmlElement child : childElements)
        {
            if (child.getTagName().equals(name))
            {
                return (child);
            }
        }
        return (new XmlElement());
    }

    public List<XmlElement> findChildren(String name)
    {
        final List<XmlElement> tagItemList = new ArrayList<>();
        for (final XmlElement child : childElements)
        {
            if (child.getTagName().equals(name))
            {
                tagItemList.add(child);
            }
        }
        return (tagItemList);
    }

    private void setParent(XmlElement parent)
    {
        parentElement = parent;
    }

    private void putAttribute(String name, String value)
    {
        attributes.put(name, value);
    }

    public String getAttribute(String name, String defaultValue)
    {
        String ret = attributes.get(name);
        if (ret == null)
        {
            ret = defaultValue;
        }
        return (ret);
    }

    private static XmlElement parse(XmlPullParser xmlPullParser)
    {
        XmlElement rootElement = XmlElement.NULL_ELEMENT;
        try
        {
            XmlElement parsingElement = XmlElement.NULL_ELEMENT;
            MAINLOOP:
            while (true)
            {
                switch (xmlPullParser.next())
                {
                    case XmlPullParser.START_DOCUMENT:
                        Log.v(TAG, "------- START DOCUMENT -----");
                        break;
                    case XmlPullParser.START_TAG:
                        final XmlElement childItem = new XmlElement();
                        childItem.setTagName(xmlPullParser.getName());
                        if (parsingElement == XmlElement.NULL_ELEMENT) {
                            rootElement = childItem;
                        } else {
                            parsingElement.putChild(childItem);
                        }
                        parsingElement = childItem;

                        // Set Attribute
                        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++)
                        {
                            parsingElement.putAttribute(xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        parsingElement.setValue(xmlPullParser.getText());
                        break;

                    case XmlPullParser.END_TAG:
                        parsingElement = parsingElement.getParent();
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        Log.v(TAG, "------- END DOCUMENT -------");
                        break MAINLOOP;

                    default:
                        break MAINLOOP;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            rootElement = XmlElement.NULL_ELEMENT;
        }
        return (rootElement);
    }

    public static XmlElement parse(@NonNull String xmlStr)
    {
        try
        {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlStr));
            return parse(xmlPullParser);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (new XmlElement());
    }
}
