package nf.co.xine.btc_eclient;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.Objects;

import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CustomAdapter adapter;
    private DragSortListView listView;
    String url = "https://btc-e.com/api/3/ticker/btc_usd-btc_rur";

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

    private JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, listener, errorListener);


    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                HashMap item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
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

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                handler.postDelayed(this, 2000);
            }
        });

    }

    private String convertName(String name) {
        return (name.substring(0, 3) + "/" + name.substring(4, 7)).toUpperCase();
    }

    private void updateQuotes(JSONObject jResponse) {
        ArrayList<HashMap<String, String>> currencies = new ArrayList<>();
        adapter = new CustomAdapter(this, currencies);
        HashMap<String, String> tmp;
        Iterator keys = jResponse.keys();
        while (keys.hasNext()) {
            String p = keys.next().toString();
            tmp = new HashMap<>();
            tmp.put("name", convertName(p));
            try {
                JSONObject object = jResponse.getJSONObject(p);
                tmp.put("ask", truncate(object.getString("buy")));
                tmp.put("bid", truncate(object.getString("sell")));
                currencies.add(tmp);
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
        DragSortController controller = new DragSortController(listView);
        controller.setDragHandleId(R.id.drag_handle);
        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        //controller.setRemoveMode(removeMode);

        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);
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
            listView.setOnTouchListener(null);
            /*
            ImageView img = (ImageView)findViewById(R.id.reorder_icon);
            TextView ask = (TextView)findViewById(R.id.ask);
            TextView bid = (TextView)findViewById(R.id.bid);
            Animation slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
            //img.startAnimation(slideLeft);
            img.setVisibility(View.VISIBLE);
            ask.setVisibility(View.INVISIBLE);
            bid.setVisibility(View.INVISIBLE);

            return true;*/
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
