package nf.co.xine.btc_eclient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrencyFragment extends Fragment {

    private CurrencyFragmentListener mListener;
    private JsonObjectRequest jsObjRequest;
    private JsonObjectRequest summaryRequest;
    private ListView ordersView;
    private Button buyOrderButton;
    private Button sellOrderButton;
    private RadioButton buyRadio;
    private RadioButton sellRadio;
    private View placeOrderDialog;
    String url;
    String summaryUrl;
    private String currencyToTrade;
    final Handler handler = new Handler();
    Runnable makeRequest = new Runnable() {
        public void run() {
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(summaryRequest);
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);
            handler.postDelayed(this, 60000);
        }
    };

    public CurrencyFragment() {
        // Required empty public constructor
    }


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
        a.setDuration(150);
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
        a.setDuration(150);
        v.startAnimation(a);
    }

    //saving data from Json response to arraylists, populating listviews with this araylists
    private void updateOrders(JSONObject response) {
        ArrayList<CurrencyOrder> orders = new ArrayList<>();
        Parcelable stateA = ordersView.onSaveInstanceState();
        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), orders);
        try {
            Log.d("URL:", url);
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

    public void setCurrencyToTrade() {
        currencyToTrade = mListener.getCurrencyToTrade();
        url = "https://btc-e.com/api/3/depth/" + MainActivity.convertName(currencyToTrade) + "?limit=20";
        summaryUrl = "https://btc-e.com/api/3/ticker/" + MainActivity.convertName(mListener.getCurrencyToTrade());
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);
        summaryRequest = new JsonObjectRequest
                (Request.Method.GET, summaryUrl, null, summaryListener, errorListener);
        stopRequests();
        handler.post(makeRequest);
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
        if (getView() != null) {
            ordersView = (ListView) getView().findViewById(R.id.orders_list);
            placeOrderDialog = getView().findViewById(R.id.place_order_dialog);
            buyOrderButton = (Button) getView().findViewById(R.id.new_buy_order);
            sellOrderButton = (Button) getView().findViewById(R.id.new_sell_order);
            buyRadio = (RadioButton) getView().findViewById(R.id.radio_buy);
            sellRadio = (RadioButton) getView().findViewById(R.id.radio_sell);
            buyOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (placeOrderDialog.getVisibility() == View.GONE) expand(placeOrderDialog);
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    buyRadio.setChecked(true);
                }
            });
            sellOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expand(placeOrderDialog);
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    sellRadio.setChecked(true);
                }
            });
            Button cancel = (Button) getView().findViewById(R.id.collapse_order_menu);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapse(placeOrderDialog);
                    buyOrderButton.setVisibility(View.VISIBLE);
                    sellOrderButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currencyToTrade = mListener.getCurrencyToTrade();
        url = "https://btc-e.com/api/3/depth/" + MainActivity.convertName(currencyToTrade) + "?limit=20";
        summaryUrl = "https://btc-e.com/api/3/ticker/" + MainActivity.convertName(mListener.getCurrencyToTrade());
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);
        summaryRequest = new JsonObjectRequest
                (Request.Method.GET, summaryUrl, null, summaryListener, errorListener);
        handler.post(makeRequest);
        Log.d("Currency", "Resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRequests();
        Log.d("Currency", "Paused");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopRequests();
        mListener = null;
    }

    public interface CurrencyFragmentListener {
        String getCurrencyToTrade();

        ArrayList<Currency> getCurrencies();
    }
}

