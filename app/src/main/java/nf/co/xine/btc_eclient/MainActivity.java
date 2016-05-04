package nf.co.xine.btc_eclient;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity implements QuotesFragment.OnFragmentInteractionListener, CurrencyFragment.CurrencyFragmentListener {

    private BottomBar mBottomBar;
    private QuotesFragment quotesFragment = new QuotesFragment();;
    private CurrencyFragment currencyFragment = new CurrencyFragment();
    private String currencyToTrade = "btc_usd";

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
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, quotesFragment);
                    transaction.commit();
                }
                if (menuItemId == R.id.nav_trade) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, currencyFragment);
                    transaction.commit();
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_trade) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
            quotesFragment.toggleEditMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static String convertName(String name) {
        return (name.substring(0, 3) + "_" + name.substring(4, 7)).toLowerCase();
    }

    @Override
    public String getCurrencyToTrade() {
        return currencyToTrade;
    }

    @Override
    public void showCurrencyFragment(String name) {
        currencyFragment = new CurrencyFragment();
        currencyToTrade = name;
        mBottomBar.selectTabAtPosition(1, true);
    }
}
