package com.giovannisaberon.simplebible;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BibleJson {

    private Context context;

    public BibleJson(Context context){
        this.context = context;
    }

    public String loadJSONFromAsset(String filename) throws IOException {
        String json = "there is nothing";
        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }
//        Log.i("json bible: ", json);
        return json;
    }

    public String JsonData(){


        String json = "{'Genesis': {'1': [{'1': 'In the beginning God'}, {'2': 'And there was light'}]}," +
                "       'Exodus': {'1': [{'1': 'Moses is born'}, {'2': 'Seven plagues'}]}" +
                "}";

        return json;
    }

    public JSONObject getJsonBible(String bible) throws JSONException {
        JSONObject obj = new JSONObject(bible);
        return obj;
    }

    public JSONObject getBook(JSONObject jsonBible, String book) throws JSONException {
        JSONObject jsonbook = jsonBible.getJSONObject(book);
        return jsonbook;
    }

    public JSONArray getChapter(JSONObject bible, String book, String chapter) throws JSONException {
        JSONObject jsonbook = this.getBook(bible, book);
        JSONArray jsonchapter = jsonbook.getJSONArray(chapter);
        return jsonchapter;
    }

    public String getVerse(JSONArray chapter, int verse) throws JSONException {
        JSONObject v = chapter.getJSONObject(verse);
        String verseNumber = Integer.toString(verse+1);
        String text = v.getString(verseNumber);
        return text;
    }

    public JSONObject getJsonFile(String filename){
        BibleJson jsonfile = new BibleJson(context){};
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

    public BibleData[] loadMarkFinelyVerses(String topicTitle) {

        BibleData[] dataset = new BibleData[0];
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView

        JSONArray jsonarrayreferences = new JSONArray();

        JSONObject topics = getJsonFile("bibletopics.json");
        JSONObject bible = getJsonFile("filename.json");
        try {
            jsonarrayreferences = topics.getJSONArray(topicTitle.toLowerCase());
            dataset = new BibleData[jsonarrayreferences.length()];
            for (int i = 0; i < jsonarrayreferences.length(); i++) {
                try {
                    JSONObject areferenceobject = (JSONObject) jsonarrayreferences.get(i);
                    String book = areferenceobject.getString("book");

                    int chapter = Integer.parseInt(areferenceobject.getString("chapter"));
                    int verse = Integer.parseInt(areferenceobject.getString("verse")) - 1;
                    String verseNumber = Integer.toString(verse + 1);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public BibleData[] loadVerses(String selectedTopic){
        SharedPreferences pref = context.getSharedPreferences("FavoriteVerses", 0);
        Set<String> setofReferences = pref.getStringSet(selectedTopic, new HashSet<String>());
        JSONObject bible = getJsonFile("filename.json");
        Log.i("biblejson", bible.toString());
        pref = context.getSharedPreferences("FavoriteVerses", 0);
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
        return dataSet;

    }
}
