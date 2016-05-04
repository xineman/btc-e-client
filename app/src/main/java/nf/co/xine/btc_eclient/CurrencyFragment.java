package nf.co.xine.btc_eclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CurrencyFragment extends Fragment {

    private CurrencyFragmentListener mListener;
    private JsonObjectRequest jsObjRequest;
    private ListView asksView;
    private ListView bidsView;
    String url;
    final Handler handler = new Handler();
    Runnable makeRequest = new Runnable() {
        public void run() {
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);
            handler.postDelayed(this, 10000);
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

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //saving data from Json response to arraylists, populating listviews with this araylists
    private void updateOrders(JSONObject response) {
        ArrayList<CurrencyOrder> asks = new ArrayList<>();
        ArrayList<CurrencyOrder> bids = new ArrayList<>();
        Parcelable stateA = asksView.onSaveInstanceState();
        Parcelable stateB = bidsView.onSaveInstanceState();
        OrdersAdapter asksAdapter = new OrdersAdapter(getActivity(), asks);
        OrdersAdapter bidsAdapter = new OrdersAdapter(getActivity(), bids);
        try {
            Log.d("URL:",url);
            JSONObject tmp = response.getJSONObject(MainActivity.convertName(mListener.getCurrencyToTrade()));
            JSONArray asksArray = tmp.getJSONArray("asks");
            JSONArray bidsArray = tmp.getJSONArray("bids");
            for (int i = 0; i < asksArray.length(); i++) {
                double askValue = ((JSONArray) asksArray.get(i)).getDouble(0);
                double askAmount = ((JSONArray) asksArray.get(i)).getDouble(1);
                asks.add(new CurrencyOrder(askValue, askAmount));
                double bidValue = ((JSONArray) bidsArray.get(i)).getDouble(0);
                double bidAmount = ((JSONArray) bidsArray.get(i)).getDouble(1);
                bids.add(new CurrencyOrder(bidValue, bidAmount));
            }
            asksView.setAdapter(asksAdapter);
            bidsView.setAdapter(bidsAdapter);
            asksView.onRestoreInstanceState(stateA);
            bidsView.onRestoreInstanceState(stateB);

        } catch (Exception ex) {
            ex.printStackTrace();
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
        asksView = (ListView) getView().findViewById(R.id.ask_list);
        bidsView = (ListView) getView().findViewById(R.id.bid_list);
        url = "https://btc-e.com/api/3/depth/" + MainActivity.convertName(mListener.getCurrencyToTrade()) + "?limit=20";
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }
}

