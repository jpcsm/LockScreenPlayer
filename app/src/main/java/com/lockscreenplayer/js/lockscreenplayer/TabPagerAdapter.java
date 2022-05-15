package com.lockscreenplayer.js.lockscreenplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by lenovo on 2017-02-23.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    String url;

    TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0 :
                return "구매하기" ;
            case 1 :
                return "내 쿠폰함" ;
            case 2 :
                return "교환처 찾기";
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return Purchase_Fragment.newInstance();
            case 1:
                return Coupon_fragment.newInstance();
            case 2:
                return MapsFragment.newInstance();
            default:
                return null;
        }

    }
}