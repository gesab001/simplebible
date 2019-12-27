package com.giovannisaberon.simplebible;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BibleJson bibleJson;
    String bible = null;
    String selectedBook = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spinner_books = (Spinner) findViewById(R.id.spinner_books);
        bibleJson = new BibleJson(this){};
        try {
            bible = loadJSONFromAsset();
        } catch (IOException e) {
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
        final TextView textview = (TextView) findViewById(R.id.textview);

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
                    textview.setText("");
                    for (int i = 0; i < chapter.length(); i++){
                        JSONObject v = chapter.getJSONObject(i);
                        String verseNumber = Integer.toString(i+1);
                        String verse = v.getString(verseNumber);
                        textview.append(verse+"\n");
                    }
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

    public String loadJSONFromAsset() throws IOException {
        String json = "there is nothing";
        AssetManager am = this.getAssets();

        try {
            InputStream is = am.open("filename.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }
        Log.i("json bible: ", json);
        return json;
    }
}
