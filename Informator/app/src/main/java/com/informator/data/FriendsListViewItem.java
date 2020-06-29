package com.informator.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informator.R;

import java.util.ArrayList;

public class FriendsListViewItem extends ArrayAdapter<String> {


    private Activity context = null;
    private ArrayList<String> fullName = null;
    private ArrayList<String> usernames = null;
    private MapPicturesWithName images = null;

    public FriendsListViewItem(Activity context,
                                     ArrayList<String> fullName, ArrayList<String> usernames, MapPicturesWithName images) {
        super(context, R.layout.list_view_search_friends_item, fullName);
        this.context = context;
        this.fullName = fullName;
        this.usernames = usernames;
        this.images = images;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_friends, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_item_search_friends);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_image_view);

        txtTitle.setText(fullName.get(position));

        Bitmap img = images.getImage(usernames.get(position));

        if(img != null){
            imageView.setImageBitmap(img);
        }
        else{
            imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
        }
        return rowView;
    }

}
