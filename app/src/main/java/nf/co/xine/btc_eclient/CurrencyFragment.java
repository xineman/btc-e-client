package nf.co.xine.btc_eclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.assist.TradeApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.adapters.CustomAdapter;
import nf.co.xine.btc_eclient.adapters.OrdersAdapter;
import nf.co.xine.btc_eclient.data_structure.Currency;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;

public class CurrencyFragment extends Fragment {

    private CurrencyFragmentListener mListener;
    /*private JsonObjectRequest jsObjRequest;
    private JsonObjectRequest summaryRequest;
    String url;
    String summaryUrl;*/
    private ListView ordersView;
    private Button buyOrderButton;
    private Button sellOrderButton;
    private RadioButton buyRadio;
    private RadioButton sellRadio;
    private TextView buyBalance;
    private TextView sellBalance;
    private TextView buyBalanceHeader;
    private TextView sellBalanceHeader;
    private EditText amount;
    private EditText price;
    private TextView total_value;
    private View placeOrderDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpdateCurrencyBalance updateCurrencyBalance;
    private UpdateOrders updateOrders;
    private UpdateSummary updateSummary;
    private ArrayList<CurrencyOrder> orders;
    private ArrayList<String> summary;
    private String currencyToTrade;
    private TradeApi tradeApi;

    final Handler handler = new Handler();
    Runnable makeRequest = new Runnable() {
        public void run() {
            updateInfo();
            handler.postDelayed(this, 60000);
        }
    };

    public CurrencyFragment() {
        // Required empty public constructor
    }

    /*
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                updateOrders(response);
            }
        };

        Response.Listener<JSONObject> summaryListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                updateSummary(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };


        public void updateValues() {
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(summaryRequest);
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);
        }


        //saving data from Json response to arraylists, populating listviews with this araylists
        private void updateOrders(JSONObject response) {
            ArrayList<CurrencyOrder> orders = new ArrayList<>();
            Parcelable stateA = ordersView.onSaveInstanceState();
            OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), orders, currencyToTrade);
            try {
                JSONObject tmp = response.getJSONObject(MainActivity.convertName(currencyToTrade));
                JSONArray asksArray = tmp.getJSONArray("asks");
                JSONArray bidsArray = tmp.getJSONArray("bids");
                for (int i = 0; i < asksArray.length(); i++) {
                    double askValue = ((JSONArray) asksArray.get(i)).getDouble(0);
                    double askAmount = ((JSONArray) asksArray.get(i)).getDouble(1);
                    double bidValue = ((JSONArray) bidsArray.get(i)).getDouble(0);
                    double bidAmount = ((JSONArray) bidsArray.get(i)).getDouble(1);
                    orders.add(new CurrencyOrder(askValue, askAmount, bidValue, bidAmount));
                }
                ordersView.setAdapter(ordersAdapter);
                ordersView.onRestoreInstanceState(stateA);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void updateSummary(JSONObject response) {
            TextView min24 = (TextView) getView().findViewById(R.id.min_24h);
            TextView max24 = (TextView) getView().findViewById(R.id.max_24h);
            TextView last = (TextView) getView().findViewById(R.id.last_deal);
            //TextView volume = (TextView) getView().findViewById(R.id.avg_24h);
            try {
                JSONObject tmp = response.getJSONObject(MainActivity.convertName(mListener.getCurrencyToTrade()));
                min24.setText(CustomAdapter.convertDouble(tmp.getDouble("low")));
                max24.setText(CustomAdapter.convertDouble(tmp.getDouble("high")));
                last.setText(CustomAdapter.convertDouble(tmp.getDouble("last")));
                //volume.setText(CustomAdapter.convertDouble(tmp.getDouble("avg")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void stopRequests() {
            MySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            handler.removeCallbacks(makeRequest);
        }
    */
    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //(int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void setCurrencyToTrade(String currency) {
        currencyToTrade = currency;
        updateInfo();
    }

    public String getCurrencyToTrade() {
        return currencyToTrade;
    }

    //TODO fix this shit: update summary and orders immediately after changing the currency in spinner.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CurrencyFragmentListener) {
            mListener = (CurrencyFragmentListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            tradeApi = new TradeApi(mListener.getKey(), mListener.getSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getView() != null) {
            swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.orders_list_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateInfo();
                }
            });
            ordersView = (ListView) getView().findViewById(R.id.orders_list);
            placeOrderDialog = getView().findViewById(R.id.place_order_dialog);
            buyOrderButton = (Button) getView().findViewById(R.id.new_buy_order);
            sellOrderButton = (Button) getView().findViewById(R.id.new_sell_order);
            buyBalance = (TextView) getView().findViewById(R.id.buy_balance);
            sellBalance = (TextView) getView().findViewById(R.id.sell_balance);
            buyBalanceHeader = (TextView) getView().findViewById(R.id.buy_balance_header);
            sellBalanceHeader = (TextView) getView().findViewById(R.id.sell_balance_header);
            amount = (EditText) getView().findViewById(R.id.amount_edit);
            price = (EditText) getView().findViewById(R.id.price_edit);
            total_value = (TextView) getView().findViewById(R.id.total_new_order_value);
            buyRadio = (RadioButton) getView().findViewById(R.id.radio_buy);
            sellRadio = (RadioButton) getView().findViewById(R.id.radio_sell);
            buyOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    if (placeOrderDialog.getVisibility() == View.GONE) expand(placeOrderDialog);
                    buyRadio.setChecked(true);
                }
            });
            sellOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    expand(placeOrderDialog);
                    sellRadio.setChecked(true);
                }
            });
            Button cancel = (Button) getView().findViewById(R.id.collapse_order_menu);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOrderButton.setVisibility(View.VISIBLE);
                    sellOrderButton.setVisibility(View.VISIBLE);
                    collapse(placeOrderDialog);
                }
            });
        }
    }

    private void updateInfo() {
        currencyToTrade = mListener.getCurrencyToTrade();
        tradeApi.getInfo.resetParams();
        tradeApi.depth.resetParams();
        tradeApi.ticker.resetParams();
        tradeApi.depth.addPair(MainActivity.convertName(currencyToTrade));
        tradeApi.depth.setLimit(20);
        (updateOrders = new UpdateOrders()).execute(tradeApi);
        tradeApi.ticker.addPair(MainActivity.convertName(mListener.getCurrencyToTrade()));
        (updateSummary = new UpdateSummary()).execute(tradeApi);
        (updateCurrencyBalance = new UpdateCurrencyBalance()).execute(tradeApi);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
        /*
        Old version, before using TradeApi
        url = "https://btc-e.com/api/3/depth/" + MainActivity.convertName(currencyToTrade) + "?limit=20";
        summaryUrl = "https://btc-e.com/api/3/ticker/" + MainActivity.convertName(mListener.getCurrencyToTrade());
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);
        summaryRequest = new JsonObjectRequest
                (Request.Method.GET, summaryUrl, null, summaryListener, errorListener);
        handler.post(makeRequest);
        */
        Log.d("Currency", "Resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        updateCurrencyBalance.cancel(true);
        updateOrders.cancel(true);
        updateSummary.cancel(true);
        Log.d("Currency", "Paused");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class UpdateCurrencyBalance extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].getInfo.runMethod();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (!isCancelled()) {
                sellBalanceHeader.setText(currencyToTrade.substring(0, 3).toUpperCase() + " balance");
                sellBalance.setText(tradeApi.getInfo.getBalance(currencyToTrade.substring(0, 3)));
                buyBalanceHeader.setText(OrdersAdapter.right(currencyToTrade, 3).toUpperCase() + " balance");
                buyBalance.setText(tradeApi.getInfo.getBalance(OrdersAdapter.right(currencyToTrade, 3)));
            }
        }
    }

    private class UpdateSummary extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].ticker.runMethod();
            apis[0].ticker.switchNextPair();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (!isCancelled()) {
                TextView min24 = (TextView) getView().findViewById(R.id.min_24h);
                TextView max24 = (TextView) getView().findViewById(R.id.max_24h);
                TextView last = (TextView) getView().findViewById(R.id.last_deal);
                min24.setText(CustomAdapter.convertDouble(Double.parseDouble(tradeApi.ticker.getCurrentLow())));
                max24.setText(CustomAdapter.convertDouble(Double.parseDouble(tradeApi.ticker.getCurrentHigh())));
                last.setText(CustomAdapter.convertDouble(Double.parseDouble(tradeApi.ticker.getCurrentLast())));
                summary = new ArrayList<>();
                summary.add(tradeApi.ticker.getCurrentPairName());
                summary.add(tradeApi.ticker.getCurrentLow());
                summary.add(tradeApi.ticker.getCurrentHigh());
                summary.add(tradeApi.ticker.getCurrentLast());
            }
        }
    }

    private class UpdateOrders extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            apis[0].depth.runMethod();
            apis[0].depth.switchNextPair();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Parcelable stateA = ordersView.onSaveInstanceState();
            orders = new ArrayList<>();
            OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), orders, currencyToTrade);
            if (!isCancelled()) {
                while (tradeApi.depth.hasNextAsk() || tradeApi.depth.hasNextBid()) {
                    tradeApi.depth.switchNextAsk();
                    tradeApi.depth.switchNextBid();
                    orders.add(new CurrencyOrder(Double.parseDouble(tradeApi.depth.getCurrentAskPrice()),
                            Double.parseDouble(tradeApi.depth.getCurrentAskAmount()),
                            Double.parseDouble(tradeApi.depth.getCurrentBidPrice()),
                            Double.parseDouble(tradeApi.depth.getCurrentBidAmount())));
                }
                swipeRefreshLayout.setRefreshing(false);
                ordersView.setAdapter(ordersAdapter);
                ordersView.onRestoreInstanceState(stateA);
            }
        }
    }

    public interface CurrencyFragmentListener {
        String getCurrencyToTrade();

        String getKey();

        String getSecret();

        ArrayList<Currency> getCurrencies();
    }
}

