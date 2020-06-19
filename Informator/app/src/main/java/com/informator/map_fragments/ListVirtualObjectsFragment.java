package com.informator.map_fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.informator.R;
import com.informator.data.StoredData;
import com.informator.data.VirtualObject;

import java.util.ArrayList;

public class ListVirtualObjectsFragment extends Fragment {

    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_virual_objects,container,false);

        listView=view.findViewById(R.id.list_virtual_objects);

        ArrayList<String> titles=new ArrayList<>();
        ArrayList<Bitmap> images=new ArrayList<>();

        Bundle bundle=this.getArguments();
        float radius=bundle.getFloat("radius");
        Toast.makeText(getActivity(),String.valueOf(radius),Toast.LENGTH_LONG).show();

        for(VirtualObject virtualObject:StoredData.getInstance().getUser().getListVO()){

        }


//        for(int i=0;i<StoredData.getInstance().getUser().getListVO().size();i++){
//            titles.add(StoredData.getInstance().getUser().getListVO().get(i).getTitle());
//            images.add(StoredData.getInstance().getUser().getListVO().get(i).getVirtual_object_image());
//        }

//        ListVirtualObjectsAdapter listVirtualObjectsAdapter=new ListVirtualObjectsAdapter(getActivity(),titles,images);
//        listView.setAdapter(listVirtualObjectsAdapter);



        return view;
    }

//    private boolean checkIfInRadius(LatLng position,float radius){
//        float distanceFromCurrentLocation[]=new float[1];
//
//        Location.distanceBetween(
//                current_user_location.getLatitude(),
//                current_user_location.getLongitude(),
//                position.latitude,
//                position.longitude,
//                distanceFromCurrentLocation
//        );
//
//        if(distanceFromCurrentLocation[0] > radius)
//            return false;
//        else
//            return true;
//    }

    public class ListVirtualObjectsAdapter extends BaseAdapter{

        private Activity context = null;
        private ArrayList<String> titles = null;
        private ArrayList<Bitmap> images = null;

        public ListVirtualObjectsAdapter(Activity context,ArrayList<String> titles,ArrayList<Bitmap> images){
            this.context=context;
            this.titles=titles;
            this.images=images;

        }


        @Override
        public int getCount() {
            return this.titles.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.list_view_comment_item, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.text_view_user_comment);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image_user_comment);

            txtTitle.setText(titles.get(position));
            if(position >= images.size()){
                imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }
            else {
                imageView.setImageBitmap(images.get(position));
            }
            return rowView;
        }
    }
}
