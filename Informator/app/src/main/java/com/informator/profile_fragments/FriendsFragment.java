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

public class FriendsFragment extends Fragment {
    TextView tvWelcome;
    ImageButton btnAddFriend;
    Boolean profile = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        Bundle bundle = getArguments();

        if(bundle != null) {
            profile = bundle.getBoolean("profile");
        }
        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_friends_fragment);
        btnAddFriend = view.findViewById(R.id.fragment_friends_add_friend_button);

        if(!profile) {
            btnAddFriend.setVisibility(View.VISIBLE);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StartActivity) getActivity()).setFragment(R.id.search_friends, null);
                }
            });
        }
        else
            btnAddFriend.setVisibility(View.GONE);
//        if(savedInstanceState == null){
        //TODO provera da li mu prosledjujemo prijatelje
/*            tvWelcome.setText(R.string.no_friends);*/
//        }
        return  view;
    }


}
