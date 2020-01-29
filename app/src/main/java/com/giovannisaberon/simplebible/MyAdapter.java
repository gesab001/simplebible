package com.giovannisaberon.simplebible;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static BibleData[] mDataset;
    private static VerseAdapterListener listener;
    private OnSwipeTouchListener onSwipeTouchListener;
    private static Context context;
    private static String activityType;
    private static SharedPreferences pref;  // 0 - for private mode
    private static SharedPreferences.Editor editor;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public Button deleteButton;

        public MyViewHolder(View v) {
            super(v);
            textView =  v.findViewById(R.id.textView);
            deleteButton = v.findViewById(R.id.delete_button);
            pref = context.getSharedPreferences("FavoriteVerses", 0);
            editor = pref.edit();
            if (activityType=="favoriteVerses"){
                Log.i("activityType", activityType);
                deleteButton.setVisibility(View.VISIBLE);

                deleteButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        BibleData bibleData = mDataset[getAdapterPosition()];
                        String reference = bibleData.getReference();
                        Set<String> set = pref.getStringSet("favoriteVerses", new HashSet<String>());
                        set.remove(reference);
                        Log.i("delete", reference);
                        editor.remove(reference);
                        editor.putStringSet("favoriteVerses", set);
                        editor.commit();

                    }
                });
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    String verse = textView.getText().toString();
                    BibleData bibleData = mDataset[getAdapterPosition()];
                    Log.i("book", bibleData.getBook());
                    listener.onVerseSelected(bibleData);
                }
            });
//
//            v.setOnTouchListener(new OnSwipeTouchListener(context){
//                public void onSwipeLeft() {
//                    Log.i("swipe", "left");
//                }
//            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(BibleData[] myDataset, VerseAdapterListener listener, Context context, String activityType) {

        this.mDataset = myDataset;
        this.listener = listener;
        this.context = context;
        this.activityType = activityType;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        BibleData bibleData = mDataset[position];
        holder.textView.setText(String.valueOf(position+1)+". " + bibleData.getWord());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public interface VerseAdapterListener {
        void onVerseSelected(BibleData bibleData);
    }
}
