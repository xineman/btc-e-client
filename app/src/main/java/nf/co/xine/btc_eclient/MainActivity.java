package nf.co.xine.btc_eclient;

import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.res.ResourcesCompat;
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
import nf.co.xine.btc_eclient.data_structure.CurrencyBalance;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;
import nf.co.xine.btc_eclient.data_structure.MyOrder;
import nf.co.xine.btc_eclient.data_structure.Transaction;


public class MainActivity extends AppCompatActivity implements QuotesFragment.OnFragmentInteractionListener,
        CurrencyFragment.CurrencyFragmentListener,
        ProfileFragment.OnFragmentInteractionListener,
        ActiveOrdersFragment.OnFragmentInteractionListener,
        TransactionsHistoryFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener,
        MarketFragment.OnFragmentInteractionListener,
        MarketHistoryFragment.OnFragmentInteractionListener,
        ChartsFragment.OnFragmentInteractionListener {

    private BottomBar mBottomBar;
    private QuotesFragment quotesFragment = new QuotesFragment();
    private CurrencyFragment currencyFragment = new CurrencyFragment();
    private MarketFragment marketFragment = new MarketFragment();
    private ProfileFragment profileFragment;
    private String currencyToTrade = "BTC/USD";
    private Menu menu;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private String aKey = ""; //API-key
    private String aSecret = ""; //SECRET-key
    private TradeApi api;

    private ArrayList<Transaction> transactions;
    private ArrayList<MyOrder> orders;
    private ArrayList<String> pairs;
    private ArrayList<String> types;
    private ArrayList<CurrencyBalance> balance;
    private ArrayList<CurrencyOrder> marketOrders;
    private ArrayList<String> marketSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            api = new TradeApi(aKey, aSecret);
            new GetPublicInfo().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    transaction.replace(R.id.main_content, marketFragment, "Trade");
                    //transaction.addToBackStack(null);
                    transaction.commit();
                    getSupportFragmentManager().executePendingTransactions();
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
                    if (profileFragment == null)
                        profileFragment = new ProfileFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, profileFragment, "Profile");
                    //transaction.addToBackStack(null);
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

    // TODO: 21.05.2016 Save received info to DB, ensure that info available before using it
    private class GetPublicInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Info", String.valueOf(api.info.isSuccess()));
            api.info.runMethod();
            api.info.switchNextPair();
            return null;
        }

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
                if (quotesFragment.toggleEditMode()) {
                    menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_white_24dp, null));
                } else {
                    menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_white_24dp, null));
                }
                spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.currency_spinner_item, quotesFragment.getCurrencies());
                spinner.setAdapter(spinnerAdapter);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                marketFragment.updateCurrencyToTrade();
            //currencyFragment.getUpdateCurrency().execute();
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
        /*if (!currencyToTrade.equals(name))
            currencyFragment = new CurrencyFragment();*/
        currencyToTrade = name;
        mBottomBar.selectTabAtPosition(1, true);
    }

    public TradeApi getApi() {
        return api;
    }

    @Override
    public void saveMarketOrders(ArrayList<CurrencyOrder> orders, ArrayList<String> summary) {
        marketOrders = orders;
        marketSummary = summary;
    }

    @Override
    public ArrayList<CurrencyOrder> getOrders() {
        return marketOrders;
    }

    @Override
    public ArrayList<String> getSummary() {
        return marketSummary;
    }

    @Override
    public void saveBalance(ArrayList<CurrencyBalance> balances) {
        balance = balances;
    }

    @Override
    public ArrayList<CurrencyBalance> getBalance() {
        return balance;
    }

    public void saveTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void saveActiveOrders(ArrayList<MyOrder> orders, ArrayList<String> pairs, ArrayList<String> types) {
        this.orders = orders;
        this.types = types;
        this.pairs = pairs;
    }

    public ArrayList<MyOrder> getActiveOrders() {
        return orders;
    }

    public ArrayList<String> getPairs() {
        return pairs;
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}
