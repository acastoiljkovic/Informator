package com.informator.map_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.MapFragment;
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.ListVirtualObjectsAdapter;
import com.informator.data.StoredData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class VirtualObjectFragment extends Fragment {
    String idVirtualObject;
    float currentRating=0;

    ImageView imageView;
    TextView textViewTitle;
    TextView textViewRecommendedBy;
    ImageButton back;
    TextView textViewDescription;
    TextView textViewNumberOfPosts;
    ImageView imageViewUserImage;
    TextView textViewRating;
    LinearLayout linearLayoutComment; //u ovaj layout treba dodati poslednji post odnosno poslednji komentar i sliku korisnika koji je postavio taj komentar
    LinearLayout ratingBarLayout;
    LinearLayout linearLayoutImage;
    LinearLayout linearLayoutTextView;
    TextView textViewWriteComment;
    ListView listViewComments;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    int indexOfVirtualObject;


    public VirtualObjectFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseStorage=FirebaseStorage.getInstance(Constants.URL_STORAGE);
        storageReference=firebaseStorage.getReference();


        View view=inflater.inflate(R.layout.fragment_virual_object,container,false);
        imageView=view.findViewById(R.id.imageViewVirtualObject);
        textViewTitle=view.findViewById(R.id.textViewTitle);
        textViewRecommendedBy=view.findViewById(R.id.textViewRecomendedBy);
        back=view.findViewById(R.id.back_to_map_from_vo);
        textViewDescription=view.findViewById(R.id.textViewDescription);
        textViewNumberOfPosts=view.findViewById(R.id.number_of_posts);
        imageViewUserImage=view.findViewById(R.id.imageView_user_image);
        textViewRating=view.findViewById(R.id.id_rating);
        linearLayoutComment=view.findViewById(R.id.layout_comment);
        ratingBarLayout=view.findViewById(R.id.rating_bar);
        linearLayoutImage=view.findViewById(R.id.linear_layout_image);
        linearLayoutTextView=view.findViewById(R.id.linear_layout_textView);
        textViewWriteComment=view.findViewById(R.id.text_view_write_comment);
        listViewComments=view.findViewById(R.id.listComments);

        textViewWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("virtualObjectName",StoredData.getInstance().getVirtualObject().getId());
                bundle.putString("userRecommendedName",StoredData.getInstance().getVirtualObject().getUserRecommended());

                ((StartActivity)getActivity()).setFragment(R.string.open_comments,bundle);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment=new MapFragment();
                ((StartActivity) getActivity()).setFragment(R.id.map,null);
            }
        });


//        Bundle bundle=this.getArguments();
//        idVirtualObject=bundle.getString("idVirtualObject");

        imageView.setImageBitmap(StoredData.getInstance().getVirtualObject().getVirtual_object_image());
        textViewTitle.setText(StoredData.getInstance().getVirtualObject().getTitle());
        textViewRecommendedBy.setText("Recommended by "+StoredData.getInstance().getVirtualObject().getUserRecommended());
        textViewDescription.setText(StoredData.getInstance().getVirtualObject().getDescription());
        textViewNumberOfPosts.setText(StoredData.getInstance().getVirtualObject().getPosts().size() +" comments");
        imageViewUserImage.setImageBitmap(StoredData.getInstance().user.getProfilePhoto());
        textViewRating.setText("Rate: "+StoredData.getInstance().getVirtualObject().getRating());

        String userRecommended=StoredData.getInstance().getVirtualObject().getUserRecommended();

        if(StoredData.getInstance().getVirtualObject().getPosts().size()>0)//postoje komentari treba ih dodati
        {
            ArrayList<String> comments=new ArrayList<>();
            final ArrayList<Bitmap> pictures=new ArrayList<>();


            final ListVirtualObjectsAdapter listVirtualObjectsAdapter = new ListVirtualObjectsAdapter(getActivity(),comments,pictures,null);

            comments.add(StoredData.getInstance().getVirtualObject().getPosts()
                    .get(StoredData.getInstance().getVirtualObject().getPosts().size()-1).getPost());

            //uzima poslednji komentar i samo njega prikazuje
            StorageReference user_image_reference=storageReference.child(StoredData.getInstance().getVirtualObject()
                    .getPosts().get(StoredData.getInstance().getVirtualObject()
                            .getPosts().size()-1).getUsername()+".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(user_image_reference!=null){
                user_image_reference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        pictures.add(bitmap);
                        listVirtualObjectsAdapter.notifyDataSetChanged();

                        //img.setImageBitmap(bitmap);
                        //frameLayout.addView(img);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            listViewComments.setAdapter(listVirtualObjectsAdapter);



        }

        //ako je prijateljev objekat moguce ga je oceniti
        if(StoredData.getInstance().getVirtualObject().getUserRecommended().compareTo(StoredData.getInstance().getUser().getUsername())!=0){

            if(!checkIfInListRated(StoredData.getInstance().getVirtualObject().getId())){
                final RatingBar ratingBar=new RatingBar(getContext());
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        currentRating=rating;
                        Toast.makeText(getActivity(),String.valueOf(currentRating),Toast.LENGTH_LONG).show();
                    }
                });

                final Button btnRate=new Button(getContext());
                btnRate.setText("Rate");
                btnRate.setTextColor(getResources().getColor(R.color.color_black));
                btnRate.setBackground(getResources().getDrawable(R.drawable.button_white_border));
                btnRate.setTextSize(20);


                btnRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentRating==0){
                            Toast.makeText(getActivity(),"Rate first!",Toast.LENGTH_LONG).show();
                        }
                        else {
                            int num=StoredData.getInstance().getVirtualObject().getNumberOfRates()+1;
                            float rating=(StoredData.getInstance().getVirtualObject().getRating()
                                    *StoredData.getInstance().getVirtualObject().getNumberOfRates()+currentRating)/num;
                            textViewRating.setText("Rate: "+rating);
                            databaseReference.child("users").child(StoredData.getInstance().getVirtualObject().getUserRecommended())
                                    .child("virtual_objects").child(StoredData.getInstance().getVirtualObject().getId())
                                    .child("numberOfRates").setValue(num);
                            databaseReference.child("users").child(StoredData.getInstance().getVirtualObject().getUserRecommended())
                                    .child("virtual_objects").child(StoredData.getInstance().getVirtualObject().getId())
                                    .child("rating").setValue(rating);

                            databaseReference.child("users").child(StoredData.getInstance().getUser().getUsername()).child("list_rated_virtual_objects")
                                    .child(StoredData.getInstance().getVirtualObject().getId()).setValue(StoredData.getInstance().getVirtualObject().getId());

                        }
                        btnRate.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.GONE);
                    }
                });

                ratingBarLayout.addView(ratingBar);
                ratingBarLayout.addView(btnRate);
            }

        }

        return view;
    }

    private boolean checkIfInListRated(String virtualObjectId){
        for(String voId:StoredData.getInstance().getUser().getListRatedVirtualObjects()){
            if(voId.compareTo(virtualObjectId)==0)
            {
                return true;
            }
        }

        return false;
    }


}
