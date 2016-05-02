package nf.co.xine.btc_eclient;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private DragSortListView listView;

    private ArrayList<Currency> currencies;
    private ArrayList<Currency> allCurrencies;
    private boolean editMode = false;
    private String url;
    final Handler handler = new Handler();
    private boolean updateEnabled = true;
    BottomBar mBottomBar;
    private Runnable quotesUpdating = new Runnable() {
        public void run() {
            if (!editMode)
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
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

    private JsonObjectRequest jsObjRequest;

    private boolean toBoolean(String value) {
        return Integer.parseInt(value) != 0;
    }

    private void setCurrencies() {
        try {
            allCurrencies = new ArrayList<>();
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.query("CURRENCIES", new String[]{"NAME", "IS_ENABLED", "PAIR_INDEX"}, null, null, null, null, "PAIR_INDEX ASC");
            while (cursor.moveToNext()) {
                allCurrencies.add(new Currency(cursor.getString(0), toBoolean(cursor.getString(1))));
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

    private void saveOrderToDb() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("CURRENCIES", null, null);
            for (int i = 0; i < allCurrencies.size(); i++) {
                DbHelper.insertCurrency(db, allCurrencies.get(i).getName(), allCurrencies.get(i).isEnabled(), i);
            }
            db.close();
            Log.d("Main act", "Successfully inserted!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String urlBuilder() {
        StringBuilder url = new StringBuilder("https://btc-e.com/api/3/ticker/");
        for (Currency tmp : currencies) {
            url.append(convertName(tmp.getName())).append("-");
        }
        if (url.charAt(url.length() - 1) == '-')
            url.deleteCharAt(url.length() - 1);
        return url.toString();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useFixedMode();
        mBottomBar.setItemsFromMenu(R.menu.activity_main_drawer, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_quotes) {
                    // The user selected item number one.
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_trade) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });
        Log.d("accccccccccc", String.valueOf(mBottomBar.getCurrentTabPosition()));
        setCurrencies();
        setShownCurrencies();
        listView = (DragSortListView) findViewById(R.id.quotes_list);
        url = urlBuilder();
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);

        handler.post(quotesUpdating);

    }

    private static String convertName(String name) {
        return (name.substring(0, 3) + "_" + name.substring(4, 7)).toLowerCase();
    }

    private void updateQuotes(JSONObject jResponse) {
        setShownCurrencies();
        Parcelable state = listView.onSaveInstanceState();
        adapter = new CustomAdapter(this, currencies, CustomAdapter.BROWSING);
        Iterator keys = jResponse.keys();
        for (Currency cc : currencies) {
            String p = keys.next().toString();
            try {
                JSONObject object = jResponse.getJSONObject(p);
                cc.setAsk(object.getDouble("buy"));
                cc.setBid(object.getDouble("sell"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (listView != null) {
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(state);
            listView.setDropListener(onDrop);
            listView.setRemoveListener(onRemove);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(quotesUpdating);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBottomBar.getCurrentTabPosition() == 0)
            handler.post(quotesUpdating);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if (!editMode) {
                handler.removeCallbacks(quotesUpdating);
                editMode = true;
                MySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                DragSortController controller = new DragSortController(listView);
                controller.setDragHandleId(R.id.drag_handle);
                //controller.setClickRemoveId(R.id.);
                controller.setRemoveEnabled(false);
                controller.setSortEnabled(true);
                controller.setDragInitMode(1);
                //controller.setRemoveMode(removeMode);

                adapter = new CustomAdapter(this, allCurrencies, CustomAdapter.EDIT);
                listView.setAdapter(adapter);
                listView.setFloatViewManager(controller);
                listView.setOnTouchListener(controller);
                listView.setDragEnabled(true);
                SimpleFloatViewManager simpleFloatViewManager = new SimpleFloatViewManager(listView);
                simpleFloatViewManager.setBackgroundColor(Color.TRANSPARENT);
                listView.setFloatViewManager(simpleFloatViewManager);
            } else {
                handler.post(quotesUpdating);
                saveOrderToDb();
                setShownCurrencies();
                url = urlBuilder();
                jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, listener, errorListener);
                //MySingleton.getInstance(getApplicationContext()).getRequestQueue().start();
                adapter = new CustomAdapter(this, currencies, CustomAdapter.BROWSING);
                listView.setAdapter(adapter);
                listView.setOnTouchListener(null);
                editMode = false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
