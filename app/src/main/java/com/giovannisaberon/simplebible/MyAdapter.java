package com.giovannisaberon.simplebible;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
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
        public Button addButton;
        public Button addToTopicButton;

        public MyViewHolder(View v) {
            super(v);
            textView =  v.findViewById(R.id.textView);
            deleteButton = v.findViewById(R.id.delete_button);
            addButton = v.findViewById(R.id.add_button);
            addToTopicButton = v.findViewById(R.id.add__to_playlist_button);


            if (activityType=="favoriteVerses"){
                Log.i("activityType", activityType);
                deleteButton.setVisibility(View.VISIBLE);
                addToTopicButton.setVisibility(View.VISIBLE);

                addToTopicButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        showPopUpDialog();

                    }
                });
            }else{
                addButton.setVisibility(View.VISIBLE);

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final BibleData bibleData = mDataset[position];
        holder.textView.setText(String.valueOf(position+1)+". " + bibleData.getWord());
        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pref = context.getSharedPreferences("FavoriteVerses", 0);
                editor = pref.edit();
                String reference = bibleData.getReference();
                Set<String> set = pref.getStringSet("favoriteVerses", new HashSet<String>());
                set.remove(reference);
                Log.i("delete", reference);
                editor.remove(reference);
                editor.putStringSet("favoriteVerses", set);
                editor.commit();
                mDataset = ArrayUtils.removeElement(mDataset, bibleData);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.length);
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public interface VerseAdapterListener {
        void onVerseSelected(BibleData bibleData);
    }

    public static void showPopUpDialog() {
        final ArrayList selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Set the dialog title
        builder.setTitle("Pick Topic")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.topic_arrays, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.show();
    }

    public static void deleteFavorite(int adapterPosition){



    }

    public interface DeleteItem {
        void delete(int position);
    }

}
