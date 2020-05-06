package com.informator.profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informator.R;
import com.informator.StartActivity;

public class PhotosFragment extends Fragment {
    TextView tvWelcome;
    Boolean profile;
    ImageButton addPhotoCamera;
    ImageButton addPhotoGallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profile = false;
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        Bundle bundle = getArguments();

        if(bundle != null) {
            profile = bundle.getBoolean("profile");
        }

        addPhotoCamera = view.findViewById(R.id.fragment_photos_add_photo_camera_button);
        addPhotoGallery = view.findViewById(R.id.fragment_photos_add_photo_gallery_button);
        if(!profile) {
            addPhotoCamera.setVisibility(View.VISIBLE);
            addPhotoGallery.setVisibility(View.VISIBLE);
            addPhotoCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            addPhotoGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        else {
            addPhotoCamera.setVisibility(View.GONE);
            addPhotoGallery.setVisibility(View.GONE);
        }
        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_photos_fragment);
//        if(savedInstanceState == null){
        //TODO provera da li mu prosledjujeo slike
/*            tvWelcome.setText(R.string.no_photos);*/
//        }
        return  view;
    }
}
