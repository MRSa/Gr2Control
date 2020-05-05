package net.osdn.gokigen.gr2control.liveview;

import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.camera.fuji_x.cameraproperty.FujiXCameraCommandSendDialog;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.ConfirmationDialog;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *
 *
 */
class LiveViewClickTouchListener implements View.OnClickListener, View.OnTouchListener, View.OnKeyListener
{
    private final String TAG = toString();
    private final FragmentActivity context;
    private final ILiveImageStatusNotify statusNotify;
    private final IStatusViewDrawer statusViewDrawer;
    private final IChangeScene changeScene;
    private final IInterfaceProvider interfaceProvider;
    private final IFocusingControl focusingControl;
    private final ICaptureControl captureControl;
    //private final IOlyCameraPropertyProvider propertyProvider;
    //private final ICameraInformation cameraInformation;
    private final ICameraConnection cameraConnection;
    private final IFavoriteSettingDialogKicker dialogKicker;
    private final IZoomLensControl zoomLensControl;

    LiveViewClickTouchListener(@NonNull FragmentActivity context, @NonNull ILiveImageStatusNotify imageStatusNotify, @NonNull IStatusViewDrawer statusView, @NonNull IChangeScene changeScene, @NonNull IInterfaceProvider interfaceProvider, @NonNull IFavoriteSettingDialogKicker dialogKicker)
    {
        this.context = context;
        this.statusNotify = imageStatusNotify;
        this.statusViewDrawer = statusView;
        this.changeScene = changeScene;
        this.interfaceProvider = interfaceProvider;

        this.focusingControl = interfaceProvider.getFocusingControl();
        this.captureControl = interfaceProvider.getCaptureControl();
        this.cameraConnection = interfaceProvider.getCameraConnection();
        this.zoomLensControl = interfaceProvider.getZoomLensControl();

        this.dialogKicker = dialogKicker;
    }

    /**
     *   オブジェクトをクリックする処理
     *
     */
    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        boolean isVibrate;
        //Log.v(TAG, "onClick() : " + id);
        try
        {
            switch (id)
            {
                case R.id.hideControlPanelTextView:
                    // 制御パネルを隠す
                    showHideControlPanel(false);
                    isVibrate = false;
                    break;

                case R.id.showControlPanelTextView:
                    // 制御パネルを表示する
                    showHideControlPanel(true);
                    isVibrate = false;
                    break;

                case R.id.showKeyPanelImageView:
                    // キーパネルを表示する
                    showHideKeyPanel(true);
                    isVibrate = true;
                    break;

                case R.id.hideKeyPanelTextView:
                case R.id.fuji_x_hideKeyPanelTextView:
                    // キーパネルを隠す
                    showHideKeyPanel(false);
                    isVibrate = true;
                    break;

                case R.id.connect_disconnect_button:
                    // カメラと接続・切断のボタンが押された
                    changeScene.changeCameraConnection();
                    isVibrate = true;
                    break;

                case R.id.shutter_button:
                    // シャッターボタンが押された (撮影)
                    pushedShutterButton();
                    isVibrate = false;
                    break;

                case R.id.focusUnlockImageView:
                    // フォーカスアンロックボタンが押された
                    pushedFocusUnlock();
                    isVibrate = false;
                    break;

                case R.id.show_images_button:
                    // 画像一覧表示ボタンが押された...画像一覧画面を開く
                    changeScene.changeScenceToImageList();
                    isVibrate = true;
                    break;

                case R.id.camera_power_off_button:
                    // 電源ボタンが押された...終了してよいか確認して、終了する
                    confirmExitApplication();
                    isVibrate = true;
                    break;

                case R.id.show_preference_button:
                    // カメラの設定
                    changeScene.changeSceneToConfiguration(ICameraConnection.CameraConnectionMethod.UNKNOWN);
                    isVibrate = true;
                    break;

                case R.id.show_hide_grid_button:
                    // グリッドの ON/OFF
                    statusNotify.toggleShowGridFrame();
                    statusViewDrawer.updateGridIcon();
                    isVibrate = false;
                    break;
                case R.id.zoom_in_button:
                    // ズームインのボタンが押された
                    actionZoomin();
                    isVibrate = false;
                    break;

                case R.id.zoom_out_button:
                    // ズームアウトのボタンが押された
                    actionZoomout();
                    isVibrate = false;
                    break;

                case R.id.specialButtonImageView:
                    // スペシャルボタンが押された
                    pushedSpecialButton();
                    isVibrate = false;
                    break;
/*
                case R.id.camera_property_settings_button:
                    // カメラのプロパティ設定
                    changeScene.changeSceneToCameraPropertyList();
                    isVibrate = false;
                    break;

                case R.id.focusing_button:
                    // AF と MFの切り替えボタンが押された
                    changeFocusingMode();
                    isVibrate = false;
                    break;

                case R.id.live_view_scale_button:
                    //  ライブビューの倍率を更新する
                    statusViewDrawer.updateLiveViewScale(true);
                    isVibrate = false;
                    break;

                case R.id.show_favorite_settings_button:
                    // お気に入り設定のダイアログを表示する
                    showFavoriteDialog();
                    isVibrate = false;
                    break;
*/
                default:
                    Log.v(TAG, "onClick() : " + id);
                    isVibrate = false;
                    break;
            }
            if (isVibrate)
            {
                vibrate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   コントロールパネルの表示・非表示
     *
     */
    private void showHideControlPanel(boolean isShow)
    {
        try
        {
            View target = context.findViewById(R.id.controlPanelLayout);
            View target2 = context.findViewById(R.id.showControlPanelTextView);
            if (target != null)
            {
                target.setVisibility((isShow) ? View.VISIBLE : View.INVISIBLE);
                target.invalidate();
                if (target2 != null)
                {
                    target2.setVisibility((isShow) ? View.INVISIBLE : View.VISIBLE);
                    target2.invalidate();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   キー操作パネルの表示・非表示
     *
     */
    private void showHideKeyPanel(boolean isShow)
    {
        View target = null;
        View target2 = null;
        View target3 = null;
        try
        {
            ICameraConnection.CameraConnectionMethod connectionMethod = interfaceProvider.getCammeraConnectionMethod();
            if (connectionMethod == ICameraConnection.CameraConnectionMethod.FUJI_X)
            {
                // FUJI X モード
                target3 = context.findViewById(R.id.keyPanelLayout);
                target2 = context.findViewById(R.id.showKeyPanelImageView);
                target = context.findViewById(R.id.fuji_x_keyPanelLayout);
            }
            else
            {
                // FUJI Xモード以外 (GR2 / Olympus)
                target = context.findViewById(R.id.keyPanelLayout);
                target2 = context.findViewById(R.id.showKeyPanelImageView);
                target3 = context.findViewById(R.id.fuji_x_keyPanelLayout);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if (target != null)
            {
                target.setVisibility((isShow) ? View.VISIBLE : View.INVISIBLE);
                target.invalidate();
            }
            if (target2 != null)
            {
                target2.setVisibility((isShow) ? View.INVISIBLE : View.VISIBLE);
                target2.invalidate();
            }
            if (target3 != null)
            {
                target3.setVisibility(View.GONE);
                target3.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void confirmExitApplication()
    {
        try
        {
            // 確認ダイアログの生成と表示
            ConfirmationDialog dialog = ConfirmationDialog.newInstance(context);
            dialog.show(R.string.dialog_title_confirmation, R.string.dialog_message_power_off, new ConfirmationDialog.Callback() {
                @Override
                public void confirm()
                {
                    changeScene.exitApplication();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void actionZoomin()
    {
        Log.v(TAG, "actionZoomin()");
        try
        {
            // ズーム可能な場合、ズームインする
            if (zoomLensControl.canZoom())
            {
                zoomLensControl.driveZoomLens(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void actionZoomout()
    {
        Log.v(TAG, "actionZoomout()");
        try
        {
            // ズーム可能な場合、ズームアウトする
            if (zoomLensControl.canZoom())
            {
                zoomLensControl.driveZoomLens(false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *   シャッターボタンが押された時の処理
     *
     *
     */
    private void pushedShutterButton()
    {
        Log.v(TAG, "pushedShutterButton()");
        try
        {
            // カメラで撮影する
            captureControl.doCapture(0);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, true))
            {
                // ライブビュー画像も保管する
                statusNotify.takePicture();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   フォーカスアンロックボタンが押された時の処理
     *
     */
    private void pushedFocusUnlock()
    {
        Log.v(TAG, "pushedFocusUnlock()");
        try
        {
            // フォーカスアンロックする
            focusingControl.unlockAutoFocus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   スペシャルボタンが押された時の処理
     *
     */
    private void pushedSpecialButton()
    {
        showFavoriteDialog();
    }

    /**
     *   お気に入り設定ダイアログの表示
     *
     */
    private void showFavoriteDialog()
    {
        Log.v(TAG, "showFavoriteDialog()");
        try
        {
            if (cameraConnection.getConnectionStatus() != ICameraConnection.CameraConnectionStatus.CONNECTED)
            {
                // カメラと接続されていない時には、何もしない
                return;
            }

            if (interfaceProvider.getCammeraConnectionMethod() == ICameraConnection.CameraConnectionMethod.OPC)
            {
                //  OPCカメラの場合には、お気に入り設定のダイアログを表示する
                dialogKicker.showFavoriteSettingDialog();
                return;
            }
            else if (interfaceProvider.getCammeraConnectionMethod() == ICameraConnection.CameraConnectionMethod.FUJI_X)
            {
                try
                {
                    // FUJI X Seriesの場合は、コマンド送信ダイアログを表示する
                    FujiXCameraCommandSendDialog.newInstance(interfaceProvider.getFujiXInterfaceProvider()).show(context.getSupportFragmentManager(), "sendCommandDialog");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return;
            }

            ICameraButtonControl btnCtl = interfaceProvider.getButtonControl();
            if (btnCtl != null)
            {
                // 'GREEN' ボタンが押されたこととする
                btnCtl.pushedButton(ICameraButtonControl.SPECIAL_GREEN_BUTTON, false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   オブジェクトをタッチする処理
     *
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        int id = view.getId();
        if (focusingControl == null)
        {
            Log.v(TAG, "focusingControl is NULL.");
            view.performClick();  // ダミー処理...
            return (false);
        }
        Log.v(TAG, "onTouch() : " + id + " (" + motionEvent.getX() + "," + motionEvent.getY() + ")");
        return ((id == R.id.cameraLiveImageView)&&(focusingControl.driveAutoFocus(motionEvent)));
    }

    /**
     *   ボタンを押したときの対応
     *
     */
    @Override
    public boolean onKey(View view, int keyCode, @NonNull KeyEvent keyEvent)
    {
        Log.v(TAG, "onKey() : " + keyCode);
        try
        {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&
                    ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)))
            {
                pushedShutterButton();
                return (true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }


    /**
     *
     *
     */
    private void vibrate()
    {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null)
            {
                vibrator.vibrate(50);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
