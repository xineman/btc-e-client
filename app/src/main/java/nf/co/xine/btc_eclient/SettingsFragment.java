package nf.co.xine.btc_eclient;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assist.TradeApi;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import nf.co.xine.btc_eclient.adapters.SettingsAdapter;
import nf.co.xine.btc_eclient.data_structure.Parameter;


public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Parameter> parameters = new ArrayList<>();
    private ListView settingsList;
    private TextView apiText;
    private String user;
    private String secret;
    private View apiKeyLayout;
    private String key;
    private TradeApi test;
    private LinearLayout linearLayout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public void initParameters() {
        parameters.clear();
        parameters.add(new Parameter("Interval (seconds)", "5"));
        parameters.add(new Parameter("Orders limit", String.valueOf(mListener.getRequestLimit())));
        key = mListener.getKey();
        apiText = (TextView) getView().findViewById(R.id.api_key_text);
        if (key == null || key.length() < 10)
            apiText.setText(getResources().getString(R.string.sign_in));
        else
            apiText.setText(key.substring(0, 8) + "*");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_signin, null);
        TextView userView = (TextView) linearLayout.findViewById(R.id.username);
        TextView secretView = (TextView) linearLayout.findViewById(R.id.password);
    }

    @Override
    public void onResume() {
        super.onResume();
        initParameters();

        apiKeyLayout = getView().findViewById(R.id.api_key_layout);
        apiKeyLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (key == null || key.length() < 10) {
                    new AlertDialog.Builder(getActivity())
                            .setView(linearLayout)
                            .setTitle("Sign in")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    TextView userView = (TextView) linearLayout.findViewById(R.id.username);
                                    TextView secretView = (TextView) linearLayout.findViewById(R.id.password);
                                    user = userView.getText().toString();
                                    secret = secretView.getText().toString();
                                    new TestApi().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Would you like to sign out?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mListener.signOut();
                                    initParameters();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return false;

            }
        });
        settingsList = (ListView) getView().findViewById(R.id.settings_list);
        settingsList.setAdapter(new SettingsAdapter(getActivity(), parameters));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.setRequestLimit(Integer.parseInt(parameters.get(1).getValue()));
        mListener = null;
    }

    private class TestApi extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                test = new TradeApi(user, secret);
                test.getInfo.runMethod();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (test.getInfo.isSuccess()) {
                mListener.signIn(user, secret);
                initParameters();
                Toast.makeText(getActivity(), "Signed in with " + user.substring(0, 8) + "*", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Couldn't sign in, try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        String getKey();

        String getSecret();

        void setKey(String aKey);

        void setSecret(String aSecret);

        void signOut();

        void signIn(String key, String secret);

        int getRequestLimit();

        void setRequestLimit(int requestLimit);
    }
}
