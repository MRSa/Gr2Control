package net.osdn.gokigen.gr2control.liveview.gridframe;

/**
 *
 *
 */
public class GridFrameFactory
{
    private static final int GRID_FRAME_0 = 0;
    private static final int GRID_FRAME_1 = 1;
    private static final int GRID_FRAME_2 = 2;
    private static final int GRID_FRAME_3 = 3;
    private static final int GRID_FRAME_4 = 4;
    private static final int GRID_FRAME_5 = 5;
    private static final int GRID_FRAME_6 = 6;

    public static IGridFrameDrawer getGridFrameDrawer(int id)
    {
        return (new GridFrameDrawerDefault());
    }
}
