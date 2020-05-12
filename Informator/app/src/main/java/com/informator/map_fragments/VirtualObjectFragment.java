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
import com.informator.data.StoredData;

import java.io.ByteArrayOutputStream;

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

        textViewWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("index_of_virtual_object",indexOfVirtualObject);

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


        Bundle bundle=this.getArguments();
        idVirtualObject=bundle.getString("idVirtualObject");

        for(int i=0;i< StoredData.getInstance().user.getListVO().size();i++){
            if(StoredData.getInstance().user.getListVO().get(i).getId().compareTo(idVirtualObject)==0){
                indexOfVirtualObject=i;
                imageView.setImageBitmap(StoredData.getInstance().user.getListVO().get(i).getVirtual_object_image());
                textViewTitle.setText(StoredData.getInstance().user.getListVO().get(i).getTitle());
                textViewRecommendedBy.setText("Recommended by "+StoredData.getInstance().user.getListVO().get(i).getUserRecommended());
                textViewDescription.setText(StoredData.getInstance().user.getListVO().get(i).getDescription());
                textViewNumberOfPosts.setText(StoredData.getInstance().user.getListVO().get(i).getPosts().size()+" comments");
                imageViewUserImage.setImageBitmap(StoredData.getInstance().user.getProfilePhoto());
                textViewRating.setText("Rate: "+StoredData.getInstance().user.getListVO().get(i).getRating());

                if(StoredData.getInstance().user.getListVO().get(i).getPosts().size()>0)//postoje komentari treba ih dodati
                {
                    final FrameLayout frameLayout=new FrameLayout(getContext());
                    FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(170,170);
                    lp.gravity= Gravity.CENTER;
                    frameLayout.setLayoutParams(lp);

                    final ImageView img=new ImageView(getContext());
                    LinearLayout.LayoutParams lpi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    lpi.bottomMargin=6;
                    lpi.topMargin=6;
                    lpi.leftMargin=6;
                    lpi.rightMargin=6;
                    img.setLayoutParams(lpi);

                    StorageReference user_image_reference=storageReference.child(StoredData.getInstance().getUser().getListVO().get(i)
                            .getPosts().get(StoredData.getInstance().getUser().getListVO().get(i)
                                    .getPosts().size()-1).getUsername()+".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    if(user_image_reference!=null){
                        user_image_reference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                                img.setImageBitmap(bitmap);
                                frameLayout.addView(img);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }


                    ImageView img1=new ImageView(getContext());
                    LinearLayout.LayoutParams lpi1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    img1.setLayoutParams(lpi1);
                    img1.setImageResource(R.drawable.profile_picture);

                    frameLayout.addView(img1);

                    linearLayoutImage.addView(frameLayout);

                    TextView textView=new TextView(getContext());
                    LinearLayout.LayoutParams lpTextView=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    lpTextView.topMargin=7;
                    lpTextView.gravity=Gravity.CENTER;

                    textView.setText(StoredData.getInstance().user.getListVO().get(i).getPosts()
                            .get(StoredData.getInstance().user.getListVO().get(i).getPosts().size()-1).getPost());
                    textView.setTextSize(14);
                    textView.setTextColor(getResources().getColor(R.color.color_black));
                    textView.setBackground(getResources().getDrawable(R.drawable.backround_comments));

                    linearLayoutTextView.addView(textView);


                }

                //ako je prijateljev objekat moguce ga je oceniti
                if(StoredData.getInstance().user.getListVO().get(i).getUserRecommended().compareTo(StoredData.getInstance().user.getUsername())!=0){
                    RatingBar ratingBar=new RatingBar(getContext());
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            currentRating=rating;
                            Toast.makeText(getActivity(),String.valueOf(currentRating),Toast.LENGTH_LONG).show();
                        }
                    });

                    Button btnRate=new Button(getContext());
                    btnRate.setText("Rate");
                    btnRate.setTextColor(getResources().getColor(R.color.color_black));
                    btnRate.setBackground(getResources().getDrawable(R.drawable.button_white_border));
                    btnRate.setTextSize(20);

                    final int finalI = i;
                    btnRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentRating==0){
                                Toast.makeText(getActivity(),"Rate first!",Toast.LENGTH_LONG).show();
                            }
                            else {
                                int num=StoredData.getInstance().user.getListVO().get(finalI).getNumberOfRates()+1;
                                float rating=(StoredData.getInstance().user.getListVO().get(finalI).getRating()
                                        *StoredData.getInstance().user.getListVO().get(finalI).getNumberOfRates()+currentRating)/num;
                                databaseReference.child("users").child(StoredData.getInstance().user.getListVO().get(finalI).getUserRecommended())
                                        .child("virtual_objects").child(StoredData.getInstance().user.getListVO().get(finalI).getTitle())
                                        .child("numberOfRates").setValue(num);
                                databaseReference.child("users").child(StoredData.getInstance().user.getListVO().get(finalI).getUserRecommended())
                                        .child("virtual_objects").child(StoredData.getInstance().user.getListVO().get(finalI).getTitle())
                                        .child("rating").setValue(rating);

                            }
                        }
                    });

                    ratingBarLayout.addView(ratingBar);
                    ratingBarLayout.addView(btnRate);
                }

                break;

            }
        }
        return view;
    }


}
