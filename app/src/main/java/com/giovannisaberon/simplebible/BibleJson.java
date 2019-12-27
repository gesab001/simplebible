package com.giovannisaberon.simplebible;

import android.content.Context;
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

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("filename.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.i("json bible: ", json);
        return json;
    }

    public String JsonData(){


        String json = "{'Genesis': {'1': [{'1': 'In the beginning God'}, {'2': 'And there was light'}]}," +
                "       'Exodus': {'1': [{'1': 'Moses is born'}, {'2': 'Seven plagues'}]}" +
                "}";

        return json;
    }

    public JSONObject getJsonBible(Context context) throws JSONException {
        JSONObject obj = new JSONObject(this.loadJSONFromAsset(context));
        return obj;
    }

    public JSONObject getBook(String bible, String book) throws JSONException {
        JSONObject jsonBible = new JSONObject(bible);
        JSONObject jsonbook = jsonBible.getJSONObject(book);
        return jsonbook;
    }

    public JSONArray getChapter(String bible, String book, String chapter) throws JSONException {
        JSONObject jsonbook = this.getBook(bible, book);
        JSONArray jsonchapter = jsonbook.getJSONArray(chapter);
        return jsonchapter;
    }
}
