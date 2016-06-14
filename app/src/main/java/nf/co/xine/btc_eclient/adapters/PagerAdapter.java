package nf.co.xine.btc_eclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import nf.co.xine.btc_eclient.ActiveOrdersFragment;
import nf.co.xine.btc_eclient.BalanceFragment;
import nf.co.xine.btc_eclient.TransactionsHistoryFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 3;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ActiveOrdersFragment();
            case 1:
                return new TransactionsHistoryFragment();
            case 2:
                return new BalanceFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Orders";
            case 1:
                return "History";
            case 2:
                return "Balance";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}