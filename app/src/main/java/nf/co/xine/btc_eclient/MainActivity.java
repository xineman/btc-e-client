package nf.co.xine.btc_eclient;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Handler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CustomAdapter adapter;
    private DragSortListView listView;

    private ArrayList<Currency> currencies;
    private ArrayList<Currency> allCurrencies;
    private boolean editMode = false;
    private String url;
    final Handler handler = new Handler();

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
            Log.d("Main act", helper.getDatabaseName());
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
                adapter.setMode(CustomAdapter.DROPPING);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        android.app.ActionBar actionBar = getActionBar();
        Spinner spinner = (Spinner) findViewById(R.id.tabs);
        actionBar.setCustomView(spinner);
        actionBar.setDisplayShowCustomEnabled(true);
        */

        setCurrencies();
        setShownCurrencies();
        url = urlBuilder();
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);

        handler.post(new Runnable() {
            public void run() {
                if (!editMode)
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                handler.postDelayed(this, 2000);
            }
        });

    }

    private static String convertName(String name) {
        return (name.substring(0, 3) + "_" + name.substring(4, 7)).toLowerCase();
    }

    private void updateQuotes(JSONObject jResponse) {
        setShownCurrencies();
        adapter = new CustomAdapter(this, currencies, CustomAdapter.BROWSING);
        HashMap<String, String> tmp;
        Iterator keys = jResponse.keys();
        for (Currency cc: currencies) {
            String p = keys.next().toString();
            try {
                JSONObject object = jResponse.getJSONObject(p);
                cc.setAsk(object.getDouble("buy"));
                cc.setBid(object.getDouble("sell"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listView = (DragSortListView) findViewById(R.id.quotes_list);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setDropListener(onDrop);
            listView.setRemoveListener(onRemove);
        }

    }

    String truncate(String original) {
        if (original.length() == 7) return original;
        else if (original.length() > 7)
            if (original.indexOf(".") < 8)
                return original.substring(0, 7);
            else return original.substring(0, original.indexOf("."));
        else {
            int n = 7 - original.length();
            if (original.contains("."))
                for (int i = 0; i < n; i++)
                    original = original.concat("0");
            else {
                original = original.concat(".");
                for (int i = 0; i < n - 1; i++)
                    original = original.concat("0");
            }
            return original;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                editMode = true;
                MySingleton.getInstance(getApplicationContext()).getRequestQueue().stop();
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
            } else {
                saveOrderToDb();
                setShownCurrencies();
                url = urlBuilder();
                jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, listener, errorListener);
                MySingleton.getInstance(getApplicationContext()).getRequestQueue().start();
                adapter = new CustomAdapter(this, currencies, CustomAdapter.BROWSING);
                listView.setAdapter(adapter);
                listView.setOnTouchListener(null);
                editMode = false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quotes) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
