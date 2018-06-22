package com.zihaochen.kyle.mytube;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TrendingFragment extends Fragment {

    public TrendingFragment(){

    }
    String searchItem ;
    YouTube.Search.List searchList;
    public TextView text;
    public TextView input;
    public ImageView button2;
    public ImageView thumb;
    public ImageView like;
    public ImageView sub;
    public TextView title;
    public List<String> name;
    public List <String> images;
    ImageView image1;
    String url;
    View view;
    int position;
    List<String> videoName;
    List<Drawable> thumbnailImage;
    List<Drawable> likeImage;
    List<Drawable> subImage;
    private List<String> info = new ArrayList<>();
    public YouTubeConfig yout;
    private static String API_KEY = "AIzaSyAsHVYF92p2z1k_sg7bphAl2B5kEBTraJ0";
    private String api = yout.getApiKey();
    YouTube mytube;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        recyclerView = view.findViewById(R.id.fragment_trending_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoName = new ArrayList<>();
        thumbnailImage = new ArrayList<>();
        likeImage = new ArrayList<>();
        subImage = new ArrayList<>();
        info.add("Default");
        input = getActivity().findViewById(R.id.search);
        title = view.findViewById(R.id.videoTitle);
        thumb = view.findViewById(R.id.thumdnailImage);
        like = view.findViewById(R.id.like);
        sub = view.findViewById(R.id.subscription);
        button2 = getActivity().findViewById(R.id.activity_main_search);
        //image1 = findViewById(R.id.output_image);
        //text = findViewById(R.id.outputText);
        mytube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer()  {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("MyTube").build();
        try {
            searchList = mytube.search().list("snippet");
        }catch(IOException ie){ ie.printStackTrace();}
        searchList.setKey(API_KEY);
        searchList.setType("video");
        searchList.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        searchList.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        // set up the RecyclerView


        String title;
        Drawable image2;
        Drawable mlike;
        Drawable msub;
        searchItem = "basketball";
        searchList.setQ(searchItem);
        //Log.d("search item" , searchItem);
        try {
            SearchListResponse searchResponse = searchList.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                int i=0;
                videoName.clear();
                thumbnailImage.clear();
                subImage.clear();
                likeImage.clear();
                info.clear();
                while (i!= NUMBER_OF_VIDEOS_RETURNED) {
                    Log.d("item:",Integer.toString(i));
                    title = new String(searchResultList.get(i).getSnippet().getTitle().toString());
                    videoName.add(title);
                    url = searchResultList.get(i).getSnippet().getThumbnails().getDefault().getUrl();
                    image2= LoadImageFromWebOperations(url);
                    thumbnailImage.add(image2);
                    info.add(searchResultList.get(i).getId().getVideoId().toString());
                    mlike = new Drawable() {
                        @Override
                        public void draw(@NonNull Canvas canvas) {

                        }
                        @Override
                        public void setAlpha(int alpha) {
                        }
                        @Override
                        public void setColorFilter(@Nullable ColorFilter colorFilter) {
                        }
                        @Override
                        public int getOpacity() {
                            return 0;
                        }
                    };
                    msub = new Drawable() {
                        @Override
                        public void draw(@NonNull Canvas canvas) {
                        }
                        @Override
                        public void setAlpha(int alpha) {
                        }
                        @Override
                        public void setColorFilter(@Nullable ColorFilter colorFilter) {
                        }
                        @Override
                        public int getOpacity() {
                            return 0;
                        }
                    };
                    subImage.add(msub);
                    likeImage.add(mlike);
                    i++;
                }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        adapter = new videoListAdapter(getContext(), videoName,thumbnailImage,likeImage,subImage);
        //  adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
    public String getIdString(int position){
        return info.get(position);
    }


}
