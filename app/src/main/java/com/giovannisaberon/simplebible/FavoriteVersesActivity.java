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
                if (selectedTopic.equals("All")){
                    pref = getApplicationContext().getSharedPreferences("FavoriteVerses", 0);
                    Set<String> set = pref.getStringSet(selectedTopic, new HashSet<String>());
                    loadVerses(set);
                    Log.i("alltopic", selectedTopic);

                }else{
                    Log.i("markfinleytopic", selectedTopic);
                    loadMarkFinelyVerses(selectedTopic);


                }
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

    public JSONObject getJsonFile(String filename){
        BibleJson jsonfile = new BibleJson(this){};
        JSONObject jsonObject = null;
        try {
            String bibleString = jsonfile.loadJSONFromAsset(filename);
            jsonObject = jsonfile.getJsonBible(bibleString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void loadVerses(Set<String> setofReferences){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        bible = getJsonFile("filename.json");
        Log.i("biblejson", bible.toString());



        pref = getApplicationContext().getSharedPreferences("FavoriteVerses", 0);
        BibleData[] dataSet = new BibleData[setofReferences.size()];
        int count = 0;
        for(String reference : setofReferences){
            Log.i("reference", reference);
            String[] verseReference = reference.split(",");
            String book = verseReference[0];
            int chapterNumber = Integer.parseInt(verseReference[1]);
            int verse = Integer.parseInt(verseReference[2]);
            Log.i("book", book);
            Log.i("chapter", Integer.toString(chapterNumber));
            Log.i("verse", Integer.toString(verse));

//                JSONArray chapter = bibleJson.getChapter(bible, book, Integer.toString(chapterNumber));
//                JSONObject v = chapter.getJSONObject(verse-1);
//                Log.i("chapter", v.toString());
            String word = pref.getString(reference, null);
//                Log.i("word", word);
            if (word!=null){
                BibleData bibleData = new BibleData(book, chapterNumber, verse, word);
                dataSet[count] = bibleData;
                count = count + 1;
            }




        }
        MyAdapter.VerseAdapterListener listener = new MyAdapter.VerseAdapterListener() {
            @Override
            public void onVerseSelected(BibleData bibleData, String activityType) {
                fullScreen(bibleData, activityType);
            }


        };
        mAdapter = new MyAdapter(dataSet, listener, this, "favoriteVerses");
        recyclerView.setAdapter(mAdapter);
    }

    public void loadMarkFinelyVerses(String topicTitle){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        BibleData[] dataset;
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        JSONArray jsonarrayreferences = new JSONArray();

        topics = getJsonFile("bibletopics.json");
        bible = getJsonFile("filename.json");
        try {
            jsonarrayreferences = topics.getJSONArray(topicTitle.toLowerCase());
            dataset = new BibleData[jsonarrayreferences.length()];
            for (int i =0; i<jsonarrayreferences.length(); i++){
                try {
                    JSONObject areferenceobject = (JSONObject) jsonarrayreferences.get(i);
                    String book = areferenceobject.getString("book");

                    int chapter = Integer.parseInt(areferenceobject.getString("chapter"));
                    int verse = Integer.parseInt(areferenceobject.getString("verse"))-1;
                    String verseNumber = Integer.toString(verse+1);
                    String chapterNumber = Integer.toString(chapter);
                    String word = bible.getJSONObject(book).getJSONArray(chapterNumber).getJSONObject(verse).getString(verseNumber);
                    Log.i("word", word.toString());
                    BibleData bibleData = new BibleData(book, chapter, verse, word);
                    dataset[i] = bibleData;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("bibletopicsjson", jsonarrayreferences.toString());

            MyAdapter.VerseAdapterListener listener = new MyAdapter.VerseAdapterListener() {
                @Override
                public void onVerseSelected(BibleData bibleData, String activityType) {
                    fullScreen(bibleData, activityType);
                }


            };
            mAdapter = new MyAdapter(dataset, listener, this, "favoriteVerses");
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
