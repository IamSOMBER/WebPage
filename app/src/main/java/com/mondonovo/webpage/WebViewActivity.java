/*
created by Cruciani Mirko for MondoNovoSRL
it creates a webview in which you can navigate only a previously defined url and open links
referring that site
 */

package com.mondonovo.webpage;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    /*
    IMPORTANT: By default, requests to open new windows are ignored.
    This is true whether they are opened by JavaScript or by the target attribute in a link
     */
    WebView siteWebView;

    /*
    ENTER HERE THE URL. It should be an https site preferably, this is not the case
    so in the manifest android:usesCleartextTraffic="true" is needed
    */
    static String URL = "http://www.campingdewijnstok.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        //we don't need action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        siteWebView = findViewById(R.id.webview);

        //Disabling JavaScript (useful??)
        WebSettings webSettings = siteWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_to_update);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isConnected()) {
                webViewHelper();
                swipeRefreshLayout.setRefreshing(false);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(siteWebView, R.string.no_connection, Snackbar.LENGTH_SHORT).show();
            }
        });

        //check if internet connection is already active (either wifi or mobile)
        if (isConnected()) {
            webViewHelper();
        } else {
            AlertDialog checkConnectionDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.no_connection)
                    .setMessage(R.string.swipe_to_update)
                    // close this app
                    .setNegativeButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create();
            checkConnectionDialog.show();
        }
    }

    private void webViewHelper() {

        siteWebView.loadUrl(URL);

        /*
        When the user clicks a link from a web page in your WebView,
        the default behavior is for Android to launch an app that handles URLs.
        Usually, the default web browser opens and loads the destination URL.
        However, you can override this behavior for your WebView, so links open
         within your WebView. You can then allow the user to navigate backward and
        forward through their web page history that's maintained by your WebView
        */
        siteWebView.setWebViewClient(new CustomViewClient());
    }

    private static class CustomViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            return false;
        }
    }

    /*
    Navigating web page history: When your WebView overrides URL loading,
    it automatically accumulates a history of visited web pages.
    You can navigate backward and forward through the history with goBack() and goForward().
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && siteWebView.canGoBack()) {
            siteWebView.goBack();
            return true;
        }

        /*
        If it wasn't the Back key or there's no web page history, bubble up to the default
        system behavior (exit the activity)
        */
        return super.onKeyDown(keyCode, event);
    }

    private boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        /*
        returns a NetworkInfo instance representing the first connected network interface
        it can find, or null if none of the interfaces is connected (meaning that an
        internet connection is not available)
        */
        return networkInfo != null && networkInfo.isConnected();
    }
}