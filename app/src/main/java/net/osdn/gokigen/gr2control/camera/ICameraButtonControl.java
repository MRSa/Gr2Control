package net.osdn.gokigen.gr2control.camera;

public interface ICameraButtonControl
{
    void pushedButton(String code);


    String FRONT_LEFT = "bjogleft";
    String FRONT_RIGHT = "bjogright";
    String ADJ_LEFT = "badjleft";
    String ADJ_ENTER = "badjok";
    String ADJ_RIGHT = "badjright";

    String TOGGLE_AEAF_OFF = "baf 0";
    String TOGGLE_AEAF_ON = "baf 1";

    String LEVER_AEAFL = "bafl";
    String LEVER_CAF = "bafc";

    String BUTTON_UP = "bup";
    String BUTTON_LEFT = "bleft";
    String BUTTON_ENTER = "bok";
    String BUTTON_RIGHT = "bright";
    String BUTTON_DOWN = "bdown";

    String BUTTON_FUNCTION_1 = "bdisp";
    String BUTTON_FUNCTION_2 = "btrash";
    String BUTTON_FUNCTION_3 = "beffect";

    String BUTTON_PLUS = "btele";
    String BUTTON_MINUS = "bwide";
    String BUTTON_PLAYBACK = "bplay";

    String KEYLOCK_ON = "uilock on";
    String KEYLOCK_OFF = "uilock off";

    String LENS_OPEN = "acclock off";
    String LENS_RETRACT = "acclock on";

    String MUTE_ON = "audio mute on";
    String MUTE_OFF = "audio mute off";

    String LCD_SLEEP_ON = "lcd sleep on";
    String LCD_SLEEP_OFF = "lcd sleep off";

    String LED1_ON = "led on 1";
    String LED1_OFF = "led off 1";

    String BEEP = "audio resplay 0 1 3";

    String MODE_REFRESH = "mode refresh";

    String SHUTTER = "brl 0";
    String SHUTTER_PRESS_AND_HALF_HOLD = "brl 2 1";

    String TAKEMODE_M = "bdial M";
    String TAKEMODE_TAV = "bdial TAV";
    String TAKEMODE_AV = "bdial AV";
    String TAKEMODE_TV = "bdial TV";
    String TAKEMODE_P = "bdial P";
    String TAKEMODE_AUTO = "bdial AUTO";
    String TAKEMODE_MY1 = "bdial MY1";
    String TAKEMODE_MY2 = "bdial MY2";
    String TAKEMODE_MY3 = "bdial MY3";
    String TAKEMODE_MOVIE = "bdial MOVIE";

}
