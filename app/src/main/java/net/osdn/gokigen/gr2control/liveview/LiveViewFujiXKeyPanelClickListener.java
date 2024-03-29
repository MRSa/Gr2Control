package net.osdn.gokigen.gr2control.liveview;

import android.content.DialogInterface;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraStatus;
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandCallback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.CommandGeneric;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.FinishRecordingMovie;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.IFujiXCameraCommands;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.StartRecordingMovie;
import net.osdn.gokigen.gr2control.camera.utils.SimpleLogDumper;

import java.util.List;

public class LiveViewFujiXKeyPanelClickListener implements View.OnClickListener, View.OnLongClickListener, IFujiXCommandCallback
{
    private final String TAG = toString();
    private final FragmentActivity activity;
    private static final boolean isDumpLog = false;
    private final IInterfaceProvider interfaceProvider;
    private final Vibrator vibrator;
    private boolean isRecordingVideoMovie = false;
    private boolean commandIssued = false;
    private int startMovieSequenceNumber = 0;

    LiveViewFujiXKeyPanelClickListener(@NonNull FragmentActivity activity, @NonNull IInterfaceProvider interfaceProvider, @Nullable Vibrator vibrator)
    {
        this.activity = activity;
        this.interfaceProvider = interfaceProvider;
        this.vibrator = vibrator;
    }

    @Override
    public void onClick(View v)
    {
        boolean isVibrate = true;
        try
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.button_fuji_x_sv_minus:
                    updateValue(IFujiXCameraCommands.SHUTTER_SPEED, 0);
                    break;
                case R.id.button_fuji_x_sv_plus:
                    updateValue(IFujiXCameraCommands.SHUTTER_SPEED, 1);
                    break;
                case R.id.button_fuji_x_tv_minus:
                    updateValue(IFujiXCameraCommands.APERTURE, 0);
                    break;
                case R.id.button_fuji_x_tv_plus:
                    updateValue(IFujiXCameraCommands.APERTURE, 1);
                    break;
                case R.id.button_fuji_x_xv_minus:
                    updateValue(IFujiXCameraCommands.EXPREV, 0);
                    break;
                case R.id.button_fuji_x_xv_plus:
                    updateValue(IFujiXCameraCommands.EXPREV, 1);
                    break;
                case R.id.button_fuji_x_flash:
                    updateSelection(ICameraStatus.FLASH_XV);
                    isVibrate = false;
                    break;
                case R.id.button_fuji_x_timer:
                    updateSelection(ICameraStatus.SELF_TIMER);
                    isVibrate = false;
                    break;
                case R.id.button_fuji_x_video_on_off:
                    startFinishMovie();
                    break;
                default:
                    isVibrate = false;
                    break;
            }
            vibrate(isVibrate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        boolean isVibrate = false;
        boolean ret = false;
        try
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.button_fuji_x_sv_minus:
                    break;
                case R.id.button_fuji_x_sv_plus:
                    break;
                case R.id.button_fuji_x_tv_minus:
                    break;
                case R.id.button_fuji_x_tv_plus:
                    break;
                case R.id.button_fuji_x_xv_minus:
                    break;
                case R.id.button_fuji_x_xv_plus:
                    break;
                case R.id.button_fuji_x_flash:
                    break;
                case R.id.button_fuji_x_timer:
                    break;
                default:
                    break;
            }
            vibrate(isVibrate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
    }

    /**
     *  値を更新する
     *
     */
    private void updateValue(int id, int value)
    {
        try
        {
            IFujiXCommandPublisher publisher = interfaceProvider.getFujiXInterfaceProvider().getCommandPublisher();
            publisher.enqueueCommand(new CommandGeneric(this, id, 4, value));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  選択肢を更新する
     *
     */
    private void updateSelection(@NonNull final String key)
    {
        try
        {
            Log.v(TAG, "updateSelection() : " + key);
            final ICameraStatus statusList = interfaceProvider.getCameraStatusListHolder();
            if (statusList == null)
            {
                // ステータスリストの保持クラスが取れなかった...
                Log.w(TAG, "ICameraStatus is NULL...");
                return;
            }
            final String current = statusList.getStatus(key);
            final List<String> itemList = statusList.getStatusList(key);
            if (itemList.size() <= 0)
            {
                // アイテム（選択肢）が登録されていなければ、何もしない
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // リストから配列に積み替え...
            String[] items = new String[itemList.size()];
            int index = 0;
            for (String item : itemList)
            {
                Log.v(TAG, " (" + index + ") " + item);
                items[index] = item;
                index++;
            }
            int position = itemList.indexOf(current);
            Log.v(TAG, " updateSelection() : " + key + " " + itemList.size() + " " + position);
            builder.setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
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

    /**
     *   ビデオ撮影開始と終了
     *
     */
    private void startFinishMovie()
    {
        try
        {
            if (commandIssued)
            {
                // すでにコマンド発行中。コマンドの発行は抑止する
                Log.v(TAG, " COMMAND IS ALREADY ISSUED...");
                return;
            }
            commandIssued = true;

            IFujiXCommandPublisher publisher = interfaceProvider.getFujiXInterfaceProvider().getCommandPublisher();
            if (isRecordingVideoMovie)
            {
                // 撮影中の場合には、撮影を止める
                publisher.enqueueCommand(new FinishRecordingMovie(new IFujiXCommandCallback() {
                    @Override
                    public void receivedMessage(int id, byte[] rx_body)
                    {
                        commandIssued = false;
                        isRecordingVideoMovie = false;
                        updateMovieRecordingIcon();
                        SimpleLogDumper.dump_bytes(" STOP MOVIE REPLY (" + startMovieSequenceNumber + ") ", rx_body);
                        startMovieSequenceNumber = 0;
                    }

                    @Override
                    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
                    {
                        // 何もしない
                    }

                    @Override
                    public boolean isReceiveMulti()
                    {
                        return (false);
                    }
                }, startMovieSequenceNumber));
            }
            else
            {
                // 撮影を開始する
                publisher.enqueueCommand(new StartRecordingMovie(new IFujiXCommandCallback() {
                    @Override
                    public void receivedMessage(int id, byte[] rx_body)
                    {
                        commandIssued = false;
                        if ((rx_body[7] == (byte) 0x20)&&(rx_body[6] == (byte) 0x01))
                        {
                            // 応答コード OKの場合... SequenceNumberを記憶する
                            startMovieSequenceNumber = ((((int) rx_body[11]) & 0xff) << 24) + ((((int) rx_body[10]) & 0xff) << 16) + ((((int) rx_body[9]) & 0xff) << 8) + (((int) rx_body[8]) & 0xff);
                            isRecordingVideoMovie = true;
                        }
                        updateMovieRecordingIcon();
                        SimpleLogDumper.dump_bytes(" START MOVIE REPLY (" + startMovieSequenceNumber + ") ", rx_body);
                    }

                    @Override
                    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
                    {
                        // 何もしない
                    }

                    @Override
                    public boolean isReceiveMulti()
                    {
                        return (false);
                    }
                }));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            commandIssued = false;
        }
    }

    private void updateMovieRecordingIcon()
    {
        try
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = activity.findViewById(R.id.button_fuji_x_video_on_off);
                    if (isRecordingVideoMovie)
                    {
                        //imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_videocam_off_black_24dp));
                        imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_stop_black_24dp));
                    }
                    else
                    {
                        imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_videocam_black__24dp));
                    }
                    imageView.invalidate();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }





    /**
     *   ぶるぶるさせる
     *
     */
    private void vibrate(boolean isVibrate)
    {
        if ((vibrator != null)&&(isVibrate))
        {
            vibrator.vibrate(30);
        }
    }

    @Override
    public void receivedMessage(int id, byte[] rx_body)
    {
        if (isDumpLog)
        {
            SimpleLogDumper.dump_bytes("" + id, rx_body);
        }
    }

    @Override
    public void onReceiveProgress(int currentBytes, int totalBytes, byte[] rx_body)
    {
        Log.v(TAG, " onReceiveProgress() : " + currentBytes + "/" + totalBytes);
    }

    @Override
    public boolean isReceiveMulti()
    {
        return (false);
    }
}
