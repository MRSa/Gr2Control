package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class FujiXCommandPublisher implements IFujiXCommandPublisher, IFujiXCommunication
{
    private final String TAG = toString();

    private static final int SEQUENCE_START_NUMBER = 1;
    private static final int BUFFER_SIZE = 1024 * 1024 + 8;
    private static final int COMMAND_SEND_RECEIVE_DURATION_MS = 10;
    private static final int COMMAND_SEND_RECEIVE_DURATION_MAX = 1000;
    private static final int COMMAND_POLL_QUEUE_MS = 10;

    private final String ipAddress;
    private final int portNumber;

    private boolean isStart = false;
    private Socket socket = null;
    private DataOutputStream dos = null;
    private BufferedReader bufferedReader = null;
    private int sequenceNumber = SEQUENCE_START_NUMBER;
    private Queue<IFujiXCommand> commandQueue;


    public FujiXCommandPublisher(@NonNull String ip, int portNumber)
    {
        this.ipAddress = ip;
        this.portNumber = portNumber;
        this.commandQueue = new ArrayDeque<>();
        commandQueue.clear();
    }

    @Override
    public boolean isConnected()
    {
        return (socket != null);
    }

    @Override
    public boolean connect()
    {
        try
        {
            socket = new Socket(ipAddress, portNumber);
            return (true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            socket = null;
        }
        return (false);
    }

    @Override
    public void disconnect()
    {
        // ストリームを全部閉じる
        try
        {
            dos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dos = null;

        try
        {
            bufferedReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        bufferedReader = null;

        try
        {
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        socket = null;
        sequenceNumber = SEQUENCE_START_NUMBER;
        System.gc();
    }

    @Override
    public void start()
    {
        if (isStart)
        {
            // すでにコマンドのスレッド動作中なので抜ける
            return;
        }
        isStart = true;
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (isStart)
                    {
                        try
                        {
                            IFujiXCommand command = commandQueue.poll();
                            if (command != null)
                            {
                                issueCommand(command);
                            }
                            Thread.sleep(COMMAND_POLL_QUEUE_MS);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.v(TAG, "<<<<< IP : " + ipAddress + " port : " + portNumber + " >>>>>");
                    e.printStackTrace();
                }
            }
        });
        try
        {
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        isStart = false;
        commandQueue.clear();
    }

    @Override
    public boolean enqueueCommand(@NonNull IFujiXCommand command)
    {
        try
        {
            //Log.v(TAG, " --- Enqueue : "  + command.getId());
            return (commandQueue.offer(command));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    private void issueCommand(@NonNull IFujiXCommand command)
    {
        try
        {
            //Log.v(TAG, "issueCommand : " + command.getId());
            byte[] commandBody = command.commandBody();
            if (commandBody != null)
            {
                // コマンドボディが入っていた場合には、コマンド送信（入っていない場合は受信待ち）
                send_to_camera(command.dumpLog(), commandBody, command.useSequenceNumber());
                byte[] commandBody2 = command.commandBody2();
                if (commandBody2 != null)
                {
                    // コマンドボディの２つめが入っていた場合には、コマンドを連続送信する
                    send_to_camera(command.dumpLog(), commandBody2, command.useSequenceNumber());
                }
                if (command.isIncrementSeqNumber())
                {
                    // シーケンス番号を更新する
                    sequenceNumber++;
                }
            }
            int delayMs = command.receiveDelayMs();
            if ((delayMs < 0)||(delayMs > COMMAND_SEND_RECEIVE_DURATION_MAX))
            {
                delayMs = COMMAND_SEND_RECEIVE_DURATION_MS;
            }
            receive_from_camera(command.dumpLog(), command.getId(), command.responseCallback(), command.receiveAgainShortLengthMessage(), delayMs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *    カメラにコマンドを送信する（メイン部分）
     *
     */
    private void send_to_camera(boolean isDumpReceiveLog, byte[] byte_array, boolean useSequenceNumber)
    {
        try
        {
            dos = new DataOutputStream(socket.getOutputStream());

            // メッセージボディを加工： 最初に４バイトのレングス長をつける
            byte[] sendData = new byte[byte_array.length + 4];

            sendData[0] = (byte) (byte_array.length + 4);
            sendData[1] = 0x00;
            sendData[2] = 0x00;
            sendData[3] = 0x00;
            System.arraycopy(byte_array,0,sendData,4, byte_array.length);

            if (useSequenceNumber)
            {
                // Sequence Number を反映させる
                sendData[8] = (byte) ((0x000000ff & sequenceNumber));
                sendData[9] = (byte) (((0x0000ff00 & sequenceNumber) >>> 8) & 0x000000ff);
                sendData[10] = (byte) (((0x00ff0000 & sequenceNumber) >>> 16) & 0x000000ff);
                sendData[11] = (byte) (((0xff000000 & sequenceNumber) >>> 24) & 0x000000ff);
                if (isDumpReceiveLog)
                {
                    Log.v(TAG, "SEQ No. : " + sequenceNumber);
                }
            }

            if (isDumpReceiveLog)
            {
                // ログに送信メッセージを出力する
                dump_bytes("SEND[" + sendData.length + "] ", sendData);
            }

            // (データを)送信
            dos.write(sendData);
            dos.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void sleep(int delayMs)
    {
        try
        {
            Thread.sleep(delayMs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     *    カメラからにコマンドの結果を受信する（メイン部分）
     *
     */
/*
    private void receive_from_camera(boolean isDumpReceiveLog, int id, IFujiXCommandCallback callback, boolean receiveAgain, int delayMs)
    {
        try
        {
            sleep(delayMs);
            byte[] byte_array = new byte[BUFFER_SIZE];
            InputStream is = socket.getInputStream();
            if (is != null)
            {
                int read_bytes = is.read(byte_array, 0, BUFFER_SIZE);
                byte[] receive_body;
                if (read_bytes > 4)
                {
                    if (receiveAgain)
                    {
                        int length = ((((int) byte_array[3]) & 0xff) << 24) + ((((int) byte_array[2]) & 0xff) << 16) + ((((int) byte_array[1]) & 0xff) << 8) + (((int) byte_array[0]) & 0xff);
                        if ((length > read_bytes)||((length == read_bytes)&&((int) byte_array[4] == 0x02)))
                        {
                            // データについて、もう一回受信が必要な場合...
                            if (isDumpReceiveLog)
                            {
                                Log.v(TAG, "--- RECEIVE AGAIN --- [" + length + "(" + read_bytes + ") " + byte_array[4]+ "] ");
                            }
                            sleep(delayMs);
                            int read_bytes2 = is.read(byte_array, read_bytes, BUFFER_SIZE - read_bytes);
                            if (read_bytes2 > 0)
                            {
                                read_bytes = read_bytes + read_bytes2;
                            }
                        }
                    }
                    receive_body = Arrays.copyOfRange(byte_array, 0, read_bytes);
                }
                else
                {
                    receive_body = new byte[1];
                }
                if (isDumpReceiveLog)
                {
                    // ログに受信メッセージを出力する
                    Log.v(TAG, " receive_from_camera() : " + read_bytes + " bytes.");
                    dump_bytes("RECV[" + receive_body.length + "] ", receive_body);
                }
                if (callback != null)
                {
                    callback.receivedMessage(id, receive_body);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
*/
    /**
     *    カメラからにコマンドの結果を受信する（メイン部分）
     *
     */
    private void receive_from_camera(boolean isDumpReceiveLog, int id, IFujiXCommandCallback callback, boolean receiveAgain, int delayMs)
    {
        try
        {
            sleep(delayMs);
            boolean isFirstTime = true;
            int totalReadBytes;
            int receive_message_buffer_size = BUFFER_SIZE;
            byte[] byte_array = new byte[receive_message_buffer_size];
            InputStream is = socket.getInputStream();
            if (is != null)
            {
                int read_bytes = is.read(byte_array, 0, receive_message_buffer_size);
                byte[] receive_body;
                if (read_bytes > 4)
                {
                    if (receiveAgain)
                    {
                        int length = ((((int) byte_array[3]) & 0xff) << 24) + ((((int) byte_array[2]) & 0xff) << 16) + ((((int) byte_array[1]) & 0xff) << 8) + (((int) byte_array[0]) & 0xff);
                        if (length > receive_message_buffer_size)
                        {
                            Log.v(TAG, "+++++ TOTAL RECEIVE MESSAGE SIZE IS " + length + " +++++");
                        }
                        totalReadBytes = read_bytes;
                        while ((length > totalReadBytes)||((length == read_bytes)&&((int) byte_array[4] == 0x02)))
                        {
                            // データについて、もう一回受信が必要な場合...
                            if (isDumpReceiveLog)
                            {
                                Log.v(TAG, "--- RECEIVE AGAIN --- [" + length + "(" + read_bytes + ") " + byte_array[4]+ "] ");
                            }
                            sleep(delayMs);
                            int read_bytes2 = is.read(byte_array, read_bytes, receive_message_buffer_size - read_bytes);
                            if (read_bytes2 > 0)
                            {
                                read_bytes = read_bytes + read_bytes2;
                                totalReadBytes = totalReadBytes + read_bytes2;
                            }
                            else
                            {
                                // よみだし終了。
                                Log.v(TAG, "FINISHED RECEIVE... ");
                                break;
                            }
                            if (callback != null)
                            {
                                if (callback.isReceiveMulti())
                                {
                                    int offset = 0;
                                    if (isFirstTime)
                                    {
                                        // 先頭のヘッダ部分をカットして送る
                                        offset = 12;
                                        isFirstTime = false;
                                        //Log.v(TAG, " FIRST TIME : " + read_bytes + " " + offset);
                                    }
                                    callback.onReceiveProgress(read_bytes - offset, length, Arrays.copyOfRange(byte_array, offset, read_bytes));
                                    read_bytes = 0;
                                }
                                else
                                {
                                    callback.onReceiveProgress(read_bytes, length, null);
                                }
                            }
                        }
                    }
                    receive_body = Arrays.copyOfRange(byte_array, 0, read_bytes);
                }
                else
                {
                    receive_body = new byte[1];
                }
                if (isDumpReceiveLog)
                {
                    // ログに受信メッセージを出力する
                    Log.v(TAG, "receive_from_camera() : " + read_bytes + " bytes. [" + receive_message_buffer_size + "]");
                    dump_bytes("RECV[" + receive_body.length + "] ", receive_body);
                }
                if (callback != null)
                {
                    if (callback.isReceiveMulti())
                    {
                        callback.receivedMessage(id, null);
                    }
                    else
                    {
                        callback.receivedMessage(id, receive_body);
                        //callback.receivedMessage(id, Arrays.copyOfRange(receive_body, 0, receive_body.length));
                    }
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    /**
     *   デバッグ用：ログにバイト列を出力する
     *
     */
    private void dump_bytes(String header, byte[] data)
    {
        int index = 0;
        StringBuffer message;
        message = new StringBuffer();
        for (byte item : data)
        {
            index++;
            message.append(String.format("%02x ", item));
            if (index >= 8)
            {
                Log.v(TAG, header + " " + message);
                index = 0;
                message = new StringBuffer();
            }
        }
        if (index != 0)
        {
            Log.v(TAG, header + " " + message);
        }
        System.gc();
    }
}
