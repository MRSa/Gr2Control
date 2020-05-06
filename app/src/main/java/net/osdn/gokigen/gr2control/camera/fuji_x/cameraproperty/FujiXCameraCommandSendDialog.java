package net.osdn.gokigen.gr2control.camera.fuji_x.cameraproperty;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.DialogFragment;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.fuji_x.IFujiXInterfaceProvider;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.IFujiXCommandPublisher;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.CommandGeneric;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.IFujiXCameraCommands;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command.messages.SetPropertyValue;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXCameraModeChangeToLiveView;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.connection.FujiXCameraModeChangeToPlayback;
import net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values.IFujiXCameraProperties;

public class FujiXCameraCommandSendDialog  extends DialogFragment
{
    private final String TAG = toString();
    private Dialog myDialog = null;
    private IFujiXCommandPublisher commandPublisher = null;
    private FujiXCameraCommandResponse responseReceiver = null;
    private SparseArrayCompat<String> commandNameIndexArray;

    private int selectedCommandIdPosition = 0;
    private int selectedMessageTypePosition = 0;
    private int selectedBodyLengthPosition = 0;

    public static FujiXCameraCommandSendDialog newInstance(@NonNull IFujiXInterfaceProvider interfaceProvider)
    {
        FujiXCameraCommandSendDialog instance = new FujiXCameraCommandSendDialog();
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
        this.commandPublisher = interfaceProvider.getCommandPublisher();
        this.commandNameIndexArray = new SparseArrayCompat<>();
    }

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.fujix_request_command_layout, null, false);
        alertDialog.setView(alertView);

        alertDialog.setIcon(R.drawable.ic_linked_camera_black_24dp);
        alertDialog.setTitle(getString(R.string.dialog_fujix_command_title_command));
        try
        {
            final TextView commandResponse = alertView.findViewById(R.id.command_response_value);
            final EditText edit_command_id = alertView.findViewById(R.id.edit_command_id);
            final EditText edit_message_body1 = alertView.findViewById(R.id.edit_message_body1);
            final EditText edit_message_body2 = alertView.findViewById(R.id.edit_message_body2);
            final EditText edit_message_body3 = alertView.findViewById(R.id.edit_message_body3);
            final EditText edit_message_body4 = alertView.findViewById(R.id.edit_message_body4);
            final Spinner selection_command_id = alertView.findViewById(R.id.spinner_selection_command_id);
            final Spinner selection_message_type = alertView.findViewById(R.id.spinner_selection_message_type);
            final Spinner selection_message_body_length = alertView.findViewById(R.id.spinner_selection_message_body_length);
            final Button sendButton = alertView.findViewById(R.id.send_message_button);
            final Button liveViewButton = alertView.findViewById(R.id.change_to_liveview);
            final Button playbackButton = alertView.findViewById(R.id.change_to_playback);

            responseReceiver = new FujiXCameraCommandResponse(activity, commandResponse);

            initializeCommandSelection(activity, selection_command_id, edit_command_id);
            initializeMessageTypeSelection(activity, selection_message_type);
            initializeBodyLengthSelection(activity, selection_message_body_length);

            sendButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        //Log.v(TAG, "SEND COMMAND");
                        if (responseReceiver != null)
                        {
                            responseReceiver.clear();
                            int id = parseInt(edit_command_id);
                            int value1 = parseInt(edit_message_body1);
                            int value2 = parseInt(edit_message_body2);
                            int value3 = parseInt(edit_message_body3);
                            int value4 = parseInt(edit_message_body4);
                            if (selectedMessageTypePosition == 0)
                            {
                                // single type
                                if (selectedBodyLengthPosition == 0)
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id));
                                }
                                else if (selectedBodyLengthPosition == 5)
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id, 16, value1, value2, value3, value4));
                                }
                                else if (selectedBodyLengthPosition == 4)
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id, 12, value1, value2, value3));
                                }
                                else if (selectedBodyLengthPosition == 3)
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id, 8, value1, value2));
                                }
                                else if (selectedBodyLengthPosition == 2)
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id, 4, value1));
                                }
                                else
                                {
                                    commandPublisher.enqueueCommand(new CommandGeneric(responseReceiver, id, 2, value1));
                                }
                            }
                            else
                            {
                                // multi type
                                if (selectedBodyLengthPosition == 0)
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id));
                                }
                                else if (selectedBodyLengthPosition == 5)
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id, 16, value1, value2, value3, value4));
                                }
                                else if (selectedBodyLengthPosition == 4)
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id, 12, value1, value2, value3));
                                }
                                else if (selectedBodyLengthPosition == 3)
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id, 8, value1, value2));
                                }
                                else if (selectedBodyLengthPosition == 2)
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id, 4, value1));
                                }
                                else
                                {
                                    commandPublisher.enqueueCommand(new SetPropertyValue(responseReceiver, id, 2, value1));
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            if ((responseReceiver != null)&&(commandPublisher != null))
            {
                liveViewButton.setOnClickListener(new FujiXCameraModeChangeToLiveView(commandPublisher, responseReceiver));
                playbackButton.setOnClickListener(new FujiXCameraModeChangeToPlayback(commandPublisher, responseReceiver));
            }
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
                        //dialog.dismiss();
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

    private int parseInt(EditText area)
    {
        try
        {
            String value = (area.getText().toString()).toLowerCase();
            int index =  value.indexOf("x");
            if (index > 0)
            {
                value = value.substring(index + 1);
            }
            if (value.length() < 1)
            {
                // 未入力のときには０を返す
                return (0);
            }
            //int convertValue = (int)Long.parseLong(value, 16);
            //Log.v(TAG, String.format(Locale.US, "PARSED VALUE : 0x%08x (%d)", convertValue, convertValue));
            //return (convertValue);
            return ((int)Long.parseLong(value, 16));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v(TAG, "[" + area.getText().toString() + "]");
        }
        return (-1);
    }


    private ArrayAdapter<String> prepareCommandAdapter(@NonNull final Activity activity)
    {
        int position = 0;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item);
        commandNameIndexArray.clear();

        // せっせとコマンドを入れていく...
        adapter.add("(Direct Input)");
        commandNameIndexArray.append(position++, "");

        adapter.add(IFujiXCameraCommands.SHUTTER_STR + " (" + IFujiXCameraCommands.SHUTTER_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.SHUTTER_STR_ID);

        adapter.add(IFujiXCameraCommands.FOCUS_POINT_STR + " (" + IFujiXCameraCommands.FOCUS_POINT_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.FOCUS_POINT_STR_ID);

        adapter.add(IFujiXCameraCommands.FOCUS_UNLOCK_STR + " (" + IFujiXCameraCommands.FOCUS_UNLOCK_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.FOCUS_UNLOCK_STR_ID);

        adapter.add(IFujiXCameraCommands.SHUTTER_SPEED_STR + " (" + IFujiXCameraCommands.SHUTTER_SPEED_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.SHUTTER_SPEED_STR_ID);

        adapter.add(IFujiXCameraCommands.APERTURE_STR + " (" + IFujiXCameraCommands.APERTURE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.APERTURE_STR_ID);

        adapter.add(IFujiXCameraCommands.EXPREV_STR + " (" + IFujiXCameraCommands.EXPREV_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.EXPREV_STR_ID);

        adapter.add(IFujiXCameraProperties.WHITE_BALANCE_STR + " (" + IFujiXCameraProperties.WHITE_BALANCE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.WHITE_BALANCE_STR_ID);

        adapter.add(IFujiXCameraProperties.EXPOSURE_COMPENSATION_STR + " (" + IFujiXCameraProperties.EXPOSURE_COMPENSATION_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.EXPOSURE_COMPENSATION_STR_ID);

        adapter.add(IFujiXCameraProperties.APERTURE_STR + " (" + IFujiXCameraProperties.APERTURE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.APERTURE_STR_ID);

        adapter.add(IFujiXCameraProperties.SHUTTER_SPEED_STR + " (" + IFujiXCameraProperties.SHUTTER_SPEED_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.SHUTTER_SPEED_STR_ID);

        adapter.add(IFujiXCameraProperties.SELF_TIMER_STR + " (" + IFujiXCameraProperties.SELF_TIMER_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.SELF_TIMER_STR_ID);

        adapter.add(IFujiXCameraProperties.FILM_SIMULATION_STR + " (" + IFujiXCameraProperties.FILM_SIMULATION_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.FILM_SIMULATION_STR_ID);

        adapter.add(IFujiXCameraProperties.ISO_STR + " (" + IFujiXCameraProperties.ISO_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.ISO_STR_ID);

        adapter.add(IFujiXCameraProperties.MOVIE_ISO_STR + " (" + IFujiXCameraProperties.MOVIE_ISO_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.MOVIE_ISO_STR_ID);

        adapter.add(IFujiXCameraProperties.IMAGE_FORMAT_STR + " (" + IFujiXCameraProperties.IMAGE_FORMAT_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.IMAGE_FORMAT_STR_ID);

        adapter.add(IFujiXCameraProperties.IMAGE_ASPECT_STR + " (" + IFujiXCameraProperties.IMAGE_ASPECT_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.IMAGE_ASPECT_STR_ID);

        adapter.add(IFujiXCameraProperties.FLASH_STR + " (" + IFujiXCameraProperties.FLASH_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.FLASH_STR_ID);

        adapter.add(IFujiXCameraProperties.F_SS_CONTROL_STR + " (" + IFujiXCameraProperties.F_SS_CONTROL_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.F_SS_CONTROL_STR_ID);

        adapter.add(IFujiXCameraProperties.RECMODE_ENABLE_STR + " (" + IFujiXCameraProperties.RECMODE_ENABLE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.RECMODE_ENABLE_STR_ID);

        adapter.add(IFujiXCameraProperties.BATTERY_LEVEL_STR + " (" + IFujiXCameraProperties.BATTERY_LEVEL_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.BATTERY_LEVEL_STR_ID);

        adapter.add(IFujiXCameraProperties.BATTERY_LEVEL_2_STR + " (" + IFujiXCameraProperties.BATTERY_LEVEL_2_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.BATTERY_LEVEL_2_STR_ID);

        adapter.add(IFujiXCameraProperties.FOCUS_MODE_STR + " (" + IFujiXCameraProperties.FOCUS_MODE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.FOCUS_MODE_STR_ID);

        adapter.add(IFujiXCameraProperties.SHOOTING_MODE_STR + " (" + IFujiXCameraProperties.SHOOTING_MODE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.SHOOTING_MODE_STR_ID);

        adapter.add(IFujiXCameraProperties.FOCUS_POINT_STR + " (" + IFujiXCameraProperties.FOCUS_POINT_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.FOCUS_POINT_STR_ID);

        adapter.add(IFujiXCameraProperties.FOCUS_LOCK_STR + " (" + IFujiXCameraProperties.FOCUS_LOCK_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.FOCUS_LOCK_STR_ID);

        adapter.add(IFujiXCameraProperties.SDCARD_REMAIN_SIZE_STR + " (" + IFujiXCameraProperties.SDCARD_REMAIN_SIZE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.SDCARD_REMAIN_SIZE_STR_ID);

        adapter.add(IFujiXCameraProperties.MOVIE_REMAINING_TIME_STR + " (" + IFujiXCameraProperties.MOVIE_REMAINING_TIME_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.MOVIE_REMAINING_TIME_STR_ID);

        adapter.add(IFujiXCameraProperties.DEVICE_ERROR_STR + " (" + IFujiXCameraProperties.DEVICE_ERROR_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.DEVICE_ERROR_STR_ID);

        adapter.add(IFujiXCameraProperties.IMAGE_FILE_COUNT_STR + " (" + IFujiXCameraProperties.IMAGE_FILE_COUNT_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraProperties.IMAGE_FILE_COUNT_STR_ID);

        adapter.add(IFujiXCameraCommands.CAMERA_CAPABILITIES_STR + " (" + IFujiXCameraCommands.CAMERA_CAPABILITIES_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.CAMERA_CAPABILITIES_STR_ID);

        adapter.add(IFujiXCameraCommands.SINGLE_REQUEST_STR + " (" + IFujiXCameraCommands.SINGLE_REQUEST_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.SINGLE_REQUEST_STR_ID);

        adapter.add(IFujiXCameraCommands.STOP_STR + " (" + IFujiXCameraCommands.STOP_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.STOP_STR_ID);

        adapter.add(IFujiXCameraCommands.IMAGE_INFO_STR + " (" + IFujiXCameraCommands.IMAGE_INFO_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.IMAGE_INFO_STR_ID);

        adapter.add(IFujiXCameraCommands.THUMBNAIL_INDEX_STR + " (" + IFujiXCameraCommands.THUMBNAIL_INDEX_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.THUMBNAIL_INDEX_STR_ID);

        adapter.add(IFujiXCameraCommands.FULL_IMAGE_STR + " (" + IFujiXCameraCommands.FULL_IMAGE_STR_ID + ")");
        commandNameIndexArray.append(position++, IFujiXCameraCommands.FULL_IMAGE_STR_ID);

        adapter.add(IFujiXCameraCommands.LAST_IMAGE_CAMERA_STR + " (" + IFujiXCameraCommands.LAST_IMAGE_CAMERA_STR_ID + ")");
        commandNameIndexArray.append(position, IFujiXCameraCommands.LAST_IMAGE_CAMERA_STR_ID);

        return (adapter);
    }

    private void initializeCommandSelection(@NonNull final Activity activity, final Spinner spinner, final EditText commandIdArea)
    {
        try
        {
            commandIdArea.setText("");
            ArrayAdapter<String> adapter = prepareCommandAdapter(activity);
            spinner.setAdapter(adapter);
            spinner.setSelection(selectedCommandIdPosition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.v(TAG, "onItemSelected : " + position + " (" + id + ")");
                    try
                    {
                        selectedCommandIdPosition = position;
                        commandIdArea.setText(commandNameIndexArray.get(position, ""));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    Log.v(TAG, "onNothingSelected");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initializeMessageTypeSelection(final Activity activity, final Spinner spinner)
    {
        try
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item);
            adapter.add("Command(Single)");
            adapter.add("Property(Multi)");

            spinner.setAdapter(adapter);
            spinner.setSelection(selectedMessageTypePosition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.v(TAG, "onItemSelected : " + position + " (" + id + ")");
                    selectedMessageTypePosition = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    Log.v(TAG, "onNothingSelected");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initializeBodyLengthSelection(final Activity activity, final Spinner spinner)
    {
        try
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item);
            adapter.add("0");
            adapter.add("2");
            adapter.add("4");
            adapter.add("8");
            adapter.add("12");
            adapter.add("16");

            spinner.setAdapter(adapter);
            spinner.setSelection(selectedBodyLengthPosition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.v(TAG, "onItemSelected : " + position + " (" + id + ")");
                    selectedBodyLengthPosition = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    Log.v(TAG, "onNothingSelected");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
