package net.osdn.gokigen.gr2control.liveview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraConnection;
import net.osdn.gokigen.gr2control.camera.ICameraInformation;
import net.osdn.gokigen.gr2control.camera.ICaptureControl;
import net.osdn.gokigen.gr2control.camera.IFocusingControl;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.IZoomLensControl;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.ConfirmationDialog;
import net.osdn.gokigen.gr2control.scene.IChangeScene;


/**
 *
 *
 */
class LiveViewClickTouchListener implements View.OnClickListener, View.OnTouchListener
{
    private final String TAG = toString();
    private final Activity context;
    private final ILiveImageStatusNotify statusNotify;
    //private final IStatusViewDrawer statusViewDrawer;
    private final IChangeScene changeScene;
    private final IInterfaceProvider interfaceProvider;
    private final IFocusingControl focusingControl;
    private final ICaptureControl captureControl;
    //private final IOlyCameraPropertyProvider propertyProvider;
    //private final ICameraInformation cameraInformation;
    private final ICameraConnection cameraConnection;
    private final IFavoriteSettingDialogKicker dialogKicker;
    private final IZoomLensControl zoomLensControl;

    LiveViewClickTouchListener(Activity context, ILiveImageStatusNotify imageStatusNotify, IStatusViewDrawer statusView, IChangeScene changeScene, IInterfaceProvider interfaceProvider, IFavoriteSettingDialogKicker dialogKicker)
    {
        this.context = context;
        this.statusNotify = imageStatusNotify;
        //this.statusViewDrawer = statusView;
        this.changeScene = changeScene;
        this.interfaceProvider = interfaceProvider;

        //ICameraConnection.CameraConnectionMethod connectionMethod = interfaceProvider.getCammeraConnectionMethod();
        //if (connectionMethod == ICameraConnection.CameraConnectionMethod.RICOH_GR2)
        {
            this.focusingControl = interfaceProvider.getRicohGr2Infterface().getFocusingControl();
            this.captureControl = interfaceProvider.getRicohGr2Infterface().getCaptureControl();
            //this.propertyProvider = interfaceProvider.getOlympusInterface().getCameraPropertyProvider();  // 要変更
            //this.cameraInformation = interfaceProvider.getRicohGr2Infterface().getCameraInformation();
            this.cameraConnection = interfaceProvider.getRicohGr2Infterface().getRicohGr2CameraConnection();
            this.zoomLensControl = interfaceProvider.getRicohGr2Infterface().getZoomLensControl();
        }
/*
        else if (connectionMethod == ICameraConnection.CameraConnectionMethod.SONY)
        {
            this.focusingControl = interfaceProvider.getSonyInterface().getFocusingControl();
            this.captureControl = interfaceProvider.getSonyInterface().getCaptureControl();
            this.propertyProvider = interfaceProvider.getOlympusInterface().getCameraPropertyProvider();  // 要変更
            this.cameraInformation = interfaceProvider.getSonyInterface().getCameraInformation();
            this.cameraConnection = interfaceProvider.getSonyInterface().getSonyCameraConnection();
            this.zoomLensControl = interfaceProvider.getSonyInterface().getZoomLensControl();
        }
        else  // if (connectionMethod == ICameraConnection.CameraConnectionMethod.OPC)
        {
            this.focusingControl = interfaceProvider.getOlympusInterface().getFocusingControl();
            this.captureControl = interfaceProvider.getOlympusInterface().getCaptureControl();
            this.propertyProvider = interfaceProvider.getOlympusInterface().getCameraPropertyProvider();
            this.cameraInformation = interfaceProvider.getOlympusInterface().getCameraInformation();
            this.cameraConnection = interfaceProvider.getOlympusInterface().getOlyCameraConnection();
            this.zoomLensControl = interfaceProvider.getOlympusInterface().getZoomLensControl();
        }
*/
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
        //Log.v(TAG, "onClick() : " + id);
        try
        {
            switch (id)
            {
                case R.id.hideControlPanelTextView:
                    // 制御パネルを隠す
                    showHideControlPanel(false);
                    break;

                case R.id.showControlPanelTextView:
                    // 制御パネルを表示する
                    showHideControlPanel(true);
                    break;

                case R.id.showKeyPanelImageView:
                    // キーパネルを表示する
                    showHideKeyPanel(true);
                    break;

                case R.id.hideKeyPanelTextView:
                    // キーパネルを隠す
                    showHideKeyPanel(false);
                    break;

                case R.id.connect_disconnect_button:
                    // カメラと接続・切断のボタンが押された
                    changeScene.changeCameraConnection();
                    break;

                case R.id.shutter_button:
                    // シャッターボタンが押された (撮影)
                    pushedShutterButton();
                    break;

                case R.id.focusUnlockImageView:
                    // フォーカスアンロックボタンが押された
                    pushedFocusUnlock();
                    break;

                case R.id.show_images_button:
                    // 画像一覧表示ボタンが押された...画像一覧画面を開く
                    changeScene.changeScenceToImageList();
                    break;

                case R.id.camera_power_off_button:
                    // 電源ボタンが押された...終了してよいか確認して、終了する
                    confirmExitApplication();
                    break;

                case R.id.show_preference_button:
                    // カメラの設定
                    changeScene.changeSceneToConfiguration();
                    break;


                    /*
                case R.id.show_hide_grid_button:
                    // グリッドの ON/OFF
                    statusNotify.toggleShowGridFrame();
                    statusViewDrawer.updateGridIcon();
                    break;

                case R.id.camera_property_settings_button:
                    // カメラのプロパティ設定
                    changeScene.changeSceneToCameraPropertyList();
                    break;

                case R.id.focusing_button:
                    // AF と MFの切り替えボタンが押された
                    changeFocusingMode();
                    break;

                case R.id.live_view_scale_button:
                    //  ライブビューの倍率を更新する
                    statusViewDrawer.updateLiveViewScale(true);
                    break;

                case R.id.show_favorite_settings_button:
                    // お気に入り設定のダイアログを表示する
                    showFavoriteDialog();
                    break;

                case R.id.btn_zoomin:
                    // ズームインのボタンが押された
                    actionZoomin();
                    break;
                case R.id.btn_zoomout:
                    // ズームアウトのボタンが押された
                    actionZoomout();
                    break;
*/
                default:
                    Log.v(TAG, "onClick() : " + id);
                    break;
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
        try
        {
            View target = context.findViewById(R.id.keyPanelLayout);
            View target2 = context.findViewById(R.id.showKeyPanelImageView);
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
     *   お気に入り設定ダイアログの表示
     *
     */
    private void showFavoriteDialog()
    {
        Log.v(TAG, "showFavoriteDialog()");
        try
        {
            if (interfaceProvider.getCammeraConnectionMethod() != ICameraConnection.CameraConnectionMethod.OPC)
            {
                // OPCカメラでない場合には、「OPCカメラのみ有効です」表示をして画面遷移させない
                Toast.makeText(context, context.getText(R.string.only_opc_feature), Toast.LENGTH_SHORT).show();
                return;
            }

            if (cameraConnection.getConnectionStatus() == ICameraConnection.CameraConnectionStatus.CONNECTED)
            {
                //  お気に入り設定のダイアログを表示する
                dialogKicker.showFavoriteSettingDialog();
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
}
