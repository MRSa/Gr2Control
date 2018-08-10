package net.osdn.gokigen.gr2control.liveview;

import android.support.annotation.NonNull;
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

    LiveViewKeyPanelClickListener(@NonNull IInterfaceProvider interfaceProvider)
    {
        this.interfaceProvider = interfaceProvider;
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
                default:
                    Log.v(TAG, "onClick() : " + id);
                    break;
            }
            if (keyId.length() > 1)
            {
                ICameraButtonControl buttonControl = interfaceProvider.getRicohGr2Infterface().getButtonControl();
                if (buttonControl != null)
                {
                    buttonControl.pushedButton(keyId);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String decideToggle()
    {
        // AEL/AFL ボタン状態から次のボタン状態指示を決める
        // あわせて、ボタン状態の表示更新を行う
        return ("");
    }

    private String decideLever()
    {
        // AEL/AFL - C-AF レバー状態から、次のレバー状態指示を決める。
        // あわせて、ボタン状態の表示更新を行う
        return ("");
    }
}
