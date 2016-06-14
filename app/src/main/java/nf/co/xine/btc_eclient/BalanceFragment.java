package nf.co.xine.btc_eclient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.assist.TradeApi;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.BalanceAdapter;
import nf.co.xine.btc_eclient.data_structure.CurrencyBalance;

public class BalanceFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ListView balanceList;
    private TradeApi t;
    private ArrayList<CurrencyBalance> balances;
    private UpdateBalance updateBalance;
    private SwipeRefreshLayout swipeRefreshLayout;

    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance, container, false);
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
        balanceList = (ListView) getView().findViewById(R.id.balance_list);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.balance_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (updateBalance = new UpdateBalance()).execute(t);
            }
        });
        balances = mListener.getBalance();
        if (balances != null) {
            BalanceAdapter adapter = new BalanceAdapter(getActivity(), balances);
            balanceList.setAdapter(adapter);
        }
        t = mListener.getApi();
        (updateBalance = new UpdateBalance()).execute(t);
    }

    private class UpdateBalance extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].getInfo.runMethod();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Log.d("Balance", "Updating UI");
            balances = new ArrayList<>();
            /*balances.add(new CurrencyBalance("USD", "20.26"));*/

            balances.add(new CurrencyBalance("BTC", t.getInfo.getBalance("BTC") + " BTC"));
            balances.add(new CurrencyBalance("LTC", t.getInfo.getBalance("LTC") + " LTC"));
            balances.add(new CurrencyBalance("NMC", t.getInfo.getBalance("NMC") + " NMC"));
            balances.add(new CurrencyBalance("NVC", t.getInfo.getBalance("NVC") + " NVC"));
            balances.add(new CurrencyBalance("TRC", t.getInfo.getBalance("TRC") + " TRC"));
            balances.add(new CurrencyBalance("PPC", t.getInfo.getBalance("PPC") + " PPC"));
            balances.add(new CurrencyBalance("FTC", t.getInfo.getBalance("FTC") + " FTC"));
            balances.add(new CurrencyBalance("XPM", t.getInfo.getBalance("XPM") + " XPM"));
            balances.add(new CurrencyBalance("DSH", t.getInfo.getBalance("DSH") + " DSH"));
            balances.add(new CurrencyBalance("ETH", t.getInfo.getBalance("ETH") + " ETH"));
            balances.add(new CurrencyBalance("USD", t.getInfo.getBalance("USD") + " USD"));
            balances.add(new CurrencyBalance("RUR", t.getInfo.getBalance("RUR") + " RUR"));
            balances.add(new CurrencyBalance("EUR", t.getInfo.getBalance("EUR") + " EUR"));
            balances.add(new CurrencyBalance("CNH", t.getInfo.getBalance("CNH") + " CNH"));
            balances.add(new CurrencyBalance("GBP", t.getInfo.getBalance("GBP") + " GBP"));
            swipeRefreshLayout.setRefreshing(false);
            if (!isCancelled()) {
                BalanceAdapter adapter = new BalanceAdapter(getActivity(), balances);
                balanceList.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.saveBalance(balances);
        mListener = null;
        updateBalance.cancel(true);
        Log.d("Balance", "Detached");
    }

    public interface OnFragmentInteractionListener {
        TradeApi getApi();

        void saveBalance(ArrayList<CurrencyBalance> balances);

        ArrayList<CurrencyBalance> getBalance();
    }
}
