package com.giovannisaberon.simplebible;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.VerseAdapterListener{
    BibleJson bibleJson;
    String selectedBook = "";
    int selectedChapter = 0;
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
        setContentView(R.layout.activity_main);

        Button favorites_button = (Button) findViewById(R.id.favorites_button);
        favorites_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToFavorites();


            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        // specify an adapter (see also next example)





        final Spinner spinner_books = (Spinner) findViewById(R.id.spinner_books);
        bibleJson = new BibleJson(this){};
        try {
            String bibleString = bibleJson.loadJSONFromAsset();
            bible = bibleJson.getJsonBible(bibleString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        spinner_books.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Resources res = getResources();
                int[] chapters = res.getIntArray(R.array.chapters);
                int totalchapters = chapters[position];
                selectedBook  = parentView.getItemAtPosition(position).toString();
                List<String> chapter_list = new ArrayList<String>();
                for(int x=1; x<=totalchapters; x++){
                    chapter_list.add(Integer.toString(x));
                }
                loadChapters(chapter_list);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    public void loadChapters(List<String> list){
        Spinner spinner_chapters = (Spinner) findViewById(R.id.spinner_chapters);
//        final TextView textview = (TextView) findViewById(R.id.textview);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_chapters.setAdapter(dataAdapter);
        spinner_chapters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String chapter_number = parentView.getItemAtPosition(position).toString();
                try{

                    JSONArray chapter = bibleJson.getChapter(bible, selectedBook, chapter_number);
                    BibleData[] dataset = new BibleData[chapter.length()];

                    recyclerView.setAdapter(mAdapter);
                    for (int i = 0; i < chapter.length(); i++){
                        JSONObject v = chapter.getJSONObject(i);
                        String verseNumber = Integer.toString(i+1);
                        String verse = v.getString(verseNumber);
                        BibleData bibleData = new BibleData(selectedBook, Integer.parseInt(chapter_number), Integer.parseInt(verseNumber), verse);
                        dataset[i] = bibleData;
                    }
                    MyAdapter.VerseAdapterListener listener = new MyAdapter.VerseAdapterListener() {
                        @Override
                        public void onVerseSelected(BibleData bibleData) {
                           fullScreen(bibleData);
                        }
                    };
                    mAdapter = new MyAdapter(dataset, listener, MainActivity.this, "Main");
                    recyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });





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

    public void switchToFavorites(){
        Intent intent = new Intent(this, FavoriteVersesActivity.class);
        startActivity(intent);
    }




}
