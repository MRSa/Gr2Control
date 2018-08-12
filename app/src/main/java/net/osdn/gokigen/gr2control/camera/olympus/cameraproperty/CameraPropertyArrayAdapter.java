package net.osdn.gokigen.gr2control.camera.olympus.cameraproperty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CameraPropertyArrayAdapter extends ArrayAdapter<CameraPropertyArrayItem>
{
    private LayoutInflater inflater;
    private final int textViewResourceId;
    private List<CameraPropertyArrayItem> listItems;

    CameraPropertyArrayAdapter(Context context, int textId, List<CameraPropertyArrayItem> items)
    {
        super(context, textId, items);

        textViewResourceId = textId;
        listItems = items;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     *
     */
    @Override
    public @NonNull View getView(int position, View convertView, @NonNull  ViewGroup parent)
    {
        View view;
        if(convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = inflater.inflate(textViewResourceId, null);
        }

        CameraPropertyArrayItem item = listItems.get(position);

        ImageView imageView = view.findViewWithTag("icon");
        imageView.setImageResource(item.getIconResource());

        TextView titleView = view.findViewWithTag("name");
        titleView.setText(item.getPropertyName());

        TextView detailView = view.findViewWithTag("title");
        detailView.setText(item.getPropertyTitle());

        TextView optionView = view.findViewWithTag("value");
        optionView.setText(item.getPropertyValueTitle());

        return (view);
    }
}
