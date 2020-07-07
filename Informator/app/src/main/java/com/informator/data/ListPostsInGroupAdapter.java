package com.informator.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.informator.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListPostsInGroupAdapter extends ArrayAdapter<String> {

    private Activity context = null;
    private ArrayList<String> postIds=null;
    private ArrayList<String> postByUsernames=null;
    private ArrayList<String> timeOfPosts = null;
    private ArrayList<String> contentOfPosts=null;
    private ArrayList<Integer> numberOfComments=null;
    private MapPicturesWithName picturesOfPosts = null;
    private MapPicturesWithName picturesOfUser=null;
    private ArrayList<String> flags;

    public ListPostsInGroupAdapter( Activity context, ArrayList<String>postIds, ArrayList<String>postByUsernames, ArrayList<String>timeOfPosts
            , ArrayList<String>contentOfPosts, ArrayList<Integer>numberOfComments, MapPicturesWithName picturesOfPosts, MapPicturesWithName picturesOfUser
            , ArrayList<String> flags) {
        super(context, R.layout.fragment_group,postIds);
        this.context=context;
        this.postIds=postIds;
        this.postByUsernames=postByUsernames;
        this.timeOfPosts=timeOfPosts;
        this.contentOfPosts=contentOfPosts;
        this.numberOfComments=numberOfComments;
        this.picturesOfPosts=picturesOfPosts;
        this.picturesOfUser=picturesOfUser;
        this.flags=flags;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        if(flags.get(position).compareTo("true")==0){
            rowView= inflater.inflate(R.layout.list_view_post, null, true);
            CircleImageView userPostPicture=rowView.findViewById(R.id.userPostPicture);
            TextView textViewUserPost=rowView.findViewById(R.id.textViewUserPost);
            TextView textViewTimePost=rowView.findViewById(R.id.textViewTimePost);
            TextView textViewPostContent=rowView.findViewById(R.id.textViewPostContent);
            TextView textViewNumberOfComments=rowView.findViewById(R.id.textViewNumberOfComments);
            CircleImageView imageViewMyPicture=rowView.findViewById(R.id.imageViewMyPicture);
            ImageView postPicture=rowView.findViewById(R.id.postPicture);

            textViewNumberOfComments.setText(numberOfComments.get(position)+" comments");
            textViewPostContent.setText(contentOfPosts.get(position));
            textViewTimePost.setText(timeOfPosts.get(position));
            textViewUserPost.setText(postByUsernames.get(position));


            imageViewMyPicture.setImageBitmap(StoredData.getInstance().getUser().getProfilePhoto());

            Bitmap postPic=picturesOfPosts.getImage(postIds.get(position));
            if(postPic!=null){
                postPicture.setImageBitmap(postPic);
            }
            else
            {
                postPicture.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }

            Bitmap userPostPic=picturesOfUser.getImage(postIds.get(position));
            if(userPostPic!=null){
                userPostPicture.setImageBitmap(userPostPic);
            }
            else
            {
                userPostPicture.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }

        }
        else
        {
            rowView= inflater.inflate(R.layout.list_view_post_noimage, null, true);
            CircleImageView userPostPicture=rowView.findViewById(R.id.userPostPicture);
            TextView textViewUserPost=rowView.findViewById(R.id.textViewUserPost);
            TextView textViewTimePost=rowView.findViewById(R.id.textViewTimePost);
            TextView textViewPostContent=rowView.findViewById(R.id.textViewPostContent);
            TextView textViewNumberOfComments=rowView.findViewById(R.id.textViewNumberOfComments);
            CircleImageView imageViewMyPicture=rowView.findViewById(R.id.imageViewMyPicture);

            textViewNumberOfComments.setText(numberOfComments.get(position)+" comments");
            textViewPostContent.setText(contentOfPosts.get(position));
            textViewTimePost.setText(timeOfPosts.get(position));
            textViewUserPost.setText(postByUsernames.get(position));


            imageViewMyPicture.setImageBitmap(StoredData.getInstance().getUser().getProfilePhoto());
            Bitmap userPostPic=picturesOfUser.getImage(postIds.get(position));
            if(userPostPic!=null){
                userPostPicture.setImageBitmap(userPostPic);
            }
            else
            {
                userPostPicture.setImageResource(R.drawable.ic_person_outline_black_24dp);
            }
        }





        return rowView;
    }
}
