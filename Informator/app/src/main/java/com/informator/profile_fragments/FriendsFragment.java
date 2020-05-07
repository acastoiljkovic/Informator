package com.informator.profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informator.R;
import com.informator.StartActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    TextView tvWelcome;
    ImageButton btnAddFriend;
    Boolean profile = false;
    ListView listFriends;
    ArrayList<String> friendsUsernames;
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        Bundle bundle = getArguments();

        listFriends = view.findViewById(R.id.list_friends);
        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_friends_fragment);
        btnAddFriend = view.findViewById(R.id.fragment_friends_add_friend_button);

        if(bundle != null) {
            profile = bundle.getBoolean("profile",false);
            friendsUsernames = bundle.getStringArrayList("friends");
            if(friendsUsernames.size() <= 0) {
                tvWelcome.setText(R.string.no_friends);
            }
            else{
                tvWelcome.setVisibility(View.GONE);
            }
            adapter = new ArrayAdapter<String>(getContext(),R.layout.list_view_friends,R.id.list_friends_fullname,friendsUsernames);
            listFriends.setAdapter(adapter);
        }

        if(!profile) {
            btnAddFriend.setVisibility(View.VISIBLE);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StartActivity) getActivity()).setFragment(R.id.search_friends, null);
                }
            });
        }
        else {
            btnAddFriend.setVisibility(View.GONE);
        }
        return  view;
    }


}
