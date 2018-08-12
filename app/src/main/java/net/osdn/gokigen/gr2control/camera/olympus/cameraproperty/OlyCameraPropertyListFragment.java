package net.osdn.gokigen.gr2control.camera.olympus.cameraproperty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.olympus.wrapper.property.IOlyCameraPropertyProvider;

import java.util.HashMap;

public class OlyCameraPropertyListFragment extends Fragment implements CameraPropertyLoader.IPropertyLoaderCallback
{
    private final String TAG = toString();
    private IOlyCameraPropertyProvider propertyInterface = null;

    private CameraPropertyLoader propertyLoader = null;
    private CameraPropertyOperator propertyOperator = null;
    private ProgressDialog busyDialog = null;
    private ListView listView = null;

    /**
     *  カメラプロパティをやり取りするインタフェースを生成する
     *
     */
    public static OlyCameraPropertyListFragment newInstance(@NonNull Context context, @NonNull IOlyCameraPropertyProvider propertyInterface)
    {
        OlyCameraPropertyListFragment instance = new OlyCameraPropertyListFragment();
        instance.prepare(context, propertyInterface);

        // パラメータはBundleにまとめておく
        Bundle arguments = new Bundle();
        //arguments.putString("title", title);
        //arguments.putString("message", message);
        instance.setArguments(arguments);

        return (instance);
    }

    private void prepare(@NonNull Context context, @NonNull IOlyCameraPropertyProvider propertyInterface)
    {
        Log.v(TAG, "prepare()");

        this.propertyInterface = propertyInterface;
        if (propertyLoader == null)
        {
            propertyLoader = new CameraPropertyLoader(propertyInterface, this);
        }
        if (propertyOperator == null)
        {
            propertyOperator = new CameraPropertyOperator(context, propertyLoader);
        }
    }

    /**
     *
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

    }

    /**
     *
     *
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");
    }
    /**
     *
     *
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragmant_camera_property, container, false);
        setHasOptionsMenu(true);

        listView = view.findViewById(R.id.CameraPropertyListView);

        ImageView restore_properties = view.findViewById(R.id.propertySettings_restore);
        restore_properties.setOnClickListener(propertyOperator);

        return (view);
    }

    /**
     *
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null)
        {
            ActionBar bar = activity.getSupportActionBar();
            if (bar != null) {
                bar.setTitle(getString(R.string.app_name));
            }
        }
    }

    /**
     *
     *
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(TAG, "onStart()");

    }

    /**
     *
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume() Start");

        loadCameraPropertyItems(true);

        Log.v(TAG, "onResume() End");
    }
    /**
     *
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause() Start");

        try
        {
            commitCameraPropertyItems();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.v(TAG, "onPause() End");
    }

    private void runOnUiThread(Runnable action)
    {
        Activity activity = getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }

    private void loadCameraPropertyItems(boolean isPropertyLoad)
    {
        // 実行中ダイアログを取得する
        busyDialog = new ProgressDialog(getActivity());
        busyDialog.setTitle(getString(R.string.dialog_title_loading_properties));
        busyDialog.setMessage(getString(R.string.dialog_message_loading_properties));
        busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        busyDialog.setCancelable(false);
        busyDialog.show();

        // データ読み込み処理（別スレッドで実行）
        if (isPropertyLoad)
        {
            new Thread(propertyLoader).start();
        }
    }

    private void commitCameraPropertyItems()
    {
        ListAdapter adapter = listView.getAdapter();
        int count = adapter.getCount();
        Log.v(TAG, "----- CHANGED CAMERA PROPERTIES { -----");
        final HashMap<String, String> propertiesToChange = new HashMap<>();
        for (int index = 0; index < count; index++)
        {
            CameraPropertyArrayItem item = (CameraPropertyArrayItem) adapter.getItem(index);
            if (item.isChanged())
            {
                Log.v(TAG, ">> " + item.getPropertyName() + " " + item.getPropertyValue());
                propertiesToChange.put(item.getPropertyName(), item.getPropertyValue());
            }
        }
        Log.v(TAG, "----- } CHANGED CAMERA PROPERTIES -----");
        if (!propertiesToChange.isEmpty())
        {
            if (propertyInterface != null)
            {
                propertyInterface.setCameraPropertyValues(propertiesToChange);
            }
            else
            {
                Log.i(TAG, "----- PROPERTY CHANGE INTERFACE IS NULL -----");
            }
        }
    }

    @Override
    public void finished()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // アイテムをListに反映
                if (listView != null)
                {
                    final CameraPropertyArrayAdapter adapter = new CameraPropertyArrayAdapter(getContext(), R.layout.listarrayitems, propertyLoader.getItemList());
                    CameraPropertyValueSelector selector = new CameraPropertyValueSelector(getContext(), propertyInterface, new ICametaPropertyUpdateNotify() {
                        @Override
                        public void onCameraPropertyUpdate(int which)
                        {
                            Log.v(TAG, "onCameraPropertyUpdate()");
                            adapter.notifyDataSetChanged();
                        }
                    });
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(selector);
                    listView.setOnLongClickListener(selector);
                }

                if (busyDialog != null)
                {
                    busyDialog.dismiss();
                    busyDialog = null;
                }
                System.gc();
            }
        });
    }

    @Override
    public void resetProperty()
    {
        try
        {
            Log.v(TAG, "resetProperty()");
            CameraPropertyArrayAdapter adapter = (CameraPropertyArrayAdapter)listView.getAdapter();
            adapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
