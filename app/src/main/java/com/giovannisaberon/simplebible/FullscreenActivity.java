package com.giovannisaberon.simplebible;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    BibleJson bibleJson;
    JSONObject bible;
    JSONObject bookText;
    JSONArray chapterText;
    private BibleData bibleData;
    private String selectedTopic;
    private String book;
    private int chapter;
    private int verse;
    private String activityType;
    private SharedPreferences pref;  // 0 - for private mode
    private SharedPreferences.Editor editor;
    private BibleData[] dataSet;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bibleJson = new BibleJson(this){};
        try {
            String bibleString = bibleJson.loadJSONFromAsset();
            bible = bibleJson.getJsonBible(bibleString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_fullscreen);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        activityType = pref.getString("activityType", null);
        Log.i("activityTypeScreen", activityType);
//        if (activityType=="favoriteVerses"){
            selectedTopic = pref.getString("selectedFavoriteTopic", null);
            pref = getApplicationContext().getSharedPreferences("FavoriteVerses", 0);

            verse = 0;
            Set<String> setFavoriteReferences = pref.getStringSet(selectedTopic, new HashSet<String>());
            dataSet = new BibleData[setFavoriteReferences.size()];
            int count = 0;
            for(String reference : setFavoriteReferences){
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

//        }
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        book = pref.getString("book", null);
        try {
            bookText = bibleJson.getBook(bible, book);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chapter = pref.getInt("chapter", 0);
        try {
            chapterText =bibleJson.getChapter(bible, book, Integer.toString(chapter));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        verse = pref.getInt("verse", 0);
        final String word = pref.getString("word", null);
        final String reference = book + " " + chapter + ":" + verse;
        bibleData = new BibleData(book, chapter, verse, word);
        mVisible = true;
        displayVerse(bibleData);
        if (activityType.startsWith("favoriteVerses")){

            for (int i=0;i<dataSet.length;i++) {
                if (dataSet[i].equals(bibleData)) {
                    verse = i;
                    break;
                }
            }

        }



            // Set up the user interaction to manually show or hide the system UI.
       // mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        mContentView.setOnTouchListener(new OnSwipeTouchListener(FullscreenActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(FullscreenActivity.this, "top", Toast.LENGTH_SHORT).show();
                toggle();

            }
            public void onSwipeRight() {
                Log.i("swiperight", activityType);
                if (activityType.startsWith("favoriteVerses")){
                    previousFavoriteVerse();
//                    Log.i("previousFavoriteVerse", "previousFavoriteVerse");
//
                }else{
                    previousVerse();
                }



//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, fontminsize, fontmaxsize, 1,
//                        TypedValue.COMPLEX_UNIT_DIP);



            }
            public void onSwipeLeft() {
                Log.i("activityleft", activityType);

                if (activityType.startsWith("favoriteVerses")){
                    nextFavoriteVerse();
//                    Log.i("nextFavoriteVerse", "nextFavoriteVerse");
                }else{
                    nextVerse();
//
                }

//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, fontminsize, fontmaxsize, 1,
//                        TypedValue.COMPLEX_UNIT_DIP);
            }
            public void onSwipeBottom() {
//                Toast.makeText(FullscreenActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                toggle();
            }

        });
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        Button favoriteButton = (Button) findViewById(R.id.dummy_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                BibleData bibleData = new BibleData(book, chapter, verse, word);
                addToFavorites(bibleData);
            }
        });

    }

    public void addToFavorites(BibleData bibleData){
        String reference = bibleData.getReference();
        pref = getApplicationContext().getSharedPreferences("FavoriteVerses", 0);
        JSONArray chapter = null;
        String word = "";
        try {
            chapter = bibleJson.getChapter(bible, bibleData.getBook(),Integer.toString(bibleData.getChapter()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject v = null;
        try {
            v = chapter.getJSONObject(bibleData.getVerse()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//                Log.i("chapter", v.toString());
        try {
            word = v.getString(Integer.toString(bibleData.getVerse()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor = pref.edit();
        Set<String> set = pref.getStringSet("All", new HashSet<String>());
        if (set.contains(reference)){
            Log.i("already exists", reference);
        }else{
            set.add(reference);
            editor.putStringSet("All", set);
            editor.putString(reference, word);
            editor.commit();
        }

        Log.i("set size", Integer.toString(set.size()));


    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void displayVerse(BibleData bibleData){
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        TextView textView = (TextView) mContentView;
        textView.setText(bibleData.getWord() + " " + bibleData.getReferenceQuote());
    }

    private void nextFavoriteVerse(){
        verse = verse + 1;
        if (verse>=dataSet.length-1) {
            verse = dataSet.length - 1;
        }
        bibleData = dataSet[verse];
        displayVerse(bibleData);
    }
    private void nextVerse(){
        verse = verse + 1;
        if (verse>=chapterText.length()-1) {
            verse = chapterText.length() - 2;
        }
        try {

            String word = bibleJson.getVerse(chapterText, verse);
            bibleData = new BibleData(book, chapter, verse, word);
            displayVerse(bibleData);
        } catch (JSONException e) {
            e.printStackTrace();
        }



//        Log.i("swipe left", "swipe left verse " + Integer.toString(verse) );
//        Log.i("activity type", activityType );

    }

    private void previousFavoriteVerse() {
        verse = verse - 1;
        if (verse < 0) {
            verse = 0;
        }
        bibleData = dataSet[verse];
        displayVerse(bibleData);


    }

    private void previousVerse(){
        verse = verse - 1;
        if (verse < 0) {
            verse = 0;
        }
        try {
            String word = bibleJson.getVerse(chapterText, verse);
            bibleData = new BibleData(book, chapter, verse, word);
            displayVerse(bibleData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //                Toast.makeText(FullscreenActivity.this, Integer.toString(verseCount), Toast.LENGTH_SHORT).show();

//        Log.i("swipe right", "verse " + Integer.toString(verse) );
//        Log.i("activity type", activityType );

    }
}
