package nf.co.xine.btc_eclient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assist.TradeApi;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.ActiveOrdersAdapter;
import nf.co.xine.btc_eclient.adapters.PagerAdapter;
import nf.co.xine.btc_eclient.data_structure.CompletedOrder;
import nf.co.xine.btc_eclient.data_structure.MyOrder;

public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView balanceTextView;
    private TabLayout tabLayout;
    private ViewPager ordersListPager;
    private PagerAdapter pagerAdapter;
    private TradeApi t;

    public ProfileFragment() {
        // Required empty public constructor
    }

    static String convertName(String name) {
        return (name.substring(0, 3) + "-" + name.substring(4, 7)).toUpperCase();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pagerAdapter == null) {
            Log.d("Profile", "Pager was null");
        }
        //if (pagerAdapter == null)
            pagerAdapter = new PagerAdapter(getChildFragmentManager());

        Log.d("Profile", "OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Profile", "On createView");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Profile", "On activity created");
        try {
            t = mListener.getApi();
            if (getView() != null) {
                Log.d("Profile", "tab clicked");
                //balanceTextView = (TextView) getView().findViewById(R.id.balance_info);
                tabLayout = (TabLayout) getView().findViewById(R.id.orders_tab_layout);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                ordersListPager = (ViewPager) getView().findViewById(R.id.pager);
                ordersListPager.setOffscreenPageLimit(3);
                ordersListPager.setAdapter(pagerAdapter);
                tabLayout.setupWithViewPager(ordersListPager);
                /*TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();*/
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        ordersListPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
            //new updateDataTask().execute(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
        public class updateDataTask extends AsyncTask<TradeApi, Void, Void> {

            @Override
            protected Void doInBackground(TradeApi... apis) {
                apis[0].activeOrders.runMethod();
                apis[0].transHistory.runMethod();
                apis[0].getInfo.runMethod();
                return null;
            }

            @Override
            protected void onPostExecute(Void res) {
                String balance = "Balance: " + String.format("%.4f", Double.parseDouble(t.getInfo.getBalance("USD"))) + " USD " +
                        String.format("%.4f", Double.parseDouble(t.getInfo.getBalance("BTC"))) + " BTC ";
                balanceTextView.setText(balance);
            }
        }
    */
    public interface OnFragmentInteractionListener {

        TradeApi getApi();
    }
}
