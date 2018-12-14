package net.osdn.gokigen.gr2control.liveview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.camera.olympus.myolycameraprops.LoadSaveCameraProperties;
import net.osdn.gokigen.gr2control.camera.olympus.myolycameraprops.LoadSaveMyCameraPropertyDialog;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *  撮影用ライブビュー画面
 *
 */
public class LiveViewFragment extends Fragment implements IStatusViewDrawer, IFocusingModeNotify, IFavoriteSettingDialogKicker, ICameraStatusUpdateNotify, LiveViewKeyPanelClickListener.KeyPanelFeedback
{
    private final String TAG = this.toString();

    private ILiveViewControl liveViewControl = null;
    private IZoomLensControl zoomLensControl = null;
    private IInterfaceProvider interfaceProvider = null;
    private IDisplayInjector interfaceInjector = null;
    //private OlympusCameraLiveViewListenerImpl liveViewListener = null;
    private IChangeScene changeScene = null;
    private ICameraInformation cameraInformation = null;
    private ICameraStatusWatcher statusWatcher = null;
    private LiveViewClickTouchListener onClickTouchListener = null;
    private LiveViewControlPanelClickListener onPanelClickListener = null;
    private LiveViewKeyPanelClickListener onKeyPanelClickListener = null;

    private TextView statusArea = null;
    private TextView focalLengthArea = null;
    private CameraLiveImageView imageView = null;

    private ImageView manualFocus = null;
    private ImageView showGrid = null;
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
            Activity activity = this.getActivity();
            Vibrator vibrator = (activity != null) ? (Vibrator) activity.getSystemService(VIBRATOR_SERVICE) : null;
            if ((onClickTouchListener == null)&&(activity != null))
            {
                onClickTouchListener = new LiveViewClickTouchListener(activity, imageView, this, changeScene, interfaceProvider, this);
            }
            imageView.setOnClickListener(onClickTouchListener);
            imageView.setOnTouchListener(onClickTouchListener);

            setOnClickListener(view, R.id.hideControlPanelTextView);
            setOnClickListener(view, R.id.showControlPanelTextView);
            setOnClickListener(view, R.id.showKeyPanelImageView);
            setOnClickListener(view, R.id.hideKeyPanelTextView);
            setOnClickListener(view, R.id.shutter_button);
            setOnClickListener(view, R.id.focusUnlockImageView);
            setOnClickListener(view, R.id.show_images_button);
            setOnClickListener(view, R.id.camera_power_off_button);
            setOnClickListener(view, R.id.show_preference_button);
            setOnClickListener(view, R.id.show_hide_grid_button);
            setOnClickListener(view, R.id.zoom_in_button);
            setOnClickListener(view, R.id.zoom_out_button);
            setOnClickListener(view, R.id.specialButtonImageView);

            if (onPanelClickListener == null)
            {
                onPanelClickListener = new LiveViewControlPanelClickListener(activity, interfaceProvider);
            }
            setPanelClickListener(view, R.id.takemodeTextView);
            setPanelClickListener(view, R.id.shutterSpeedTextView);
            setPanelClickListener(view, R.id.apertureValueTextView);
            setPanelClickListener(view, R.id.exposureCompensationTextView);
            setPanelClickListener(view, R.id.aeModeImageView);
            setPanelClickListener(view, R.id.whiteBalanceTextView);
            setPanelClickListener(view, R.id.isoSensitivityTextView);
            setPanelClickListener(view, R.id.setEffectImageView);

            if (onKeyPanelClickListener == null)
            {
                onKeyPanelClickListener = new LiveViewKeyPanelClickListener(interfaceProvider, this, vibrator);
            }
            setKeyPanelClickListener(view, R.id.button_front_left);
            setKeyPanelClickListener(view, R.id.button_front_right);
            setKeyPanelClickListener(view, R.id.button_adjust_left);
            setKeyPanelClickListener(view, R.id.button_adjust_enter);
            setKeyPanelClickListener(view, R.id.button_adjust_right);
            setKeyPanelClickListener(view, R.id.button_toggle_aeaf);
            setKeyPanelClickListener(view, R.id.lever_ael_caf);
            setKeyPanelClickListener(view, R.id.button_up);
            setKeyPanelClickListener(view, R.id.button_left);
            setKeyPanelClickListener(view, R.id.button_center_enter);
            setKeyPanelClickListener(view, R.id.button_right);
            setKeyPanelClickListener(view, R.id.button_down);
            setKeyPanelClickListener(view, R.id.button_function_1);
            setKeyPanelClickListener(view, R.id.button_function_2);
            setKeyPanelClickListener(view, R.id.button_function_3);
            setKeyPanelClickListener(view, R.id.button_plus);
            setKeyPanelClickListener(view, R.id.button_minus);
            setKeyPanelClickListener(view, R.id.button_playback);
            setKeyPanelClickListener(view, R.id.button_acclock);
            setKeyPanelClickListener(view, R.id.button_lcd_onoff);
            setKeyPanelClickListener(view, R.id.button_highlight);

            connectStatus = view.findViewById(R.id.connect_disconnect_button);
            if (connectStatus != null)
            {
                connectStatus.setOnClickListener(onClickTouchListener);
            }

            View keyPanel = view.findViewById(R.id.showKeyPanelImageView);
            ICameraButtonControl buttonControl = interfaceProvider.getButtonControl();
            if (keyPanel != null)
            {
                keyPanel.setVisibility((buttonControl == null) ? View.INVISIBLE : View.VISIBLE);
                keyPanel.invalidate();
            }
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

    private void setPanelClickListener(View view, int id)
    {
        try
        {
            View button = view.findViewById(id);
            if (button != null)
            {
                button.setOnClickListener(onPanelClickListener);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setKeyPanelClickListener(View view, int id)
    {
        try
        {
            View button = view.findViewById(id);
            if (button != null)
            {
                button.setOnClickListener(onKeyPanelClickListener);
                button.setOnLongClickListener(onKeyPanelClickListener);
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

        this.changeScene = sceneSelector;
        this.interfaceProvider = interfaceProvider;
        this.interfaceInjector = interfaceProvider.getDisplayInjector();
        this.liveViewControl = interfaceProvider.getLiveViewControl();
        this.zoomLensControl = interfaceProvider.getZoomLensControl();
        this.cameraInformation = interfaceProvider.getCameraInformation();
        this.statusWatcher = interfaceProvider.getCameraStatusWatcher();
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
            Activity activity = getActivity();
            if (activity != null)
            {
                if (showGrid == null) {
                    showGrid = activity.findViewById(R.id.show_hide_grid_button);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        int id = (imageView.isShowGrid()) ? R.drawable.ic_grid_off_black_24dp : R.drawable.ic_grid_on_black_24dp;
                        if (showGrid != null)
                        {
                            showGrid.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
                            showGrid.invalidate();
                        }
                        imageView.invalidate();
                    }
                });
            }
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

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null)
        {
            ActionBar bar = activity.getSupportActionBar();
            if (bar != null)
            {
                bar.hide();   // ActionBarの表示を消す
            }
        }

        // 撮影モードかどうかを確認して、撮影モードではなかったら撮影モードに切り替える
        ICameraRunMode changeRunModeExecutor = interfaceProvider.getCameraRunMode();
        if ((changeRunModeExecutor != null)&&(!changeRunModeExecutor.isRecordingMode()))
        {
            // Runモードを切り替える。（でも切り替えると、設定がクリアされてしまう...。）
            changeRunModeExecutor.changeRunMode(true);
        }

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
            boolean isCameraScreen = true;
            if (context != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                liveViewControl.changeLiveViewSize(preferences.getString(IPreferencePropertyAccessor.LIVE_VIEW_QUALITY, IPreferencePropertyAccessor.LIVE_VIEW_QUALITY_DEFAULT_VALUE));
                isCameraScreen = preferences.getBoolean(IPreferencePropertyAccessor.GR2_DISPLAY_CAMERA_VIEW, true);
            }
            ILiveViewListener lvListener = interfaceProvider.getLiveViewListener();
            if (lvListener != null)
            {
                lvListener.setCameraLiveImageView(imageView);
            }
            liveViewControl.startLiveView(isCameraScreen);   // false : ライブビューのみ、 true : カメラ画面をミラー

            // ここでグリッドアイコンを更新する
            updateGridIcon();

            // ここでズームレンズ制御ができるか確認する
            if ((zoomLensControl != null)&&(zoomLensControl.canZoom()))
            {
                //Log.v(TAG, "CAN ZOOM LENS");
                updateZoomlensControl(true);
            }
            else
            {
                //Log.v(TAG, "NO ZOOM LENS");
                updateZoomlensControl(false);
            }


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
            LoadSaveMyCameraPropertyDialog dialog = LoadSaveMyCameraPropertyDialog.newInstance(new LoadSaveCameraProperties(getActivity(), interfaceProvider.getOlympusInterfaceProvider()));
            dialog.show(getChildFragmentManager(), "favorite_dialog");
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
    private void updateZoomlensControl(final boolean isVisible)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // isVisibleがtrueなら、ズームレンズボタンを有効にする
                Activity activity = getActivity();
                if (activity != null)
                {
                    try
                    {
                        View view1 = activity.findViewById(R.id.zoom_out_button);
                        if (view1 != null)
                        {
                            view1.setVisibility((isVisible) ? View.VISIBLE : View.INVISIBLE);
                            view1.invalidate();
                        }

                        View view2 = activity.findViewById(R.id.zoom_in_button);
                        if (view2 != null)
                        {
                            view2.setVisibility((isVisible) ? View.VISIBLE : View.INVISIBLE);
                            view2.invalidate();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
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
            statusWatcher.stopStatusWatch();
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
                    if (view != null)
                    {
                        view.setText(mode);
                        view.invalidate();
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
                    if (view != null) {
                        view.setText(shutterSpeed);
                        view.invalidate();
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
                    if (view != null)
                    {
                        view.setText(apertureValue);
                        view.invalidate();
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
                    if (view != null)
                    {
                        view.setText(xv);
                        view.invalidate();
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
    public void updatedMeteringMode(final String meteringMode)
    {
        try
        {
            Log.v(TAG, "updatedMeteringMode() : " + meteringMode);
            final Activity activity = getActivity();
            if ((activity == null)||(meteringMode == null))
            {
                return;
            }

            int iconId = R.drawable.ic_crop_free_black_24dp;
            switch (meteringMode)
            {
                case ICameraStatus.AE_STATUS_MULTI:
                case ICameraStatus.AE_STATUS_ESP:
                    iconId = R.drawable.ic_crop_free_black_24dp;
                    break;
                case ICameraStatus.AE_STATUS_CENTER:
                case ICameraStatus.AE_STATUS_CENTER2:
                    iconId = R.drawable.ic_center_focus_weak_black_24dp;
                    break;
                case ICameraStatus.AE_STATUS_SPOT:
                case ICameraStatus.AE_STATUS_PINPOINT:
                    iconId = R.drawable.ic_filter_center_focus_black_24dp;
                    break;
            }
            final int id = iconId;
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ImageView view = activity.findViewById(R.id.aeModeImageView);
                    if (view != null)
                    {
                        view.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
                        view.invalidate();
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
    public void updatedWBMode(final String wbMode)
    {
        // とりあえず何もしない... 選択肢は以下 (Ricohの場合...)
        // auto, multiAuto, daylight, shade, cloud, tungsten, warmWhiteFluorescent, daylightFluorescent, dayWhiteFluorescent, coolWhiteFluorescent, incandescent,manual1, cte, custom
    }

    /**
     *   残りバッテリー状態をアイコンで示す
     *
     */
    @Override
    public void updateRemainBattery(final int percentage)
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
                    if (view != null)
                    {
                        Drawable target = ResourcesCompat.getDrawable(getResources(), id, null);
                        if (target != null)
                        {
                            if (percentage <= 20)
                            {
                                DrawableCompat.setTint(target, Color.RED);
                            } else if (percentage <= 40)
                            {
                                DrawableCompat.setTint(target, Color.YELLOW);
                            }
                            view.setImageDrawable(target);
                            view.invalidate();
                        }
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
    public void updateFocusedStatus(final boolean focused, final boolean focusLocked)
    {
        Activity activity = getActivity();
        try
        {
            if (activity != null)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView view = getActivity().findViewById(R.id.focusUnlockImageView);
                        if (focused)
                        {
                            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_center_focus_strong_black_24dp, null);
                            if (icon != null)
                            {
                                DrawableCompat.setTint(icon, Color.GREEN);
                                view.setImageDrawable(icon);
                            }
                        }
                        else
                        {
                            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_focus_free_black_24dp, null);
                            if (icon != null)
                            {
                                int color = Color.BLACK;
                                if (focusLocked)
                                {
                                    color = Color.RED;
                                }
                                DrawableCompat.setTint(icon, color);
                                view.setImageDrawable(icon);
                            }
                        }
                        view.invalidate();
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIsoSensitivity(String sv)
    {
        Log.v(TAG, "updateIsoSensitivity : " + sv);
    }

    @Override
    public void updateWarning(String warning)
    {
        Log.v(TAG, "updateWarning : " + warning);
    }

    @Override
    public void updateStorageStatus(String status)
    {
        Log.v(TAG, "updateStorageStatus : " + status);
    }

    @Override
    public void updateToggleButton(boolean isOn)
    {
        try
        {
            Activity activity = getActivity();
            if (activity != null)
            {
                ImageView imageView = activity.findViewById(R.id.button_toggle_aeaf);
                if (isOn)
                {
                    imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_radio_button_checked_black_24dp));
                }
                else
                {
                    imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_radio_button_unchecked_black_24dp));
                }
                imageView.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLcdOnOff(boolean isOn)
    {
        Log.v(TAG, "updateLcdOnOff() " + isOn);
    }

    @Override
    public void updateAFLlever(boolean isCaf)
    {
        try
        {
            Activity activity = getActivity();
            if (activity != null)
            {
                TextView textView = activity.findViewById(R.id.lever_ael_caf);
                if (isCaf)
                {
                    textView.setText(getString(R.string.label_c_af));
                }
                else
                {
                    textView.setText(getString(R.string.label_aelock));
                }
                textView.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
