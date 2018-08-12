package net.osdn.gokigen.gr2control.camera.olympus.myolycameraprops;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

class LoadSaveMyCameraPropertyPagerAdapter extends FragmentPagerAdapter
{
    private final String TAG = toString();
    private final ILoadSaveMyCameraPropertyDialogDismiss dismissInterface;

    private String[] titles = null;
    private LoadMyCameraPropertyFragment loadFragment = null;
    private SaveMyCameraPropertyFragment saveFragment = null;

    LoadSaveMyCameraPropertyPagerAdapter(FragmentManager fm, ILoadSaveMyCameraPropertyDialogDismiss dismissInterface)
    {
        super(fm);
        this.dismissInterface = dismissInterface;
    }

    private void initialize()
    {
        loadFragment = null;
        loadFragment = new LoadMyCameraPropertyFragment();
        loadFragment.setDismissInterface(dismissInterface);

        saveFragment = null;
        saveFragment = new SaveMyCameraPropertyFragment();
        saveFragment.setDismissInterface(dismissInterface);
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.v(TAG, "getItem :" + position);
        if ((loadFragment == null)||(saveFragment == null))
        {
            initialize();
        }
        Fragment returnFragment;
        if (position == 0)
        {
            // loadFragment
            returnFragment = loadFragment;
        }
        else  //
        {
            // saveFragment
            returnFragment = saveFragment;
        }
        return (returnFragment);
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }

    void setTitles(String[] titles)
    {
        this.titles = titles;
    }
}
