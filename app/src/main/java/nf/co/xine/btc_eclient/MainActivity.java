package nf.co.xine.btc_eclient;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity implements QuotesFragment.OnFragmentInteractionListener {


    private boolean editMode = false;
    BottomBar mBottomBar;
    QuotesFragment quotesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quotesFragment = new QuotesFragment();
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
            editMode = quotesFragment.toggleEditMode(editMode);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
