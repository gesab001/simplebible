package com.giovannisaberon.simplebible;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FavoriteVersesActivity extends AppCompatActivity implements MyAdapter.VerseAdapterListener {

    BibleJson bibleJson;
    JSONObject bible;
    private SharedPreferences pref;  // 0 - for private mode
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onVerseSelected(BibleData bibleData) {
        Toast.makeText(getApplicationContext(), "Selected: " + bibleData.getVerse(), Toast.LENGTH_LONG).show();
        pref = this.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        editor.putString("book", bibleData.getBook() );
        editor.putInt("chapter", bibleData.getChapter());
        editor.putInt("verse", bibleData.getVerse());
        editor.putString("word", bibleData.getWord() );

        editor.commit();
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_verses);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        bibleJson = new BibleJson(this){};
        try {
            String bibleString = bibleJson.loadJSONFromAsset();
            bible = bibleJson.getJsonBible(bibleString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pref = getApplicationContext().getSharedPreferences("FavoriteVerses", 0);
        Set<String> setofReferences = pref.getStringSet("favoriteVerses", new HashSet<String>());
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
            public void onVerseSelected(BibleData bibleData) {
                fullScreen(bibleData);
            }
        };
        mAdapter = new MyAdapter(dataSet, listener);
        recyclerView.setAdapter(mAdapter);
    }

    public void fullScreen(BibleData bibleData){
        Toast.makeText(getApplicationContext(), "Selected: " + bibleData.getVerse(), Toast.LENGTH_LONG).show();
        pref = this.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        editor.putString("book", bibleData.getBook() );
        editor.putInt("chapter", bibleData.getChapter());
        editor.putInt("verse", bibleData.getVerse());
        editor.putString("word", bibleData.getWord() );

        editor.commit();
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }
}
