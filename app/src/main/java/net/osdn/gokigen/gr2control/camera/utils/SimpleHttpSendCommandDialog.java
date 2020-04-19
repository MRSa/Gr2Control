package net.osdn.gokigen.gr2control.camera.utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ILiveViewControl;

import java.util.Map;

public class SimpleHttpSendCommandDialog extends DialogFragment implements View.OnClickListener
{
    private final String TAG = toString();
    private ILiveViewControl liveViewControl = null;
    private Dialog myDialog = null;
    private EditText http_header = null;
    private EditText method = null;
    private EditText service = null;
    private EditText parameter = null;
    private EditText command = null;
    private TextView responseArea = null;
    private String urlToSend = null;
    private Map<String, String> headerMap;

    private static final int TIMEOUT_MS = 6000;
    private static final String COMMUNICATE_URL_DEFAULT = "http://192.168.0.10/";

    /**
     *
     *
     */
    public static SimpleHttpSendCommandDialog newInstance(@Nullable String urlToSend, @Nullable ILiveViewControl liveViewControl,  @Nullable Map<String, String> headerMap)
    {
        SimpleHttpSendCommandDialog instance = new SimpleHttpSendCommandDialog();
        instance.prepare(urlToSend, liveViewControl, headerMap);

        // パラメータはBundleにまとめておく
        Bundle arguments = new Bundle();
        //arguments.putString("method", method);
        //arguments.putString("message", message);
        instance.setArguments(arguments);

        return (instance);
    }

    /**
     *
     *
     */
    private void prepare(@Nullable String urlToSend, @Nullable ILiveViewControl liveViewControl, @Nullable Map<String, String> headerMap)
    {
        if ((urlToSend == null)||(!urlToSend.contains("http://")))
        {
            this.urlToSend = COMMUNICATE_URL_DEFAULT;
        }
        else
        {
            this.urlToSend = urlToSend;
        }

        //
        this.liveViewControl = liveViewControl;

        this.headerMap = headerMap;
        //headerMap = new HashMap<>();
        //headerMap.put("User-Agent", "OlympusCameraKit"); // "OI.Share"
        //headerMap.put("X-Protocol", "OlympusCameraKit"); // "OI.Share"
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "AlertDialog::onPause()");
        try
        {
            if (myDialog != null)
            {
                myDialog.cancel();
            }
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
    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();

        // コマンド送信ダイアログの生成
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.http_request_layout, null, false);
        alertDialog.setView(alertView);

        alertDialog.setIcon(R.drawable.ic_linked_camera_black_24dp);
        alertDialog.setTitle(activity.getString(R.string.dialog_http_command_title_command));
        http_header = alertView.findViewById(R.id.edit_http_header);
        method = alertView.findViewById(R.id.edit_method);
        service = alertView.findViewById(R.id.edit_service);
        parameter = alertView.findViewById(R.id.edit_parameter);
        command = alertView.findViewById(R.id.edit_command);
        responseArea = alertView.findViewById(R.id.olympuspen_command_response_value);
        final Button sendButton = alertView.findViewById(R.id.send_message_button);
        final Button toRunningButton = alertView.findViewById(R.id.change_to_liveview);
        final Button toPlaybackButton = alertView.findViewById(R.id.change_to_playback);

        if (liveViewControl != null)
        {
            toRunningButton.setOnClickListener(this);
            toPlaybackButton.setOnClickListener(this);
        }
        else
        {
            // ライブビューのオン・オフ切り替えボタンを非表示にする
            toRunningButton.setVisibility(View.GONE);
            toPlaybackButton.setVisibility(View.GONE);
        }
        sendButton.setOnClickListener(this);
        alertDialog.setCancelable(true);
        try
        {
            if (method != null)
            {
                method.setText(activity.getText(R.string.http_method_string));
            }
            if (http_header != null)
            {
                http_header.setText(urlToSend);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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

    private void changeRunMode(boolean isStartLiveView)
    {
        // ライブビューの停止と開始
        Log.v(TAG, "changeRunMode() : " + isStartLiveView);
        if (liveViewControl == null)
        {
            Log.v(TAG, "liveViewControl is NULL...");
            return;
        }
        try
        {
            if (isStartLiveView)
            {
                liveViewControl.startLiveView(false);
            }
            else
            {
                liveViewControl.stopLiveView();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.change_to_liveview)
        {
            changeRunMode(true);
            return;
        }
        if (view.getId() == R.id.change_to_playback)
        {
            changeRunMode(false);
            return;
        }


        try
        {
            String methodStr = "";
            String serviceStr = "";
            String commandStr = "";
            String parameterStr = "";
            final Activity activity = getActivity();
            if (activity != null)
            {
                if (http_header != null)
                {
                    String httpStr = http_header.getText().toString().toLowerCase();
                    if (!httpStr.contains("http://"))
                    {
                        this.urlToSend = COMMUNICATE_URL_DEFAULT;
                        http_header.setText(COMMUNICATE_URL_DEFAULT);
                    }
                    else
                    {
                        this.urlToSend = httpStr;
                    }
                }
                
                if (method != null)
                {
                    methodStr = method.getText().toString().toLowerCase();
                }
                final boolean isPost = (methodStr.contains("post"));
                final boolean isPut = (methodStr.contains("put"));

                if (service != null)
                {
                    serviceStr = service.getText().toString();
                }
                if (command != null)
                {
                    commandStr = command.getText().toString();
                }
                if (parameter != null)
                {
                    // GET メソッドのときは、 commandStr と parameterStrを結合する。
                    parameterStr = parameter.getText().toString();
                    if ((!isPost)&&(parameterStr.length() > 0))
                    {
                        commandStr = commandStr + "&" + parameterStr;
                    }
                }

                //  > GET  : http://xxx.xxx.xxx.xxx/(serviceStr) + "?" (commandStr) + "&" (parameterStr)
                //  > POST : http://xxx.xxx.xxx.xxx/(serviceStr) + "?" (commandStr) , parameterStr ← BODY
                if (commandStr.length() > 0)
                {
                    serviceStr = urlToSend + serviceStr + "?" + commandStr;
                }
                else
                {
                    // commandStrにデータが記入されていない場合はServiceStrのみ
                    serviceStr = urlToSend + serviceStr;
                }

                final String url = serviceStr;
                final String param = parameterStr;

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            String reply;
                            if (isPost)
                            {
                                reply =  SimpleHttpClient.httpPostWithHeader(url, param, headerMap, null, TIMEOUT_MS);
                            }
                            else if (isPut)
                            {
                                reply =  SimpleHttpClient.httpPutWithHeader(url, param, headerMap, null, TIMEOUT_MS);
                            }
                            else
                            {
                                reply = SimpleHttpClient.httpGetWithHeader(url, headerMap, null, TIMEOUT_MS);
                            }
                            Log.v(TAG, "URL : " + url + " "+ param + " RESP : " + reply);
                            final String response = reply;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (responseArea != null)
                                    {
                                        responseArea.setText(response);
                                    }
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            else
            {
                Log.v(TAG, "getActivity() Fail...");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
