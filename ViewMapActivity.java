package com.example.shwetashahane.assignment4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class ViewMapActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_map);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_layout_map, new ViewByMapFragment()).commit();
        }
        System.out.println("View User By Map Activity");

    }
}
