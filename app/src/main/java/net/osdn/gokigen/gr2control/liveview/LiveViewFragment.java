package net.osdn.gokigen.gr2control.liveview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraStatusWatcher;
import net.osdn.gokigen.gr2control.camera.IDisplayInjector;
import net.osdn.gokigen.gr2control.camera.IFocusingModeNotify;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;
import net.osdn.gokigen.gr2control.liveview.liveviewlistener.ILiveViewListener;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *  撮影用ライブビュー画面
 *
 */
public class LiveViewFragment extends Fragment implements IStatusViewDrawer, IFocusingModeNotify, IFavoriteSettingDialogKicker, ICameraStatusUpdateNotify
{
    private final String TAG = this.toString();

    private ILiveViewControl liveViewControl = null;
    //private IZoomLensControl zoomLensControl = null;
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

            if (onPanelClickListener == null)
            {
                onPanelClickListener = new LiveViewControlPanelClickListener(activity, interfaceProvider);
            }
            setPanelClickListener(view, R.id.takemodeTextView);
            setPanelClickListener(view, R.id.shutterSpeedTextView);
            setPanelClickListener(view, R.id.apertureValueTextView);
            setPanelClickListener(view, R.id.exposureCompensationTextView);
            setPanelClickListener(view, R.id.aeModeTextView);
            setPanelClickListener(view, R.id.whiteBalanceImageView);
            setPanelClickListener(view, R.id.setEffectImageView);

            if (onKeyPanelClickListener == null)
            {
                onKeyPanelClickListener = new LiveViewKeyPanelClickListener(interfaceProvider, vibrator);
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
            if (connectStatus != null)
            {
                connectStatus.setOnClickListener(onClickTouchListener);
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
        //this.zoomLensControl = interfaceProvider.getZoomLensControl();
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

/*
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
                    if (view != null)
                    {
                        view.setText(meteringMode);
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
        // とりあえず何もしない... 選択肢は以下
        // auto, multiAuto, daylight, shade, cloud, tungsten, warmWhiteFluorescent, daylightFluorescent, dayWhiteFluorescent, coolWhiteFluorescent, incandescent,manual1, cte, custom
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
}
