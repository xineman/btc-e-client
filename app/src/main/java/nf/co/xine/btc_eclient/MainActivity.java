package nf.co.xine.btc_eclient;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.assist.TradeApi;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.data_structure.Currency;


public class MainActivity extends AppCompatActivity implements QuotesFragment.OnFragmentInteractionListener,
        CurrencyFragment.CurrencyFragmentListener,
        ProfileFragment.OnFragmentInteractionListener,
        ActiveOrdersFragment.OnFragmentInteractionListener,
        TransactionsHistoryFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener {

    private BottomBar mBottomBar;
    private QuotesFragment quotesFragment = new QuotesFragment();
    private CurrencyFragment currencyFragment = new CurrencyFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private String currencyToTrade = "btc_usd";
    private Menu menu;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private String aKey = ""; //API-key
    private String aSecret = ""; //SECRET-key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("nonce", String.valueOf(System.currentTimeMillis() / 100L - 14625283416L));
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useFixedMode();
        mBottomBar.setItemsFromMenu(R.menu.activity_main_drawer, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_quotes) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, quotesFragment, "Quotes");
                    transaction.commit();
                    if (menu != null) {
                        menu.clear();
                        getMenuInflater().inflate(R.menu.main, menu);
                    }
                }
                if (menuItemId == R.id.nav_trade) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, currencyFragment, "Trade");
                    transaction.commit();
                    menu.clear();
                    getMenuInflater().inflate(R.menu.trade_menu, menu);
                    MenuItem item = menu.findItem(R.id.spinner);
                    spinner = (Spinner) MenuItemCompat.getActionView(item);
                    spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.currency_spinner_item, quotesFragment.getCurrencies());
                    spinner.setAdapter(spinnerAdapter);
                    spinner.setOnItemSelectedListener(currencyChangeListener);
                    spinner.setSelection(getIndex(spinner, currencyToTrade));
                }

                if (menuItemId == R.id.nav_profile) {
                    profileFragment = new ProfileFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, profileFragment, "Profile");
                    transaction.commit();
                    menu.clear();
                    getMenuInflater().inflate(R.menu.main, menu);
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_trade) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        /*
        ActionBar actionBar = getSupportActionBar();
        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);

        actionBar.setSubtitle("mytest");
        actionBar.setTitle("vogella.com");*/

    }

    public static int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
/*
    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (placeOrderDialog.getVisibility() == View.GONE) expand(placeOrderDialog);
        }
    };
*/


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
        getMenuInflater().inflate(R.menu.trade_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
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
            if (getSupportFragmentManager().findFragmentByTag("Quotes") != null && getSupportFragmentManager().findFragmentByTag("Quotes").isVisible()) {
                quotesFragment.toggleEditMode();
                spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.currency_spinner_item, quotesFragment.getCurrencies());
                spinner.setAdapter(spinnerAdapter);
            }
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

    public AdapterView.OnItemSelectedListener currencyChangeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currencyToTrade = parent.getSelectedItem().toString();
            if (getSupportFragmentManager().findFragmentByTag("Trade") != null && getSupportFragmentManager().findFragmentByTag("Trade").isVisible())
                currencyFragment.setCurrencyToTrade(currencyToTrade);
            Log.d("Changed", currencyToTrade);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public ArrayList<Currency> getCurrencies() {
        return quotesFragment.getCurrencies();
    }

    @Override
    public void showCurrencyFragment(String name) {
        currencyFragment = new CurrencyFragment();
        currencyToTrade = name;
        mBottomBar.selectTabAtPosition(1, true);
    }

    @Override
    public String getKey() {
        return aKey;
    }

    @Override
    public String getSecret() {
        return aSecret;
    }

    public TradeApi getApi() {
        return profileFragment.getApi();
    }
}
