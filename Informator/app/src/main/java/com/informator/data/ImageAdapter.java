package com.informator.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.informator.profile_fragments.PhotosFragment;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bitmap> pictures;

    public ImageAdapter(Context context,ArrayList<Bitmap> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            imageView.setLayoutParams(new
                    GridView.LayoutParams(4*metrics.widthPixels/10, 4*metrics.widthPixels/10));
            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(4*metrics.widthPixels/10, 4*metrics.widthPixels/10);
            params.setMargins(5,5,5,5);
            imageView.setLayoutParams(params);



        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(pictures.get(position));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open dialog with bigger picture
                PhotosFragment.dispatchPicture(pictures.get(position));
            }
        });
        return imageView;
    }
}
