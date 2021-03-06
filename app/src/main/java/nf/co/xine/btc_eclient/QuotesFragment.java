package nf.co.xine.btc_eclient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import nf.co.xine.btc_eclient.adapters.CustomAdapter;
import nf.co.xine.btc_eclient.data_structure.Currency;

public class QuotesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private CustomAdapter adapter;
    private DragSortListView listView;
    private ArrayList<Currency> currencies;
    private ArrayList<Currency> allCurrencies;
    private String url;
    private JsonObjectRequest jsObjRequest;
    private boolean editMode = false;

    final Handler handler = new Handler();
    Runnable makeRequest = new Runnable() {
        public void run() {
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);
            handler.postDelayed(this, 2000);

        }
    };

    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            updateQuotes(response);
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                Currency item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
                //Collections.swap(allCurrencies,allCurrencies.indexOf(item),to);
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            adapter.remove(adapter.getItem(which));
        }
    };

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView c = (TextView) view.findViewById(R.id.currency_name);
            onItemPressed(c.getText().toString());
        }
    };

    public void onItemPressed(String name) {
        if (mListener != null) {
            mListener.showCurrencyFragment(name);
        }
    }

    public QuotesFragment() {
        // Required empty public constructor
    }

    private boolean toBoolean(String value) {
        return Integer.parseInt(value) != 0;
    }

    private void setCurrencies() {
        try {
            allCurrencies = new ArrayList<>();
            DbHelper helper = new DbHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.query("CURRENCIES", new String[]{"NAME", "IS_ENABLED", "PAIR_INDEX", "ASK_VAL", "BID_VAL"}, null, null, null, null, "PAIR_INDEX ASC");
            while (cursor.moveToNext()) {
                allCurrencies.add(new Currency(cursor.getString(0), toBoolean(cursor.getString(1)), cursor.getString(3), cursor.getString(4)));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setShownCurrencies() {
        currencies = new ArrayList<>();
        for (Currency cc : allCurrencies) {
            if (cc.isEnabled()) currencies.add(cc);
        }
    }

    private class SaveCurrenciesToDb extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {
            long startTime = System.currentTimeMillis();
            saveOrderToDb(params[0]);

            long endTime = System.currentTimeMillis();

            System.out.println("DB work took " + (endTime - startTime) + " milliseconds");
            return null;
        }
    }

    private void saveOrderToDb(Context context) {
        try {
            DbHelper helper = new DbHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            //db.delete("CURRENCIES", null, null);
            for (int i = 0; i < allCurrencies.size(); i++) {
                DbHelper.insertCurrency(db, allCurrencies.get(i).getName(), allCurrencies.get(i).isEnabled(), i, allCurrencies.get(i).getAsk(), allCurrencies.get(i).getBid());
            }
            db.close();
            Log.d("Main act", "Successfully inserted!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String urlBuilder() {
        StringBuilder url = new StringBuilder("https://btc-e.com/api/3/ticker/");
        for (Currency tmp : currencies) {
            url.append(StaticConverter.currencyNameToUrlFormat(tmp.getName())).append("-");
        }
        if (url.charAt(url.length() - 1) == '-')
            url.deleteCharAt(url.length() - 1);
        return url.toString();
    }

    public void startRequests() {
        handler.post(makeRequest);
    }

    public void stopRequests() {
        MySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        handler.removeCallbacks(makeRequest);
    }

    private void updateQuotes(JSONObject jResponse) {
        setShownCurrencies();
        Parcelable state = listView.onSaveInstanceState();
        if (this.isAdded())
            adapter = new CustomAdapter(getActivity(), currencies, CustomAdapter.BROWSING);
        for (Currency cc : allCurrencies) {
            try {
                if (currencies.contains(cc)) {
                    JSONObject object = jResponse.getJSONObject(StaticConverter.currencyNameToUrlFormat(cc.getName()));
                    cc.setAsk(object.getString("buy"));
                    cc.setBid(object.getString("sell"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setShownCurrencies();

        if (listView != null) {
            if (listView.getAdapter() != adapter)
                listView.setAdapter(adapter);
            listView.onRestoreInstanceState(state);
            listView.setDropListener(onDrop);
            listView.setRemoveListener(onRemove);
        }

    }

    public boolean toggleEditMode() {
        if (!editMode) {
            stopRequests();
            DragSortController controller = new DragSortController(listView);
            controller.setDragHandleId(R.id.drag_handle);
            //controller.setClickRemoveId(R.id.);
            controller.setRemoveEnabled(false);
            controller.setSortEnabled(true);
            controller.setDragInitMode(1);
            //controller.setRemoveMode(removeMode);

            adapter = new CustomAdapter(getActivity(), allCurrencies, CustomAdapter.EDIT);
            listView.setAdapter(adapter);
            listView.setFloatViewManager(controller);
            listView.setOnTouchListener(controller);
            listView.setDragEnabled(true);
            SimpleFloatViewManager simpleFloatViewManager = new SimpleFloatViewManager(listView);
            simpleFloatViewManager.setBackgroundColor(Color.WHITE);
            listView.setFloatViewManager(simpleFloatViewManager);
        } else {
            setShownCurrencies();
            adapter = new CustomAdapter(getActivity(), currencies, CustomAdapter.EDITED);
            listView.setAdapter(adapter);
            listView.setOnTouchListener(null);
            url = urlBuilder();
            Log.d("URL", url);
            jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, listener, errorListener);
            handler.postDelayed(makeRequest, 500);
        }
        return editMode = !editMode;
    }

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            if (editMode) toggleEditMode();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrencies();
        setShownCurrencies();
        url = urlBuilder();
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this quotesFragment
        return inflater.inflate(R.layout.fragment_quotes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (DragSortListView) getView().findViewById(R.id.quotes_list);
        listView.setOnTouchListener(null);
        listView.setOnItemClickListener(clickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new CustomAdapter(getActivity(), currencies, CustomAdapter.BROWSING);
        listView.setAdapter(adapter);
        handler.post(makeRequest);
        Log.d("Quotes", "Resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRequests();
        new SaveCurrenciesToDb().execute(getActivity());
        Log.d("Quotes", "Paused");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopRequests();
        Log.d("Quotes", "Detached");
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void showCurrencyFragment(String currencyName);

        boolean isConnectedToNetwork();
    }
}
