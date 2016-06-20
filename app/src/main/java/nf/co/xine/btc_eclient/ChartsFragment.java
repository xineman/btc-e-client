package nf.co.xine.btc_eclient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.assist.TradeApi;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChartsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AVLoadingIndicatorView progressBar;
    private WebView webView;
    private StringBuilder out = new StringBuilder();
    private String parsed = null;

    public ChartsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charts, container, false);
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
        if (getView() != null) {
            progressBar = (AVLoadingIndicatorView) getView().findViewById(R.id.charts_progress_bar);
            webView = (WebView) getView().findViewById(R.id.webview);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Network().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class Network extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (mListener.isConnectedToNetwork())
                try {
                    URL url = new URL("https://btc-e.com/exchange/btc_usd");
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        out.append(line);
                    }
                    Pattern pattern = Pattern.compile("<script type='text/javascript' src='https://www.google.com/jsapi'></script>(.*?)<div id='chart_div'></div>");
                    Matcher matcher = pattern.matcher(out.toString());
                    if (matcher.find()) {
                        //chart data
                        parsed = matcher.group(0);
                    }
                    rd.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAdded() && parsed != null) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient() {

                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                long startTime = System.currentTimeMillis();
                webView.loadDataWithBaseURL("", parsed, "text/html", "UTF-8", "");

                long endTime = System.currentTimeMillis();

                System.out.println("Chart loading took " + (endTime - startTime) + " milliseconds");
            } else {
                progressBar.setVisibility(View.GONE);
            }
            /*TextView parsedView = (TextView) getView().findViewById(R.id.parsed);
            parsedView.setText(parsed);
            parsedView.setMovementMethod(new ScrollingMovementMethod());*/
        }
    }

    private void showNoNetworkMessage() {
        Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        TradeApi getApi();

        boolean isConnectedToNetwork();

        boolean isPublicInfoReceived();
    }
}
