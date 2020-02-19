package com.giovannisaberon.simplebible;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BibleJson {

    private Context context;

    public BibleJson(Context context){
        this.context = context;
    }

    public String loadJSONFromAsset() throws IOException {
        String json = "there is nothing";
        AssetManager am = context.getAssets();

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
}
