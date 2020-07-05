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

public class ListGroupsAdapter extends ArrayAdapter<String> {
    private Activity context = null;
    private ArrayList<String> groupIds=null;
    private ArrayList<String> groupNames = null;
    private ArrayList<Integer> groupNumberOfMembers=null;
    private MapPicturesWithName groupPictures = null;

    public ListGroupsAdapter(Activity context,ArrayList<String> groupIds,ArrayList<String> groupNames,ArrayList<Integer> groupNumberOfMembers,MapPicturesWithName pictures){
        super(context, R.layout.fragment_groups,groupIds);
        this.context=context;
        this.groupIds=groupIds;
        this.groupNames=groupNames;
        this.groupPictures=pictures;
        this.groupNumberOfMembers=groupNumberOfMembers;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_view_group, null, true);

        ImageView imageViewGroupPicture=rowView.findViewById(R.id.group_picture);
        TextView textViewGroupName=rowView.findViewById(R.id.group_name);
        TextView textViewGroupNumberOfMembers=rowView.findViewById(R.id.group_number_of_members);

        textViewGroupName.setText(groupNames.get(position));
        textViewGroupNumberOfMembers.setText(groupNumberOfMembers.get(position).toString()+" members");



        Bitmap img = groupPictures.getImage(groupIds.get(position));

        if(img != null){
            imageViewGroupPicture.setImageBitmap(img);
        }
        else{
            imageViewGroupPicture.setImageResource(R.drawable.ic_person_outline_black_24dp);
        }


        return rowView;
    }
}
