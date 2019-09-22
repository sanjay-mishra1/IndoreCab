package com.example.sanjay.cab.webframe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
 import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sanjay.cab.MainActivity;
import com.example.sanjay.cab.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class WebFrame extends Fragment {
    private RelativeLayout progressBar;
    private WebView webView;
    private TextView toolbar;
    View errorScreen;
    String url;
     String name="";
     private boolean isLogedin=false;
    private int count = 0;
    private SharedPreferences sharedPreferences;
    private boolean endProgress=true;

    public WebFrame(){

}
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.webfragment,container,false);
    progressBar=v.findViewById(R.id.progressBar);
    webView=v.findViewById(R.id.webviewFrag);
    errorScreen=v.findViewById(R.id.error_screen);

      try {
          toolbar =getActivity(). findViewById(R.id.title);
      }catch (Exception ignored){}
        Log.e("WebFrame","Inside webframe"+this.getArguments().getString("URL"));
        url=this.getArguments().getString("URL");


        loadUrl(url);
        v.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorScreen.setVisibility(View.GONE);
                 loadUrl(url);


            }
        });
        v.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();


                  startActivity(new Intent( getContext(),MainActivity.class));


            }
        });
    return v;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        Log.e("WebFrame","Onstart name is "+name);
      if (!name.isEmpty())
          headerName(name,"");
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    void loadUrl(String url){
         progressBar.setVisibility(View.VISIBLE);
try {
           webView.setWebViewClient(new Browse(url));
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

}catch (Exception e){
    e.printStackTrace();
    errorScreen.setVisibility(View.VISIBLE);

}

        if (Build.VERSION.SDK_INT>21){
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }else{
            CookieManager.getInstance().setAcceptCookie(true);
        }


          Log.e("webframe","Inside loadurl url is =>"+url);
//        webView.setDownloadListener(new DownloadListener() {
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });





    }

     void loadFragment(String url){
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        WebFrame web = new WebFrame();
        web.setArguments(bundle);
        FragmentManager FM=Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
        Log.e("webframe","Starting new fragment");
//        Constants.Stack.push(title);
        fragmentTransaction1.add(R.id.fragment, web).commit();
        fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK );

        fragmentTransaction1.addToBackStack(url);
    }

    public class Browse extends WebViewClient {
        String url;
        boolean isCall=false;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //super.onPageStarted(view, url, favicon);

            if (url.contains("tel:+")){
                call(url);
                view.stopLoading();
                isCall=true;
                //view.goBack();


             }else if(!WebFrame.this.url.equals(url)) {
                Log.e("webframe","Creating new fragment");
                 WebFrame.this.url=url;
                  progressBar.setVisibility(View.VISIBLE);
              try {
                  loadFragment(url);
              }catch (Exception e){e.printStackTrace();
                  errorScreen.setVisibility(View.VISIBLE);
              }
                isCall=false;
                webView.stopLoading();
             }else
            {
                WebFrame.this.url=url;
                isCall=false;
                progressBar.setVisibility(View.VISIBLE);
             }

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
//            errorScreen.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("iscall=>","page finished "+isCall);
            progressBar.setVisibility(View.GONE);
            if (!isCall) {
                Log.e("Home", "Title is=>" + view.getTitle());

                Log.e("WebFrame", "Done Loading " + url);


            }else{

                Log.e("iscall","it is true");

            }
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (url.contains("tel:+"))
            {
                Log.e("call","going inside call=>"+url);

            }else
            view.loadUrl(url);

            return true;
        }
    }

    private void call(String url) {
        if (isHaveCallPermision()) {


            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(url));
            startActivity(callIntent);
        }else{
            Toast.makeText(getContext(),"Not have call permission",Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isHaveCallPermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }else{

                return true;
            }
        }else{
            return true;
        }

        }


 }
