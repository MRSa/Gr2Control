package net.osdn.gokigen.gr2control.liveview;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;

import java.util.List;

/**
 *
 *
 */
class LiveViewControlPanelClickListener  implements View.OnClickListener
{
    private final String TAG = toString();
    private final Activity activity;
    private final IInterfaceProvider interfaceProvider;

    LiveViewControlPanelClickListener(Activity context, IInterfaceProvider interfaceProvider)
    {
        this.activity = context;
        this.interfaceProvider = interfaceProvider;
    }

    @Override
    public void onClick(View view)
    {
        try
        {
            int id = view.getId();
            ICameraStatus statusList = interfaceProvider.getCameraStatusListHolder();
            if (statusList == null)
            {
                // ステータスリストの保持クラスが取れなかった...
                Log.w(TAG, "ICameraStatus is NULL...");
                return;
            }
            switch (id)
            {
                case R.id.takemodeTextView:
                    selectTakeMode(statusList);
                    break;

                case R.id.shutterSpeedTextView:
                    selectShutterSpeed(statusList);
                    break;

                case R.id.apertureValueTextView:
                    selectAperture(statusList);
                    break;

                case R.id.exposureCompensationTextView:
                    selectExposureCompensation(statusList);
                    break;

                case R.id.aeModeImageView:
                    selectAeMode(statusList);
                    break;

                case R.id.whiteBalanceTextView:
                    selectWhiteBalance(statusList);
                    break;

                case R.id.isoSensitivityTextView:
                    selectIsoSensitivity(statusList);
                    break;

                case R.id.setEffectImageView:
                    selectEffect(statusList);
                    break;

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

    private void selectTakeMode(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectTakeMode()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.TAKE_MODE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectShutterSpeed(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectShutterSpeed()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.SHUTTER_SPEED);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void selectAperture(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectAperture()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.APERATURE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void selectExposureCompensation(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectExposureCompensation()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.EXPREV);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void selectAeMode(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectAeMode()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.AE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectWhiteBalance(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectWhiteBalance()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.WHITE_BALANCE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectIsoSensitivity(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectIsoSensitivity()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.ISO_SENSITIVITY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectEffect(@NonNull ICameraStatus statusList)
    {
        Log.v(TAG,"selectWhiteBalance()");
        try
        {
            choiceStatusList(statusList, ICameraStatus.EFFECT);
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
    private void choiceStatusList(@NonNull final ICameraStatus statusList, final String key)
    {
        try
        {
            final String current = statusList.getStatus(key);
            final List<String> itemList = statusList.getStatusList(key);

            if (itemList.size() <= 0)
            {
                // アイテム（選択肢）が登録されていなければ、何もしない
                return;
            }

            // しかし、ここ、ちょーダサいんだけど...
            String[] items = new String[itemList.size()];
            for (int ii = 0; ii < items.length; ++ii)
            {
                items[ii] = itemList.get(ii);
                // ついでにもうべたべたで...ここで表示用の文字列を置き換える
                //  (注: itemsだけ置き換え、itemList と current は 内部値のままとなっている
                if (key.equals(ICameraStatus.SHUTTER_SPEED))
                {
                    items[ii] = items[ii].replace(".", "/");
                }
                else if (key.equals(ICameraStatus.APERATURE))
                {
                    items[ii] = "F" + items[ii];
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setSingleChoiceItems(items, itemList.indexOf(current), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    String choice = itemList.get(i);
                    Log.v(TAG, key + " ITEM CHOICED : " + choice + "(CURRENT : " + current + ")");

                    statusList.setStatus(key, choice);
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
