package com.informator.event_fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Event;
import com.informator.data.StoredData;

import java.util.Date;

public class SingleEventFragment extends Fragment {

    TextView twTime;
    TextView twRemainingTime;
    TextView twEstimatedTime;
    ImageView iwEstimatedTime;
    GoogleMap mMap;
    Event event;
    MapView mapView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_event,container,false);
        final Bundle bundle = getArguments();

        Initialize(view,bundle);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Initialize(View view, Bundle bundle){

        view.setBackgroundColor(Color.WHITE);

        String id = bundle.getString("eventId","none");
        if(id.isEmpty() || id.equals("none")){
            getActivity().onBackPressed();
        }
        else{
            event = StoredData.getInstance().user.getEventsWithId().get(id);
            twTime = view.findViewById(R.id.textViewTime);
            twRemainingTime = view.findViewById(R.id.textViewRemainingTime);
            twEstimatedTime = view.findViewById(R.id.textViewEstimatedTime);
            iwEstimatedTime = view.findViewById(R.id.imageViewEstimatedTime);

            Date eventDate = new Date(event.getDatetime());

            int dayDate = Integer.parseInt((String) DateFormat.format("dd",eventDate));
            int yearDate = Integer.parseInt((String) DateFormat.format("yyyy",eventDate));
            int monthDate = Integer.parseInt((String) DateFormat.format("MM",eventDate));
            int min = Integer.parseInt((String) DateFormat.format("mm",eventDate));
            int hour = Integer.parseInt((String) DateFormat.format("HH",eventDate));

            twTime.setText("Time : "+ hour + " : "+min  );

            Date now = new Date();

            int dayDateNow = Integer.parseInt((String) DateFormat.format("dd",now));
            int yearDateNow = Integer.parseInt((String) DateFormat.format("yyyy",now));
            int monthDateNow = Integer.parseInt((String) DateFormat.format("MM",now));
            int minNow = Integer.parseInt((String) DateFormat.format("mm",now));
            int hourNow = Integer.parseInt((String) DateFormat.format("HH",now));

            int minRem = min - minNow ;
            int hourRem = hour - hourNow;

            if(dayDate > dayDateNow && monthDate >= monthDateNow && yearDate >= yearDateNow) {
                twRemainingTime.setText("Remaining time : 23 : 59");
            }
            else if(yearDateNow > yearDate || ( yearDate == yearDateNow && monthDateNow > monthDate ) ||
                    ( yearDate == yearDateNow && monthDate == monthDateNow && dayDateNow > dayDate ) ||
                    ( yearDate == yearDateNow && monthDate == monthDateNow && dayDate == dayDateNow && hourNow > hour) ||
                    ( yearDate == yearDateNow && monthDate == monthDateNow && dayDate == dayDateNow && hourNow == hour && minNow > min)){
                twRemainingTime.setText("Remaining time : event has ended");
                hourRem = -1998;
            }
            else {
                if(minRem < 0) {
                    hourRem -= 1;
                    minRem = 60 + minRem;
                }
                twRemainingTime.setText("Remaining time : " + hourRem + " : " + minRem);
            }



            mapView = view.findViewById(R.id.mapSingleEvent);

            if(StoredData.getInstance().user.getLatitude() == 0 || StoredData.getInstance().user.getLongitude() == 0) {
                Toast.makeText(getContext(),"Unable to get location. Go to profile and enable !", Toast.LENGTH_SHORT).show();
            }
            else {
                if (event.getLatitude() != 0 && event.getLongitude() != 0) {
                    if (hourRem != -1998) {

                        Location loc1 = new Location(Location.convert(0, 0));
                        loc1.setLongitude(event.getLongitude());
                        loc1.setLatitude(event.getLatitude());
                        Location loc2 = new Location(Location.convert(0, 0));
                        loc2.setLongitude(StoredData.getInstance().user.getLongitude());
                        loc2.setLatitude(StoredData.getInstance().user.getLatitude());


                        float distance = loc1.distanceTo(loc2);

                        float estimatedTime = distance / 6000; // racunamo da se krece brzinom od 6km/h

                        int estimatedHour = (int) estimatedTime;
                        int estimatedMinute = (int) ((estimatedTime - estimatedHour) * 60);


                        twEstimatedTime.setText("Estimated time : " + estimatedHour + " : " + estimatedMinute);

                        int remTime = hourRem * 60 + minRem;
                        if (remTime < (estimatedHour * 60 + estimatedMinute)) {
                            iwEstimatedTime.setForeground(getResources().getDrawable(R.drawable.ic_baseline_timer_24_red));
                        }

                    } else {
                        iwEstimatedTime.setForeground(getResources().getDrawable(R.drawable.ic_baseline_timer_24_red));
                        twEstimatedTime.setText("Estimated time : event has ended");
                    }

                    mapView.onCreate(null);
                    mapView.onResume();
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude())).title("Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 10));
                        }
                    });
                } else {
                    iwEstimatedTime.setForeground(getResources().getDrawable(R.drawable.ic_baseline_timer_24_red));
                    twEstimatedTime.setText("Estimated time : event does not have location");
                    mapView.setVisibility(View.INVISIBLE);
                }

            }

        }

    }
}
