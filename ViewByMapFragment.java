package com.example.shwetashahane.assignment4;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class ViewByMapFragment extends Fragment {

    JSONArray jsonarray;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    static String stateUrl, stateSelected;
    private GoogleMap gMap;
    private com.google.android.gms.maps.MapView mapView;
    ArrayList<Person> userList, personList;
    Spinner stateSpinner, countrySpinner;
    double lat, lng;
    EditText yearMapText;
    int yearEntered;

    public ViewByMapFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_view_user_map, container, false);
        System.out.println("inside map fragment");
        countryList.add("Select Country");
        System.out.println("Select country" + countryList);
        yearMapText = (EditText) rootView.findViewById(R.id.yearMapText);
        yearMapText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        yearEntered = Integer.parseInt(yearMapText.getText().toString());
                        if (yearEntered <= 1970 || yearEntered >= 2017)
                            yearMapText.setError("Year must be in between 1970 and 2017");
                    } catch (Exception e) {
                    }
                }
            }
        });
        mapView = (com.google.android.gms.maps.MapView) rootView.findViewById(R.id.mapViewUser);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        Button filterMapButton = (Button) rootView.findViewById(R.id.filterMapButton);
        filterMapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                yearMapText.clearFocus();
                validation();
            }
        });
        return rootView;
    }

    public void validation() {
        if (yearMapText.getText().toString().length() != 0)
            new DownloadMarker().execute("http://bismarck.sdsu.edu/hometown/users?year=" + yearMapText.getText());
        if (countrySpinner.getSelectedItemPosition() != 0)
            new DownloadMarker().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl);
        if (stateSpinner.getSelectedItemPosition() != 0)
            new DownloadMarker().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected));
        if (yearMapText.getText().toString().length() != 0 && countrySpinner.getSelectedItemPosition() != 0 && stateSpinner.getSelectedItemPosition() != 0)
            new DownloadMarker().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected) + "&year=" + yearMapText.getText());
        else if (yearMapText.getText().toString().length() == 0 && stateSpinner.getSelectedItemPosition() == 0 && countrySpinner.getSelectedItemPosition() == 0)
            new DownloadMarker().execute("http://bismarck.sdsu.edu/hometown/users");

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new DownloadJSON().execute();

    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                jsonarray = JSONfunctions.getJSONfromURL("http://bismarck.sdsu.edu/hometown/countries");
                System.out.println("Json array :" + jsonarray.length());
                for (int i = 0; i < jsonarray.length(); i++) {
                    countryList.add(jsonarray.getString(i));
                    System.out.println(countryList.get(i));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {
            countrySpinner = (Spinner) getActivity().findViewById(R.id.countryMapSpinner);

            countrySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    countryList));
            countrySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try {
                                stateUrl = countryList.get(position);
                                if (stateUrl == "Select Country") {
                                    stateList.add("Select State");
                                    stateSpinner = (Spinner) getActivity().findViewById(R.id.stateMapSpinner);
                                    stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            stateList));
                                } else {
                                    stateList.clear();
                                    stateList.add("Select State");
                                    displayCountry(countryList.get(position));
                                    new DownloadState().execute();
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        }
    }

    private class DownloadState extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("state");
            try {

                jsonarray = JSONfunctions.getJSONfromURL("http://bismarck.sdsu.edu/hometown/states?country=" + stateUrl);
                System.out.println("Json state array :" + jsonarray.length());
                for (int i = 0; i < jsonarray.length(); i++) {
                    stateList.add(jsonarray.getString(i));
                    System.out.println(stateList.get(i));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {
            Spinner stateSpinner = (Spinner) getActivity().findViewById(R.id.stateMapSpinner);
            stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    stateList));
            stateSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try {
                                stateSelected = stateList.get(position);
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class DownloadMarker extends AsyncTask<String, Void, ArrayList<Person>> {

        String result;

        @Override
        protected ArrayList<Person> doInBackground(String... params) {
            userList = new ArrayList<Person>();
            JSONObject jsonobject;
            System.out.println("person list");
            try {
                URL url = new URL(params[0]);
                System.out.println("URRRRL:" + url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = bf.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);

                    Person worldpop = new Person();

                    worldpop.setNickname(jsonobject.optString("nickname"));
                    worldpop.setCountry(jsonobject.optString("country"));
                    worldpop.setState(jsonobject.optString("state"));
                    worldpop.setCity(jsonobject.optString("city"));
                    worldpop.setLat(jsonobject.optDouble("latitude"));
                    worldpop.setLongitude(jsonobject.optDouble("longitude"));
                    userList.add(worldpop);

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {
            gMap.clear();
            if (result.size() != 0) {
                personList = result;
                for (int i = 0; i < personList.size(); i++) {
                    if (personList.get(i).getLat() == 0.0 || personList.get(i).getLongitude() == 0.0) {
                        Geocoder locator = new Geocoder(getActivity());
                        String country_state = personList.get(i).getCity() + "," + personList.get(i).getState() + ", " + personList.get(i).getCountry();
                        System.out.println("CS with no lat long :" + country_state + "nick name " + personList.get(i).getNickname());
                        try {
                            List<Address> state = locator.getFromLocationName(country_state, 1);
                            for (Address stateLocation : state) {
                                if (stateLocation.hasLatitude())
                                    lat = stateLocation.getLatitude();
                                if (stateLocation.hasLongitude())
                                    lng = stateLocation.getLongitude();
                            }
                        } catch (Exception error) {
                            Log.e("rew", "Address lookup Error", error);
                        }
                        gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                .title(personList.get(i).getNickname()));
                    } else {
                        System.out.println("CS with lat long :" + "nick name " + personList.get(i).getNickname());
                        gMap.addMarker(new MarkerOptions().position(new LatLng(personList.get(i).getLat(), personList.get(i).getLongitude()))
                                .title(personList.get(i).getNickname()));
                    }

                }
            } else if (result.size() == 0) {
                gMap.clear();
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void displayCountry(String country) {
        Geocoder locator = new Geocoder(getContext());

        try {
            List<Address> address =
                    locator.getFromLocationName(country, 1);
            for (Address addressLocation : address) {
                if (addressLocation.hasLatitude())
                    lat = addressLocation.getLatitude();
                if (addressLocation.hasLongitude())
                    lng = addressLocation.getLongitude();
            }
        } catch (Exception error) {
        }
        LatLng display = new LatLng(lat, lng);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(display));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(4));
    }
}