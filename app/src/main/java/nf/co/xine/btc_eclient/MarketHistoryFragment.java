package nf.co.xine.btc_eclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.assist.TradeApi;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.MarketHistoryAdapter;
import nf.co.xine.btc_eclient.data_structure.MarketTrade;

public class MarketHistoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TradeApi api;
    private ArrayList<MarketTrade> marketTrades;
    private ListView marketOrdersList;
    private SwipeRefreshLayout refreshLayout;
    private AVLoadingIndicatorView progressBar;
    private UpdateMarketHistory updateMarketHistory;

    public MarketHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market_history, container, false);
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
            marketOrdersList = (ListView) getView().findViewById(R.id.market_orders_list);
            refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.market_orders_refresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    (updateMarketHistory = new UpdateMarketHistory()).execute();
                }
            });
            progressBar = (AVLoadingIndicatorView) getView().findViewById(R.id.market_orders_progress_bar);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        api = mListener.getApi();
        (updateMarketHistory = new UpdateMarketHistory()).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public class UpdateMarketHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            api.trades.resetParams();
            if (isAdded() && mListener.isConnectedToNetwork()) {
                api.trades.addPair(StaticConverter.currencyNameToUrlFormat(mListener.getCurrencyToTrade()));
                api.trades.runMethod();
                if (api.trades.isSuccess()) {
                    marketTrades = new ArrayList<>();
                    api.trades.switchNextPair();
                    while (api.trades.hasNextTrade()) {
                        api.trades.switchNextTrade();
                        marketTrades.add(new MarketTrade(api.trades.getCurrentType(), api.trades.getCurrentAmount(), api.trades.getCurrentPrice(), api.trades.getCurrentTimestamp()));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshLayout.setRefreshing(false);
            if (isAdded() && api.trades.isSuccess()) {
                marketOrdersList.setAdapter(new MarketHistoryAdapter(getActivity(), marketTrades));
            }
        }
    }

    public void updateCurrencyToTrade() {
        (updateMarketHistory = new UpdateMarketHistory()).execute();
    }

    private void showNoNetworkMessage() {
        Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        TradeApi getApi();

        String getCurrencyToTrade();

        boolean isConnectedToNetwork();

        boolean isPublicInfoReceived();
    }
}
