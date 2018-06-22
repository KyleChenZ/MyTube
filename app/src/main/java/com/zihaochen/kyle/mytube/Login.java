package com.zihaochen.kyle.mytube;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoPlayer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout activity_main_home;
    private LinearLayout activity_main_trending;
    private LinearLayout activity_main_subscriptions;
    private LinearLayout activity_main_playlist;
    private ImageView activity_main_search;


    private ImageView activity_main_home_img;
    private ImageView activity_main_trending_img;
    private ImageView activity_main_subscriptions_img;
    private ImageView activity_main_playlist_img;


    private TextView activity_main_home_text;
    private TextView activity_main_trending_text;
    private TextView activity_main_subscriptions_text;
    private TextView activity_main_playlist_text;

    /**
     * 顶部titlebar控件
     */
    private ImageView activity_main_bar_logo;
    private CircleImageView activity_main_avtor;
    public Integer currentState = 0;

    /**
     * 侧滑菜单控件
     */
    //private DrawerLayout mDrawerLayout;
    //private View v_menu_left, v_menu_right;

    private static final String TAG = "Login";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;


    //private static String API_KEY = "AIzaSyAKpu0oSNJSNbug29bNd0IqTp5X_QciFlI";
    public static YouTube mytube;
    public Button searchButton;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        // Views


        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        // [END customize_button]

    }
    protected void onStart () {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        home();
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]
    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]
    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.search_out).setVisibility(View.GONE);
            findViewById(R.id.activity_main_search).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_main_title).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_main_avtor).setVisibility(View.VISIBLE);
            findViewById(R.id.search).setVisibility(View.VISIBLE);

        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.search_out).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_main_title).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.activity_main_avtor).setVisibility(View.GONE);
            findViewById(R.id.activity_main_search).setVisibility(View.GONE);
            findViewById(R.id.search).setVisibility(View.GONE);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    private void initView() {
        activity_main_home = findViewById(R.id.activity_main_home);
        activity_main_trending = findViewById(R.id.activity_main_trending);
        activity_main_subscriptions = findViewById(R.id.activity_main_subscriptions);
        activity_main_playlist = findViewById(R.id.activity_main_playlist);
        activity_main_search = findViewById(R.id.activity_main_search);

        activity_main_home.setOnClickListener(this);
        activity_main_trending.setOnClickListener(this);
        activity_main_subscriptions.setOnClickListener(this);
        activity_main_playlist.setOnClickListener(this);
        activity_main_search.setOnClickListener(this);

        activity_main_home_img = findViewById(R.id.activity_main_home_img);
        activity_main_trending_img = findViewById(R.id.activity_main_trending_img);
        activity_main_subscriptions_img = findViewById(R.id.activity_main_subscriptions_img);
        activity_main_playlist_img = findViewById(R.id.activity_main_playlist_img);

        activity_main_home_text = findViewById(R.id.activity_main_home_text);
        activity_main_trending_text = findViewById(R.id.activity_main_trending_text);
        activity_main_subscriptions_text = findViewById(R.id.activity_main_subscriptions_text);
        activity_main_playlist_text = findViewById(R.id.activity_main_playlist_text);


        activity_main_bar_logo = findViewById(R.id.activity_main_bar_logo);
        activity_main_avtor = findViewById(R.id.activity_main_avtor);

        activity_main_bar_logo.setOnClickListener(this);
        activity_main_avtor.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if (drawerlayoutOpen()) return true;
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();

                break;
            //case R.id.sign_out_button:
            //    signOut();
            //    break;

            case R.id.activity_main_home:
                //if (drawerlayoutOpen()) break;
                resetNavState();
                activity_main_home_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.home_do,null));
                activity_main_home_text.setTextColor(Color.parseColor("#d81e06"));
                currentState=0;
                home();
                break;
            case R.id.activity_main_trending:
                //if (drawerlayoutOpen()) break;
                resetNavState();
                activity_main_trending_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trending_do,null));
                activity_main_trending_text.setTextColor(Color.parseColor("#d81e06"));
                currentState=1;
                trending();
                break;
            case R.id.activity_main_subscriptions:
                //if (drawerlayoutOpen()) break;
                resetNavState();
                activity_main_subscriptions_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.subscriptions_do,null));
                activity_main_subscriptions_text.setTextColor(Color.parseColor("#d81e06"));
                currentState=2;
                subscriptions();
                break;
            case R.id.activity_main_playlist:
                //if (drawerlayoutOpen()) break;
                resetNavState();
                activity_main_playlist_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.playlist_do,null));
                activity_main_playlist_text.setTextColor(Color.parseColor("#d81e06"));
                currentState=3;
                playlist();
                break;

            case R.id.activity_main_bar_logo:
                resetNavState();
                activity_main_home_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.home_do,null));
                activity_main_home_text.setTextColor(Color.parseColor("#d81e06"));
                home();
                break;
            case R.id.activity_main_avtor:
                //toggleRight();
                //startActivity(new Intent(Login.this, Login.class));
                resetNavState();
                activity_main_home_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.home_do,null));
                activity_main_home_text.setTextColor(Color.parseColor("#d81e06"));
                home();
                signOut();
                break;
            case R.id.activity_main_search:
                //toggleRight();
                //startActivity(new Intent(Login.this, Login.class));
                currentState = 4;
                resetNavState();
                search_video();

                break;

        }
    }

    private void resetNavState() {
        activity_main_home_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.home_undo,null));
        activity_main_home_text.setTextColor(Color.parseColor("#000000"));

        activity_main_trending_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trending_undo,null));
        activity_main_trending_text.setTextColor(Color.parseColor("#000000"));

        activity_main_subscriptions_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.subscriptions_undo,null));
        activity_main_subscriptions_text.setTextColor(Color.parseColor("#000000"));

        activity_main_playlist_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.playlist_undo,null));
        activity_main_playlist_text.setTextColor(Color.parseColor("#000000"));
    }

    private void home(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void trending() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TrendingFragment fragment = new TrendingFragment();
        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void subscriptions(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SubscriptionFragment fragment = new SubscriptionFragment();
        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        fragmentTransaction.commit();
    }
    private void playlist(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlaylistFragment fragment = new PlaylistFragment();
        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        fragmentTransaction.commit();
    }
    private void search_video(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SearchFragment fragment = new SearchFragment();
        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        fragmentTransaction.commit();
    }


    public void startPlay(int position){
        Log.d("he","5");
        Intent intent = new Intent(this, VideoPlayer.class);
        String id="";
        Log.d("hel","1");
        switch(currentState){
            case 0:
                HomeFragment fragment = (HomeFragment) getFragmentManager().findFragmentById(R.id.fragment_home_recycler_view);
                id = fragment.getIdString(position);
                break;
            case 1:
                TrendingFragment fragment1 = (TrendingFragment) getFragmentManager().findFragmentById(R.id.fragment_trending_recycler_view);
                id = fragment1.getIdString(position);
                break;
            case 2:
                SubscriptionFragment fragment2 = (SubscriptionFragment) getFragmentManager().findFragmentById(R.id.fragment_subscription_recycler_view);
                id = fragment2.getIdString(position);
                break;
            case 3:
                PlaylistFragment fragment3 = (PlaylistFragment) getFragmentManager().findFragmentById(R.id.fragment_playlist_recycler_view);
                id = fragment3.getIdString(position);
                break;
            case 4:
                SearchFragment fragment4 = (SearchFragment) getFragmentManager().findFragmentById(R.id.fragment_search_recycler_view);
                id = fragment4.getIdString(position);
                break;
        }
        Log.d("hello","2");
        Bundle b = new Bundle();
        b.putString("key", id); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }
}
