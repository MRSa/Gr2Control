package net.osdn.gokigen.gr2control.liveview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.IChangeScene;


/**
 *  撮影用ライブビュー画面
 *
 */
public class LiveViewFragment extends Fragment implements IStatusViewDrawer, IFocusingModeNotify, IFavoriteSettingDialogKicker, ICameraStatusUpdateNotify
{
    private final String TAG = this.toString();
    private static final int COMMAND_MY_PROPERTY = 0x00000100;

    private ILiveViewControl liveViewControl = null;
    private IZoomLensControl zoomLensControl = null;
    private IInterfaceProvider interfaceProvider = null;
    private IDisplayInjector interfaceInjector = null;
    //private OlympusCameraLiveViewListenerImpl liveViewListener = null;
    private IChangeScene changeScene = null;
    private ICameraInformation cameraInformation = null;
    private ICameraStatusWatcher statusWatcher = null;
    private LiveViewClickTouchListener onClickTouchListener = null;

    private TextView statusArea = null;
    private TextView focalLengthArea = null;
    private CameraLiveImageView imageView = null;

    private ImageView manualFocus = null;
    private ImageButton showGrid = null;
    private ImageView connectStatus = null;
    private Button changeLiveViewScale = null;

    private boolean imageViewCreated = false;
    private View myView = null;
    private String messageValue = "";

    private ICameraConnection.CameraConnectionStatus currentConnectionStatus =  ICameraConnection.CameraConnectionStatus.UNKNOWN;

    public static LiveViewFragment newInstance(IChangeScene sceneSelector, @NonNull IInterfaceProvider provider)
    {
        LiveViewFragment instance = new LiveViewFragment();
        instance.prepare(sceneSelector, provider);

        // パラメータはBundleにまとめておく
        Bundle arguments = new Bundle();
        //arguments.putString("title", title);
        //arguments.putString("message", message);
        instance.setArguments(arguments);

        return (instance);
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
/*
        if (liveViewListener == null)
        {
            liveViewListener = new OlympusCameraLiveViewListenerImpl();
        }
*/
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
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(TAG, "onCreateView()");
        if ((imageViewCreated)&&(myView != null))
        {
            // Viewを再利用。。。
            Log.v(TAG, "onCreateView() : called again, so do nothing... : " + myView);
            return (myView);
        }

        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        myView = view;
        imageViewCreated = true;
        try
        {

            imageView = view.findViewById(R.id.cameraLiveImageView);
            if (interfaceInjector != null)
            {
                interfaceInjector.injectDisplay(imageView, imageView, this);
            }
            else
            {
                Log.v(TAG, "interfaceInjector is NULL...");
            }
            if (onClickTouchListener == null)
            {
                onClickTouchListener = new LiveViewClickTouchListener(this.getActivity(), imageView, this, changeScene, interfaceProvider, this);
            }
            imageView.setOnClickListener(onClickTouchListener);
            imageView.setOnTouchListener(onClickTouchListener);

            setOnClickListener(view, R.id.hideControlPanelTextView);
            setOnClickListener(view, R.id.showControlPanelTextView);
            setOnClickListener(view, R.id.showKeyPanelImageView);
            setOnClickListener(view, R.id.hideKeyPanelTextView);
            setOnClickListener(view, R.id.shutter_button);
            setOnClickListener(view, R.id.focusUnlockImageView);

            /*
            view.findViewById(R.id.show_preference_button).setOnClickListener(onClickTouchListener);
            view.findViewById(R.id.camera_property_settings_button).setOnClickListener(onClickTouchListener);
            view.findViewById(R.id.shutter_button).setOnClickListener(onClickTouchListener);
            view.findViewById(R.id.btn_zoomin).setOnClickListener(onClickTouchListener);
            view.findViewById(R.id.btn_zoomout).setOnClickListener(onClickTouchListener);

            manualFocus = view.findViewById(R.id.focusing_button);
            changeLiveViewScale = view.findViewById(R.id.live_view_scale_button);

            ICameraConnection.CameraConnectionMethod connectionMethod = interfaceProvider.getCammeraConnectionMethod();

            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                view.findViewById(R.id.show_favorite_settings_button).setOnClickListener(onClickTouchListener);
            }
            else
            {
                // お気に入りボタン(とMFボタン)は、SONYモード, RICOH GR2モードのときには表示しない
                final View favoriteButton = view.findViewById(R.id.show_favorite_settings_button);
                final View propertyButton = view.findViewById(R.id.camera_property_settings_button);
                if ((favoriteButton != null)&&(manualFocus != null))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            favoriteButton.setVisibility(View.INVISIBLE);
                            if (manualFocus != null)
                            {
                                manualFocus.setVisibility(View.INVISIBLE);
                            }
                            propertyButton.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                if (connectionMethod == ICameraConnection.CameraConnectionMethod.SONY)
                {
                    if (changeLiveViewScale != null)
                    {
                        changeLiveViewScale.setVisibility(View.INVISIBLE);
                    }
                }
                else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
                {
                    if (changeLiveViewScale != null)
                    {
                        changeLiveViewScale.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (manualFocus != null)
            {
                manualFocus.setOnClickListener(onClickTouchListener);
            }
            changedFocusingMode();

            if (changeLiveViewScale != null)
            {
                changeLiveViewScale.setOnClickListener(onClickTouchListener);
            }

            showGrid = view.findViewById(R.id.show_hide_grid_button);
            showGrid.setOnClickListener(onClickTouchListener);
            updateGridIcon();

            updateConnectionStatus(ICameraConnection.CameraConnectionStatus.UNKNOWN);

            statusArea = view.findViewById(R.id.informationMessageTextView);
            focalLengthArea = view.findViewById(R.id.focal_length_with_digital_zoom_view);
*/
            connectStatus = view.findViewById(R.id.connect_disconnect_button);
            connectStatus.setOnClickListener(onClickTouchListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return (view);
    }

    private void setOnClickListener(View view, int id)
    {
        try
        {
            View button = view.findViewById(id);
            if (button != null)
            {
                button.setOnClickListener(onClickTouchListener);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void prepare(IChangeScene sceneSelector, IInterfaceProvider interfaceProvider)
    {
        Log.v(TAG, "prepare()");

        IDisplayInjector interfaceInjector;
        interfaceInjector = interfaceProvider.getRicohGr2Infterface().getDisplayInjector();
/*
        ICameraConnection.CameraConnectionMethod connectionMethod = interfaceProvider.getCammeraConnectionMethod();
        if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
        {
            interfaceInjector = interfaceProvider.getRicohGr2Infterface().getDisplayInjector();
        }
        else if (connectionMethod == ICameraConnection.CameraConnectionMethod.SONY)
        {
            interfaceInjector = interfaceProvider.getSonyInterface().getDisplayInjector();
        }
        else // if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
        {
            interfaceInjector = interfaceProvider.getOlympusInterface().getDisplayInjector();
        }
*/
        this.changeScene = sceneSelector;
        this.interfaceProvider = interfaceProvider;
        this.interfaceInjector = interfaceInjector;

        //if  (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
        {
            this.liveViewControl = interfaceProvider.getRicohGr2Infterface().getLiveViewControl();
            this.zoomLensControl = interfaceProvider.getRicohGr2Infterface().getZoomLensControl();
            this.cameraInformation = interfaceProvider.getRicohGr2Infterface().getCameraInformation();
            this.statusWatcher = interfaceProvider.getRicohGr2Infterface().getCameraStatusWatcher();
        }
/*
        else  if (connectionMethod == ICameraConnection.CameraConnectionMethod.SONY)
        {
            this.liveViewControl = interfaceProvider.getSonyInterface().getSonyLiveViewControl();
            this.zoomLensControl = interfaceProvider.getSonyInterface().getZoomLensControl();
            this.cameraInformation = interfaceProvider.getSonyInterface().getCameraInformation();
        }
        else //  if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
        {
            this.liveViewControl = interfaceProvider.getOlympusInterface().getLiveViewControl();
            this.zoomLensControl = interfaceProvider.getOlympusInterface().getZoomLensControl();
            this.cameraInformation = interfaceProvider.getOlympusInterface().getCameraInformation();
        }
*/
    }

    /**
     *  カメラとの接続状態の更新
     *
     */
    @Override
    public void updateConnectionStatus(final ICameraConnection.CameraConnectionStatus connectionStatus)
    {
        try
        {
            currentConnectionStatus = connectionStatus;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    int id = R.drawable.ic_cloud_off_black_24dp;
                    if (currentConnectionStatus == ICameraConnection.CameraConnectionStatus.CONNECTING)
                    {
                        id = R.drawable.ic_cloud_queue_black_24dp;
                    }
                    else if  (currentConnectionStatus == ICameraConnection.CameraConnectionStatus.CONNECTED)
                    {
                        id = R.drawable.ic_cloud_done_black_24dp;
                    }
                    if (connectStatus != null)
                    {
                        connectStatus.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
                        connectStatus.invalidate();
                    }
                    if (imageView != null)
                    {
                        imageView.invalidate();
                    }
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  グリッドの表示・非表示の更新
     *
     */
    @Override
    public void updateGridIcon()
    {
        try
        {
            int id = (imageView.isShowGrid()) ? R.drawable.ic_grid_off_black_24dp : R.drawable.ic_grid_on_black_24dp;
            showGrid.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
            showGrid.invalidate();
            imageView.invalidate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   AF/MFの表示を更新する
     *
     */
    @Override
    public void changedFocusingMode()
    {
        try
        {
            if ((cameraInformation == null)||(manualFocus == null))
            {
                return;
            }
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (currentConnectionStatus == ICameraConnection.CameraConnectionStatus.CONNECTED)
                    {
                        manualFocus.setSelected(cameraInformation.isManualFocus());
                        manualFocus.invalidate();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLiveViewScale(boolean isChangeScale)
    {
        try
        {
            Log.v(TAG, "updateLiveViewScale() : " + isChangeScale);

            // ライブビューの倍率設定
            liveViewControl.updateMagnifyingLiveViewScale(isChangeScale);

            // ボタンの文字を更新する
            float scale = liveViewControl.getMagnifyingLiveViewScale();
            final String datavalue = "LV: " + scale;

            // デジタルズームの倍率を表示する
            float digitalZoom = liveViewControl.getDigitalZoomScale();
            final String digitalValue = (digitalZoom > 1.0f) ? "D x" + digitalZoom : "";

            // 更新自体は、UIスレッドで行う
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    changeLiveViewScale.setText(datavalue);
                    changeLiveViewScale.postInvalidate();

                    focalLengthArea.setText(digitalValue);
                    focalLengthArea.postInvalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
/*
        // 撮影モードかどうかを確認して、撮影モードではなかったら撮影モードに切り替える
        if ((changeRunModeExecutor != null)&&(!changeRunModeExecutor.isRecordingMode()))
        {
            // Runモードを切り替える。（でも切り替えると、設定がクリアされてしまう...。
            changeRunModeExecutor.changeRunMode(true);
        }

        // ステータスの変更を通知してもらう
        camera.setCameraStatusListener(statusListener);

        // 画面下部の表示エリアの用途を切り替える
        setupLowerDisplayArea();
*/
        // propertyを取得
        try
        {
            Context context = getContext();
            if (context != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                // グリッド・フォーカスアシストの情報を戻す
                boolean showGrid = preferences.getBoolean(IPreferencePropertyAccessor.SHOW_GRID_STATUS, false);
                if ((imageView != null) && (imageView.isShowGrid() != showGrid)) {
                    imageView.toggleShowGridFrame();
                    imageView.postInvalidate();
                }
            }
            if (currentConnectionStatus == ICameraConnection.CameraConnectionStatus.CONNECTED)
            {
                startLiveView();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

        // ライブビューとステータス監視の停止
        try
        {
            liveViewControl.stopLiveView();
            stopWatchStatus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.v(TAG, "onPause() End");
    }

    /**
     *   表示エリアに文字を表示する
     *
     */
    @Override
    public void updateStatusView(String message)
    {
        messageValue = message;
        runOnUiThread(new Runnable()
        {
            /**
             * カメラの状態(ステータステキスト）を更新する
             * (ステータステキストは、プライベート変数で保持して、書き換える)
             */
            @Override
            public void run()
            {
                if (statusArea != null)
                {
                    statusArea.setText(messageValue);
                    statusArea.invalidate();
                }
            }
        });
    }

    /**
     *   ライブビューの開始
     *
     */
    @Override
    public void startLiveView()
    {
        ICameraConnection.CameraConnectionMethod connectionMethod = interfaceProvider.getCammeraConnectionMethod();

        if (liveViewControl == null)
        {
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                Log.v(TAG, "startLiveView() : liveViewControl is null.");
                return;
            }
            else
            {
                // ダミー
                prepare(changeScene, interfaceProvider);
            }
        }
        try
        {
            // ライブビューの開始
            Context context = getContext();
            if (context != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                liveViewControl.changeLiveViewSize(preferences.getString(IPreferencePropertyAccessor.LIVE_VIEW_QUALITY, IPreferencePropertyAccessor.LIVE_VIEW_QUALITY_DEFAULT_VALUE));
            }
            ILiveViewListener lvListener = interfaceProvider.getRicohGr2Infterface().getLiveViewListener();
/*
            ILiveViewListener lvListener;
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
            {
                lvListener = interfaceProvider.getRicohGr2Infterface().getLiveViewListener();
            }
            else if (connectionMethod == ICameraConnection.CameraConnectionMethod.SONY)
            {
                lvListener = interfaceProvider.getSonyInterface().getLiveViewListener();
            }
            else  // if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
            {
                interfaceProvider.getOlympusLiveViewListener().setOlympusLiveViewListener(liveViewListener);
                lvListener = liveViewListener;
            }
*/
            lvListener.setCameraLiveImageView(imageView);
            liveViewControl.startLiveView();

            // ステータス監視も実施する
            startWatchStatus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void showFavoriteSettingDialog()
    {
        try
        {
            Log.v(TAG, "showFavoriteSettingDialog()");
/*
            LoadSaveMyCameraPropertyDialog dialog = new LoadSaveMyCameraPropertyDialog();
            dialog.setTargetFragment(this, COMMAND_MY_PROPERTY);
            dialog.setPropertyOperationsHolder(new LoadSaveCameraProperties(getActivity(), interfaceProvider.getOlympusInterface()));
            FragmentManager manager = getFragmentManager();
            if (manager != null)
            {
                dialog.show(manager, "my_dialog");
            }
*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     */
    private void startWatchStatus()
    {
        if (statusWatcher != null)
        {
            statusWatcher.startStatusWatch(this);
        }
    }

    /**
     *
     *
     */
    private void stopWatchStatus()
    {
        if (statusWatcher != null)
        {
            statusWatcher.stoptStatusWatch();
        }
    }

    /**
     *
     *
     */
    private void runOnUiThread(Runnable action)
    {
        Activity activity = getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }

    @Override
    public void updatedTakeMode(final String mode)
    {
        try
        {
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TextView view = activity.findViewById(R.id.takemodeTextView);
                    view.setText(mode);
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updatedShutterSpeed(final String tv)
    {
        try
        {
            final String shutterSpeed = tv.replace(".", "/");
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TextView view = activity.findViewById(R.id.shutterSpeedTextView);
                    view.setText(shutterSpeed);
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void updatedAperture(final String av)
    {
        try
        {
            final String apertureValue = (av.length() > 1) ? ("F" + av) : "";
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TextView view = activity.findViewById(R.id.apertureValueTextView);
                    view.setText(apertureValue);
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updatedExposureCompensation(final String xv)
    {
        try
        {
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TextView view = activity.findViewById(R.id.exposureCompensationTextView);
                    view.setText(xv);
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updatedMeteringMode(final String meteringMode)
    {
        try
        {
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TextView view = activity.findViewById(R.id.aeModeTextView);
                    view.setText(meteringMode);
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updatedWBMode(final String wbMode)
    {
        // とりあえず何もしない...
    }

    /**
     *   残りバッテリー状態をアイコンで示す
     *
     */
    @Override
    public void updateRemainBattery(int percentage)
    {
        try
        {
            final Activity activity = getActivity();
            if (activity == null)
            {
                return;
            }
            int iconId;
            if (percentage < 20)
            {
                iconId = R.drawable.ic_battery_alert_black_24dp;
            }
            else if (percentage < 60)
            {
                iconId = R.drawable.ic_battery_20_black_24dp;
            }
            else if (percentage < 80)
            {
                iconId = R.drawable.ic_battery_60_black_24dp;
            }
            else
            {
                iconId = R.drawable.ic_battery_full_black_24dp;
            }
            final int id = iconId;
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ImageView view = activity.findViewById(R.id.currentBatteryImageView);
                    view.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
                    view.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
