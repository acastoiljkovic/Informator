package com.informator;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.informator.data.Event;
import com.informator.data.StoredData;

import java.util.ArrayList;
import java.util.Date;

public class EventsFragment extends Fragment {

    CalendarView calendarView;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> events;
    ArrayList<String> eventsIds;
    Toolbar toolbar;
    TextView textViewCurrentEvents;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events,container,false);

        Initialize(view);


        return view;
    }


    private void Initialize(View view){
        view.setBackgroundColor(Color.WHITE);
        ((StartActivity)getActivity()).setBottomNavItemSelected(R.id.events);
        calendarView = view.findViewById(R.id.fragment_event_calendar);
        listView = view.findViewById(R.id.fragment_events_list_view);
        toolbar = (Toolbar) view.findViewById(R.id.fragment_event_toolbar);

        events = new ArrayList<>();
        eventsIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),R.layout.list_view_single_textview,R.id.list_item_single_view,events);
        listView.setAdapter(adapter);

        textViewCurrentEvents = view.findViewById(R.id.textView_current_events);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarItemClick(item);
                return false;
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                onCalendarClick(year,month,dayOfMonth);
            }
        });

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

    private void onCalendarClick(int year, int month, int dayOfMonth){
        changedDate(year,month,dayOfMonth);

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
        Toast.makeText(getContext(), (month+1) + "/" + dayOfMonth + "/" + year, Toast.LENGTH_LONG).show();
    }

    private void onToolbarItemClick(MenuItem item){
        if(item.getItemId() == R.id.add_event){
            ((StartActivity)getActivity()).setFragment(R.id.add_event,null);

        }
        else if(item.getItemId() == R.id.find_event){
            //TODO add code for search events
        }
    }


}
