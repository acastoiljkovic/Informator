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

public class ListVirtualObjectsAdapter extends ArrayAdapter<String> {
    private Activity context = null;
    private ArrayList<String> titles = null;
    private ArrayList<Bitmap> images = null;
    private MapPicturesWithName pictures = null;

    public ListVirtualObjectsAdapter(Activity context,ArrayList<String> titles,ArrayList<Bitmap> images,MapPicturesWithName pictures){
        super(context, R.layout.fragment_list_virual_objects, titles);
        this.context=context;
        this.titles=titles;
        this.images=images;
        this.pictures=pictures;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_comment_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text_view_user_comment);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_user_comment);

        txtTitle.setText(titles.get(position));

        if(pictures==null){

            if(images.size()==0){
                imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }
            else
            {
                Bitmap image=images.get(position);
                imageView.setImageBitmap(image);
            }

        }
        else
        {
            Bitmap img = pictures.getImage(titles.get(position));

            if(img != null){
                imageView.setImageBitmap(img);
            }
            else{
                imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }
        }


        return rowView;
    }

}
