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

public class ListUserAdapter extends ArrayAdapter<String> {
    private Activity context = null;
    private ArrayList<String> usernames = null;
    private MapPicturesWithName pictures = null;

    public ListUserAdapter(Activity context,ArrayList<String> titles,MapPicturesWithName pictures){
        super(context, R.layout.fragment_list_virual_objects, titles);
        this.context=context;
        this.usernames=titles;
        this.pictures=pictures;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_user, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.friend_username);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.friend_picture);

        txtTitle.setText(usernames.get(position));



        Bitmap img = pictures.getImage(usernames.get(position));

        if(img != null){
            imageView.setImageBitmap(img);
        }
        else{
            imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
        }


        return rowView;
    }
}
