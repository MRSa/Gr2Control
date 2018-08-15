package net.osdn.gokigen.gr2control.liveview;

import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraButtonControl;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;

/**
 *
 *
 */
class LiveViewKeyPanelClickListener  implements View.OnClickListener
{
    private final String TAG = toString();
    private final IInterfaceProvider interfaceProvider;
    private final KeyPanelFeedback feedback;
    private final Vibrator vibrator;

    private boolean lcdOffOn = false;
    private boolean lightOnOff = false;
    private boolean accLock = false;
    private boolean toggleButton = false;
    private boolean leverAfl = false;

    interface KeyPanelFeedback
    {
        void updateToggleButton(boolean isOn);
        void updateLcdOnOff(boolean isOn);
        void updateAFLlever(boolean isCaf);
    };

    LiveViewKeyPanelClickListener(@NonNull IInterfaceProvider interfaceProvider, @Nullable KeyPanelFeedback feedback, @Nullable Vibrator vibrator)
    {
        this.interfaceProvider = interfaceProvider;
        this.feedback = feedback;
        this.vibrator = vibrator;
    }

    @Override
    public void onClick(View view)
    {
        try
        {
            String keyId = "";
            int id = view.getId();
            switch (id)
            {
                case R.id.button_front_left:
                    // フロントダイアル左
                    keyId = ICameraButtonControl.FRONT_LEFT;
                    break;
                case R.id.button_front_right:
                    // フロントダイアル右
                    keyId = ICameraButtonControl.FRONT_RIGHT;
                    break;
                case R.id.button_adjust_left:
                    // ADJボタン左
                    keyId = ICameraButtonControl.ADJ_LEFT;
                    break;
                case R.id.button_adjust_enter:
                    // ADJボタン押下
                    keyId = ICameraButtonControl.ADJ_ENTER;
                    break;
                case R.id.button_adjust_right:
                    // ADJボタン右
                    keyId = ICameraButtonControl.ADJ_RIGHT;
                    break;
                case R.id.button_toggle_aeaf:
                    // AE/AFのトグルボタン
                    keyId = decideToggle();
                    break;
                case R.id.lever_ael_caf:
                    // AEL/AFL - C-AF レバー
                    keyId = decideLever();
                    break;
                case R.id.button_up:
                    // 上ボタン
                    keyId = ICameraButtonControl.BUTTON_UP;
                    break;
                case R.id.button_left:
                    // 左ボタン
                    keyId = ICameraButtonControl.BUTTON_LEFT;
                    break;
                case R.id.button_center_enter:
                    // OKボタン
                    keyId = ICameraButtonControl.BUTTON_ENTER;
                    break;
                case R.id.button_right:
                    // 右ボタン
                    keyId = ICameraButtonControl.BUTTON_RIGHT;
                    break;
                case R.id.button_down:
                    // 下ボタン
                    keyId = ICameraButtonControl.BUTTON_DOWN;
                    break;
                case R.id.button_function_1:
                    // Fn1ボタン
                    keyId = ICameraButtonControl.BUTTON_FUNCTION_1;
                    break;
                case R.id.button_function_2:
                    // Fn2ボタン
                    keyId = ICameraButtonControl.BUTTON_FUNCTION_2;
                    break;
                case R.id.button_function_3:
                    // Fn3ボタン
                    keyId = ICameraButtonControl.BUTTON_FUNCTION_3;
                    break;
                case R.id.button_plus:
                    // +ボタン
                    keyId = ICameraButtonControl.BUTTON_PLUS;
                    break;
                case R.id.button_minus:
                    // -ボタン
                    keyId = ICameraButtonControl.BUTTON_MINUS;
                    break;
                case R.id.button_playback:
                    // プレイボタン
                    keyId = ICameraButtonControl.BUTTON_PLAYBACK;
                    break;
                case R.id.button_acclock:
                    // アクセサリーロックボタン
                    keyId = decideAccLock();
                    break;
                case R.id.button_highlight:
                    // ライトオンオフボタン
                    keyId = decideLightOnOff();
                    break;
                case R.id.button_lcd_onoff:
                    // LCDオンオフボタン
                    keyId = decideLCDOnOff();
                    break;
                default:
                    Log.v(TAG, "onClick() : " + id);
                    break;
            }
            if (keyId.length() > 1)
            {
                ICameraButtonControl buttonControl = interfaceProvider.getButtonControl();
                if (buttonControl != null)
                {
                    buttonControl.pushedButton(keyId);
                    if (vibrator != null)
                    {
                        vibrator.vibrate(30);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String decideAccLock()
    {
        // アクセサリーロック・ロック解除ボタン
        accLock = !accLock;
        return ((accLock) ? "acclock on" : "acclock off");
    }

    private String decideLightOnOff()
    {
        // Light ON/OFFボタン
        lightOnOff = !lightOnOff;
        return ((lightOnOff) ? "led on 1" : "led off 1");
    }

    private String decideLCDOnOff()
    {
        // LCD ON/OFFボタン
        lcdOffOn = !lcdOffOn;
        if (feedback != null)
        {
            feedback.updateLcdOnOff(lcdOffOn);
        }
        return ((lcdOffOn) ? "lcd sleep on" : "lcd sleep off");
    }

    private String decideToggle()
    {
        // AEL/AFL ボタン状態から次のボタン状態指示を決める
        // あわせて、ボタン状態の表示更新を行う
        toggleButton = !toggleButton;
        if (feedback != null)
        {
            feedback.updateToggleButton(toggleButton);
        }
        return ((toggleButton) ? "baf 1" : "baf 0");
    }

    private String decideLever()
    {
        // AEL/AFL - C-AF レバー状態から、次のレバー状態指示を決める。
        // あわせて、ボタン状態の表示更新を行う
        leverAfl = !leverAfl;
        if (feedback != null)
        {
            feedback.updateAFLlever(leverAfl);
        }
        return ((leverAfl) ? "bafl" : "bafc");
    }
}
