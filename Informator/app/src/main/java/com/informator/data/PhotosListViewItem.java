package com.informator.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.informator.R;

import java.util.ArrayList;

public class PhotosListViewItem extends ArrayAdapter<Bitmap> {

    private Activity context = null;
    private ArrayList<Bitmap> images = null;

    public PhotosListViewItem(Activity context, ArrayList<Bitmap> images) {
        super(context, R.layout.list_view_photos);
        this.images = images;
        this.context = context;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_search_friends_item, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_view_photo);

        imageView.setImageBitmap(images.get(position));

        return rowView;
    }
}
