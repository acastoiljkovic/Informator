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

import org.w3c.dom.Text;

public class RankingFragment extends Fragment {
    TextView tvWelcome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_ranking_fragment);
//        if(savedInstanceState == null){
        //TODO provera da li mu prosledjujemo rankove prijatelja
            tvWelcome.setText(R.string.no_ranking);
//        }
        return  view;
    }
}
