package com.giovannisaberon.simplebible;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteVersesActivity extends AppCompatActivity implements MyAdapter.VerseAdapterListener {

    BibleJson bibleJson;
    BibleJson topicJson;
    JSONObject topics;
    JSONObject bible;
    private SharedPreferences pref;  // 0 - for private mode
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String selectedTopic;

    @Override
    public void onVerseSelected(BibleData bibleData, String activityType) {
        Toast.makeText(getApplicationContext(), "Selected: " + bibleData.getVerse(), Toast.LENGTH_LONG).show();
        pref = this.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        editor.putString("book", bibleData.getBook() );
        editor.putInt("chapter", bibleData.getChapter());
        editor.putInt("verse", bibleData.getVerse());
        editor.putString("word", bibleData.getWord() );
        editor.putString("activityType", activityType);
        editor.commit();
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_verses);
        final Spinner spinner_topics = (Spinner) findViewById(R.id.spinner_topics);
        spinner_topics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Resources res = getResources();
                selectedTopic  = parentView.getItemAtPosition(position).toString();
                Log.i("selectedtopic", selectedTopic);
                BibleData[] dataset = new BibleData[0];
                BibleJson bibleJson = new BibleJson(getApplicationContext());
                if (selectedTopic.equals("All")){
                    dataset = bibleJson.loadVerses(selectedTopic);
                    Log.i("alltopic", selectedTopic);
                }else{
                    Log.i("markfinleytopic", selectedTopic);
                    dataset = bibleJson.loadMarkFinelyVerses(selectedTopic);
                }
                populateAdapter(dataset);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    public void fullScreen(BibleData bibleData, String activityType){
        Toast.makeText(getApplicationContext(), "Selected: " + bibleData.getVerse(), Toast.LENGTH_LONG).show();
        pref = this.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        editor.putString("book", bibleData.getBook() );
        editor.putInt("chapter", bibleData.getChapter());
        editor.putInt("verse", bibleData.getVerse());
        editor.putString("word", bibleData.getWord() );
        editor.putString("activityType", activityType);
        editor.putString("selectedFavoriteTopic", selectedTopic);
        editor.commit();
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }

    public void populateAdapter(BibleData[] dataset){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        MyAdapter.VerseAdapterListener listener = new MyAdapter.VerseAdapterListener() {
            @Override
            public void onVerseSelected(BibleData bibleData, String activityType) {
                fullScreen(bibleData, activityType);
            }


        };
        mAdapter = new MyAdapter(dataset, listener, this, "favoriteVerses");
        recyclerView.setAdapter(mAdapter);
    }


}
