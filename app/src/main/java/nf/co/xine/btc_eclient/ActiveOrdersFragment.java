package nf.co.xine.btc_eclient;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.assist.TradeApi;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.ActiveOrdersAdapter;
import nf.co.xine.btc_eclient.data_structure.Currency;
import nf.co.xine.btc_eclient.data_structure.MyOrder;

public class ActiveOrdersFragment extends Fragment {


    public ActiveOrdersFragment() {
        // Required empty public constructor
    }

    private TradeApi t;
    private ArrayList<MyOrder> activeOrders;
    private String currencyToTrade;
    private String selectedType;
    private ActiveOrdersAdapter activeOrdersAdapter;
    private OnFragmentInteractionListener mListener;
    private ListView activeOrdersList;
    private UpdateActiveOrders updateActiveOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner currencyFilter;
    private Spinner orderTypeFilter;
    private AVLoadingIndicatorView progressBar;
    private LinearLayout spinners;
    private TextView noOrders;
    private ArrayList<String> activePairs = new ArrayList<>();
    private ArrayList<String> activeOrdersTypes = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("ActiveOrdersFragment", "View created");
        return inflater.inflate(R.layout.fragment_active_orders, container, false);
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
    public void onResume() {
        super.onResume();
        Log.d("ActiveOrdersFragment", "Resumed");
        noOrders = (TextView) getView().findViewById(R.id.no_orders_text);
        progressBar = (AVLoadingIndicatorView) getView().findViewById(R.id.active_orders_progress_bar);
        spinners = (LinearLayout) getView().findViewById(R.id.active_orders_spinners);
        currencyFilter = (Spinner) getView().findViewById(R.id.currency_filter);
        currencyFilter.setOnItemSelectedListener(currencyChangeListener);
        orderTypeFilter = (Spinner) getView().findViewById(R.id.order_type_filter);
        orderTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        activeOrdersList = (ListView) getView().findViewById(R.id.active_orders_list);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.active_orders_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (updateActiveOrders = new UpdateActiveOrders()).execute(t);
            }
        });

        //resuming fragment
        if (mListener.getTypes() != null && mListener.getPairs() != null) {
            currencyFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, mListener.getPairs()));
            orderTypeFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, mListener.getTypes()));
        }

        if (mListener.getActiveOrders() != null) {
            activeOrdersAdapter = new ActiveOrdersAdapter(getActivity(), mListener.getActiveOrders());
            activeOrdersList.setAdapter(activeOrdersAdapter);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            spinners.setVisibility(View.GONE);
            activeOrdersList.setVisibility(View.GONE);
        }

        t = mListener.getApi();
        activePairs.add(getResources().getString(R.string.all_pairs));
        activeOrdersTypes.add(getResources().getString(R.string.all_types));
        //TODO: replace this with db cache method.
//        currencyFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, activePairs));
//        orderTypeFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, activeOrdersTypes));
        (updateActiveOrders = new UpdateActiveOrders()).execute(t);
    }

    public AdapterView.OnItemSelectedListener currencyChangeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currencyToTrade = parent.getSelectedItem().toString();
            Log.d("Changed", currencyToTrade);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private class UpdateActiveOrders extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].activeOrders.runMethod();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            String pair = currencyToTrade;
            String type = selectedType;
            activePairs = new ArrayList<>();
            activeOrdersTypes = new ArrayList<>();
            activePairs.add(getResources().getString(R.string.all_pairs));
            activeOrdersTypes.add(getResources().getString(R.string.all_types));
            boolean pairStillPresent;
            boolean typeStillPresent;
            Log.d("Loading", "started method");
            //currencyToTrade = mListener.getCurrencyToTrade();
            activeOrders = new ArrayList<>();
            while (t.activeOrders.hasNext()) {
                t.activeOrders.switchNext();
                if (!activePairs.contains(t.activeOrders.getCurrentPair()))
                    activePairs.add(t.activeOrders.getCurrentPair());
                if (!activeOrdersTypes.contains(t.activeOrders.getCurrentType()))
                    activeOrdersTypes.add(t.activeOrders.getCurrentType().toUpperCase());
                Log.d("Error", "this:" + t.activeOrders.getErrorMessage());
                if (!t.activeOrders.getCurrentPair().equals(ProfileFragment.convertName(currencyToTrade)))
                    if (!currencyToTrade.equals(getResources().getString(R.string.all_pairs)))
                        continue;
                if (!t.activeOrders.getCurrentType().equalsIgnoreCase(selectedType))
                    if (!selectedType.equals(getResources().getString(R.string.all_types)))
                        continue;
                activeOrders.add(new MyOrder(t.activeOrders.getCurrentPair(),
                        t.activeOrders.getCurrentType(),
                        t.activeOrders.getCurrentAmount(),
                        t.activeOrders.getCurrentRate(),
                        t.activeOrders.getCurrentTimestamp_created()));
            }
            swipeRefreshLayout.setRefreshing(false);
            if (!isCancelled()) {
                if (activeOrders.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                    noOrders.setVisibility(View.VISIBLE);
                    //activeOrdersList.setEmptyView(noOrders);
                } else {
                    currencyFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, activePairs));
                    if (activePairs.contains(pair)) {
                        Log.d("Contains", String.valueOf(MainActivity.getIndex(currencyFilter, pair)));
                        currencyToTrade = pair;
                        currencyFilter.setSelection(MainActivity.getIndex(currencyFilter, pair));
                    }
                    orderTypeFilter.setAdapter(new ArrayAdapter(getActivity(), R.layout.currency_spinner_item, activeOrdersTypes));
                    if (activeOrdersTypes.contains(type)) {
                        selectedType = type;
                        orderTypeFilter.setSelection(MainActivity.getIndex(orderTypeFilter, type));
                    }
                    activeOrdersAdapter = new ActiveOrdersAdapter(getActivity(), activeOrders);
                    activeOrdersList.setAdapter(activeOrdersAdapter);
                    progressBar.setVisibility(View.GONE);
                    spinners.setVisibility(View.VISIBLE);
                    activeOrdersList.setVisibility(View.VISIBLE);
                    noOrders.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.saveActiveOrders(activeOrders, activePairs, activeOrdersTypes);
        mListener = null;
        updateActiveOrders.cancel(true);
        Log.d("Active", "Detached");
    }

    public interface OnFragmentInteractionListener {
        TradeApi getApi();

        void saveActiveOrders(ArrayList<MyOrder> orders, ArrayList<String> pairs, ArrayList<String> types);

        ArrayList<MyOrder> getActiveOrders();

        ArrayList<String> getPairs();

        ArrayList<String> getTypes();

        ArrayList<Currency> getCurrencies();

        String getCurrencyToTrade();
    }
}
