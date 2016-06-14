package nf.co.xine.btc_eclient;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.assist.TradeApi;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.TransactionsHistoryAdapter;
import nf.co.xine.btc_eclient.data_structure.Transaction;

public class TransactionsHistoryFragment extends Fragment {


    public TransactionsHistoryFragment() {
        // Required empty public constructor
    }

    private TradeApi t;
    private ArrayList<Transaction> transactions;
    private String currencyToTrade;
    private TransactionsHistoryAdapter historyAdapter;
    private OnFragmentInteractionListener mListener;
    private ListView transactionsList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private updateHistory update;
    private String test = "1";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null)
            test = savedInstanceState.getString("TEST");
        return inflater.inflate(R.layout.fragment_orders_history, container, false);
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
        transactionsList = (ListView) getView().findViewById(R.id.orders_history_list);
        Parcelable state = transactionsList.onSaveInstanceState();
        transactions = mListener.getTransactions();
        if (transactions != null) {
            historyAdapter = new TransactionsHistoryAdapter(getActivity(), transactions);
            transactionsList.setAdapter(historyAdapter);
        }
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.orders_history_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (update = new updateHistory()).execute(t);
            }
        });
        t = mListener.getApi();
        Log.d("History test", test);
        transactionsList.onRestoreInstanceState(state);
        (update = new updateHistory()).execute(t);
    }

    private class updateHistory extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].transHistory.runMethod();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Log.d("History", "Updating UI");
            transactions = new ArrayList<>();
            while (t.transHistory.hasNext()) {
                t.transHistory.switchNext();
                transactions.add(new Transaction(t.transHistory.getCurrentType(),
                        t.transHistory.getCurrentAmount(),
                        t.transHistory.getCurrentCurrency(),
                        t.transHistory.getCurrentDesc(),
                        t.transHistory.getCurrentStatus(),
                        t.transHistory.getCurrentTimestamp()));
            }
            swipeRefreshLayout.setRefreshing(false);
            if (isAdded()) {
                historyAdapter = new TransactionsHistoryAdapter(getActivity(), transactions);
                transactionsList.setAdapter(historyAdapter);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        test = "saved";
        outState.putString("TEST", test);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.saveTransactions(transactions);
        mListener = null;
        //update.cancel(true);
        Log.d("History", "Detached");
    }

    public interface OnFragmentInteractionListener {
        TradeApi getApi();

        void saveTransactions(ArrayList<Transaction> transactions);

        ArrayList<Transaction> getTransactions();

        String getCurrencyToTrade();
    }
}
