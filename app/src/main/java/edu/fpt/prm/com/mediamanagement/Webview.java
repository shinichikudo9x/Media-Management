package edu.fpt.prm.com.mediamanagement;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends AppCompatActivity {
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        myWebView = (WebView) findViewById(R.id.webview);
        // Configure related browser settings
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Enable responsive layout
        myWebView.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        // Configure the client to use when opening URLs
        myWebView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        String url = getIntent().getStringExtra("extra_text");
        myWebView.loadUrl(url);
    }
    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
