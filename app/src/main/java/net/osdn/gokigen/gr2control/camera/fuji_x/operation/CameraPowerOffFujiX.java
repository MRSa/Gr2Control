package net.osdn.gokigen.gr2control.camera.fuji_x.operation;

import android.content.Context;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor;
import net.osdn.gokigen.gr2control.scene.ConfirmationDialog;
import net.osdn.gokigen.gr2control.scene.IChangeScene;

import androidx.preference.Preference;

public class CameraPowerOffFujiX implements Preference.OnPreferenceClickListener, ConfirmationDialog.Callback
{

    private final Context context;
    private final IChangeScene changeScene;
    private String preferenceKey = null;

    /**
     *   コンストラクタ
     *
     */
    public CameraPowerOffFujiX(Context context, IChangeScene changeScene)
    {
        this.context = context;
        this.changeScene = changeScene;
    }

    /**
     *   クラスの準備
     *
     */
    public void prepare()
    {
        // 何もしない
    }

    /**
     *
     *
     * @param preference クリックしたpreference
     * @return false : ハンドルしない / true : ハンドルした
     */
    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (!preference.hasKey())
        {
            return (false);
        }

        preferenceKey = preference.getKey();
        if (preferenceKey.contains(IPreferencePropertyAccessor.EXIT_APPLICATION))
        {

            // 確認ダイアログの生成と表示
            ConfirmationDialog dialog = ConfirmationDialog.newInstance(context);
            dialog.show(R.string.dialog_title_confirmation, R.string.dialog_message_exit, this);
            return (true);
        }
        return (false);
    }

    /**
     *
     *
     */
    @Override
    public void confirm()
    {
        if (preferenceKey.contains(IPreferencePropertyAccessor.EXIT_APPLICATION))
        {
            // アプリケーションを終了する。
            changeScene.exitApplication();
        }
    }
}
