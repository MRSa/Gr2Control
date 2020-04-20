package net.osdn.gokigen.gr2control.camera.fuji_x.wrapper.values;

public interface IFujiXFlashMode
{
    int FLASH_AUTO  =1;
    int FLASH_OFF  =2;
    int FLASH_FILL  =3;
    int FLASH_REDEYE_AUTO  =4;
    int FLASH_REDEYE_FILL  =5;
    int FLASH_EXTERNAL_SYNC  =6;
    int FLASH_ON  =0x8001;
    int FLASH_REDEYE  =0x8002;
    int FLASH_REDEYE_ON  =0x8003;
    int FLASH_REDEYE_SYNC  =0x8004;
    int FLASH_REDEYE_REAR  =0x8005;
    int FLASH_SLOW_SYNC  =0x8006;
    int FLASH_REAR_SYNC  =0x8007;
    int FLASH_COMMANDER  =0x8008;
    int FLASH_DISABLE  =0x8009;
    int FLASH_ENABLE  =0x800a;
}
