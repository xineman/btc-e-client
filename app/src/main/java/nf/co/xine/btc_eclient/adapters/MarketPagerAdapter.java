package nf.co.xine.btc_eclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import nf.co.xine.btc_eclient.ChartsFragment;
import nf.co.xine.btc_eclient.CurrencyFragment;
import nf.co.xine.btc_eclient.MarketHistoryFragment;

public class MarketPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public MarketPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 3;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new CurrencyFragment();
            case 1:
                return new MarketHistoryFragment();
            case 2:
                return new ChartsFragment();
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
                return "Chart";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public Fragment getOrdersFragment() {
        for (int i = 0; i < registeredFragments.size(); i++)
            if (registeredFragments.get(i) instanceof CurrencyFragment)
                return registeredFragments.get(i);
        return null;
    }

    public Fragment getHistoryFragment() {
        for (int i = 0; i < registeredFragments.size(); i++)
            if (registeredFragments.get(i) instanceof MarketHistoryFragment)
                return registeredFragments.get(i);
        return null;
    }
}