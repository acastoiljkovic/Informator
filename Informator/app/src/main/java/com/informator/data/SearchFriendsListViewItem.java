package com.informator.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informator.R;

import java.util.ArrayList;

public class SearchFriendsListViewItem extends ArrayAdapter<String> {


    private Activity context = null;
    private ArrayList<String> fullName = null;
    private ArrayList<Bitmap> images = null;

    public SearchFriendsListViewItem(Activity context,
                                     ArrayList<String> fullName, ArrayList<Bitmap> images) {
        super(context, R.layout.list_view_search_friends_item, fullName);
        this.context = context;
        this.fullName = fullName;
        this.images = images;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_search_friends_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_item_search_friends);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_image_view);

        txtTitle.setText(fullName.get(position));
        if(position >= images.size()){
            imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
        }
        else {
            imageView.setImageBitmap(images.get(position));
        }
        return rowView;
    }

}
