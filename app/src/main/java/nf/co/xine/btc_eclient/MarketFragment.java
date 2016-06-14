package nf.co.xine.btc_eclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assist.TradeApi;

import nf.co.xine.btc_eclient.adapters.MarketPagerAdapter;

public class MarketFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ViewPager marketPager;
    private TabLayout marketTabs;
    private MarketPagerAdapter marketPagerAdapter;

    public MarketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market_orders, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            marketPager = (ViewPager) getView().findViewById(R.id.market_pager);
            marketTabs = (TabLayout) getView().findViewById(R.id.market_tabs);
            marketPager.setOffscreenPageLimit(3);
            marketPagerAdapter = new MarketPagerAdapter(getChildFragmentManager());
            marketPager.setAdapter(marketPagerAdapter);
            marketTabs.setupWithViewPager(marketPager);
            /*marketTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    marketPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateCurrencyToTrade() {
        ((CurrencyFragment) marketPagerAdapter.getOrdersFragment()).updateCurrencyToTrade();
        ((MarketHistoryFragment) marketPagerAdapter.getHistoryFragment()).updateCurrencyToTrade();
    }

    public interface OnFragmentInteractionListener {
        String getCurrencyToTrade();

        TradeApi getApi();
    }
}
