package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.command;

public interface IFujiXCommand
{
    // メッセージの識別子
    int getId();

    // 短い長さのメッセージを受け取ったときに再度受信するか
    boolean receiveAgainShortLengthMessage();

    // シーケンス番号を埋め込むかどうか
    boolean useSequenceNumber();

    // シーケンス番号を更新（＋１）するかどうか
    boolean isIncrementSeqNumber();

    // コマンドの受信待ち時間(単位:ms)
    int receiveDelayMs();

    // 送信するメッセージボディ
    byte[] commandBody();

    // 送信するメッセージボディ(連続送信する場合)
    byte[] commandBody2();

    // コマンド送信結果（応答）の通知先
    IFujiXCommandCallback responseCallback();

    //  特定シーケンスを特定するID
    int getHoldId();

    // 特定シーケンスに入るか？
    boolean isHold();

    // 特定シーケンスから出るか？
    boolean isRelease();

    // デバッグ用： ログ(logcat)に通信結果を残すかどうか
    boolean dumpLog();

}
