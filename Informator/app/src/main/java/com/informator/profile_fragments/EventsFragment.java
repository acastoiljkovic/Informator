package com.informator.profile_fragments;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Event;
import com.informator.data.StoredData;

import java.util.ArrayList;
import java.util.Date;

public class EventsFragment extends Fragment {
    TextView tvWelcome;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> events;
    ArrayList<String> eventsIds;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_events, container, false);

        Initialize(view);

        return  view;
    }

    public void Initialize(View view){

        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_events_profile_fragment);
        listView = view.findViewById(R.id.list_events);
        events = new ArrayList<>();
        eventsIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),R.layout.list_view_single_textview,R.id.list_item_single_view,events);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onListViewItemClick(i);
            }
        });

        Date d = new Date();
        int dayDate = Integer.parseInt((String) DateFormat.format("dd",d));
        int yearDate = Integer.parseInt((String) DateFormat.format("yyyy",d));
        // kada pribavljamo datum sa kalendara krati ga za 1, a ovde date tacan pa mora rucno da se smanji
        int monthDate = Integer.parseInt((String) DateFormat.format("MM",d)) - 1;

        changedDate(yearDate,monthDate,dayDate);
    }

    private void onListViewItemClick(int index){
        Bundle bundle = new Bundle();
        bundle.putString("eventId",eventsIds.get(index));
        ((StartActivity)getActivity()).setFragment(R.string.single_event_id,bundle);
    }

    private void changedDate(int year,int month, int dayOfMonth){
        events.clear();
        eventsIds.clear();
        for(Event e : StoredData.getInstance().user.getEvents()){
            Date d = new Date(e.getDatetime());
            int dayDate = Integer.parseInt((String) DateFormat.format("dd",d));
            int yearDate = Integer.parseInt((String) DateFormat.format("yyyy",d));
            int monthDate = Integer.parseInt((String) DateFormat.format("MM",d));
            int min = Integer.parseInt((String) DateFormat.format("mm",d));
            int hour = Integer.parseInt((String) DateFormat.format("HH",d));
            if(yearDate == year && monthDate == (month+1) && dayDate == dayOfMonth){
                events.add("Title  "+e.getTitle() + " - Time  " + hour + " : " + min );
                eventsIds.add(e.getId());
                adapter.notifyDataSetChanged();
            }
        }
        adapter.notifyDataSetChanged();
    }
}
