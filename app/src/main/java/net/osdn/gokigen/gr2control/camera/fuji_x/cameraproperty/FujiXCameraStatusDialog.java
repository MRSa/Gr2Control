package net.osdn.gokigen.gr2control.camera.fuji_x.cameraproperty;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.fuji_x.IFujiXInterfaceProvider;

import java.util.List;

public class FujiXCameraStatusDialog extends DialogFragment
{
    private final String TAG = toString();
    private ICameraStatus cameraStatus = null;
    private Dialog myDialog = null;

    public static FujiXCameraStatusDialog newInstance(@NonNull IFujiXInterfaceProvider interfaceProvider)
    {
        FujiXCameraStatusDialog instance = new FujiXCameraStatusDialog();
        instance.prepare(interfaceProvider);

        // パラメータはBundleにまとめておく
        Bundle arguments = new Bundle();
        //arguments.putString("method", method);
        //arguments.putString("message", message);
        instance.setArguments(arguments);

        return (instance);
    }

    private void prepare(@NonNull IFujiXInterfaceProvider interfaceProvider)
    {
        this.cameraStatus = interfaceProvider.getCameraStatusListHolder();
    }

    /**
     *
     *
     */
    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_status_show, null, false);
        alertDialog.setView(alertView);

        alertDialog.setIcon(R.drawable.ic_linked_camera_black_24dp);
        alertDialog.setTitle(getString(R.string.camera_status_title));
        final Button updateButton = alertView.findViewById(R.id.status_update_button);
        final TextView statusTextView = alertView.findViewById(R.id.status_text_view);
        try
        {
            if (updateButton != null)
            {
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            updateStatus(statusTextView);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            updateStatus(statusTextView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        alertDialog.setCancelable(true);

        // ボタンを設定する（実行ボタン）
        alertDialog.setPositiveButton(activity.getString(R.string.dialog_positive_execute),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        // ボタンを設定する (キャンセルボタン）
        alertDialog.setNegativeButton(activity.getString(R.string.dialog_negative_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // 確認ダイアログを応答する
        myDialog = alertDialog.create();
        return (myDialog);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "AlertDialog::onPause()");
        if (myDialog != null)
        {
            myDialog.cancel();
        }
    }

    private void updateStatus(TextView statusTextView)
    {
        try
        {
            String message = "";
            if (cameraStatus == null)
            {
                Log.v(TAG, "STATUS HOLDER IS NULL..");
                return;
            }
            List<String> statusList = cameraStatus.getStatusList("");
            for (String statusName : statusList)
            {
                if (statusName != null)
                {
                    message = message.concat(statusName);
                    message = message + " : " + cameraStatus.getStatus(statusName) + "\n";
                }
            }
            if (statusTextView != null)
            {
                statusTextView.setText(message);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
