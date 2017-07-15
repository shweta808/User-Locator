package com.example.shwetashahane.assignment4;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class ViewByListFragment extends Fragment {

    JSONArray jsonarray;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    ArrayList<Person> userList;
    ArrayList<String> users;
    static String stateUrl, stateSelected;
    private ListView list;
    Spinner stateSpinner, countrySpinner;
    int yearEntered;
    EditText yearText;

    public ViewByListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view_user_list, container, false);
        countryList.add("Select Country");
        yearText = (EditText) rootView.findViewById(R.id.yearListText);
        yearText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        yearEntered = Integer.parseInt(yearText.getText().toString());
                        if (yearEntered <= 1970 || yearEntered >= 2017)
                            yearText.setError("Year must be in between 1970 and 2017");
                    } catch (Exception e) {
                    }
                }
            }
        });

        Button filterButton = (Button) rootView.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearText.clearFocus();
                validation();
            }
        });

        return rootView;
    }

    public void validation() {

        if (yearText.getText().toString().length() != 0)
            new DownloadList().execute("http://bismarck.sdsu.edu/hometown/users?year=" + yearText.getText());
        if (countrySpinner.getSelectedItemPosition() != 0)
            new DownloadList().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl);
        if (stateSpinner.getSelectedItemPosition() != 0)
            new DownloadList().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected));
        if (yearText.getText().toString().length() != 0 && countrySpinner.getSelectedItemPosition() != 0 && stateSpinner.getSelectedItemPosition() != 0)
            new DownloadList().execute("http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected) + "&year=" + yearText.getText());
        else if (yearText.getText().toString().length() == 0 && stateSpinner.getSelectedItemPosition() == 0 && countrySpinner.getSelectedItemPosition() == 0)
            new DownloadList().execute("http://bismarck.sdsu.edu/hometown/users");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ViewByListFragment.DownloadJSON().execute();

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
            countrySpinner = (Spinner) getActivity().findViewById(R.id.countryListSpinner);

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
                                    stateSpinner = (Spinner) getActivity().findViewById(R.id.stateListSpinner);
                                    stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            stateList));
                                } else {
                                    stateList.clear();
                                    stateList.add("Select State");
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
            stateSpinner = (Spinner) getActivity().findViewById(R.id.stateListSpinner);
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

    private class DownloadList extends AsyncTask<String, Void, ArrayList<Person>> {

        String result;

        @Override
        protected ArrayList<Person> doInBackground(String... params) {
            users = new ArrayList<String>();
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
                //System.out.println("Response  Nick Name is "+value);
                result = sb.toString();
                jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);

                    Person worldpop = new Person();

                    worldpop.setNickname(jsonobject.optString("nickname"));
                    System.out.println(worldpop.getNickname());
                    worldpop.setCountry(jsonobject.optString("country"));
                    worldpop.setState(jsonobject.optString("state"));
                    worldpop.setCity(jsonobject.optString("city"));
                    worldpop.setYear(jsonobject.optInt("year"));
                    userList.add(worldpop);

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {
            list = (ListView) getActivity().findViewById(R.id.listView);
            if (result.size() != 0) {
                CustomListAdapter adapter = new CustomListAdapter(getContext(), R.layout.custom_row, result);
                list.setAdapter(adapter);
            } else if (result.size() == 0) {
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
                list.setAdapter(null);
            }
        }

    }

}

