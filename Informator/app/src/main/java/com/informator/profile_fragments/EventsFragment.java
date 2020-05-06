package com.informator.profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informator.R;

public class EventsFragment extends Fragment {
    TextView tvWelcome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_events, container, false);

        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_events_profile_fragment);
//        if(savedInstanceState == null){
        //TODO provera da li mu prosledjujemo evente
/*            tvWelcome.setText(R.string.no_events);*/
//        }
        return  view;
    }
}
