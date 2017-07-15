package com.example.shwetashahane.assignment4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button newUserButton = (Button) this.findViewById(R.id.newUserButton);
        Button viewUserButton = (Button) this.findViewById(R.id.viewUserButton);
        Button viewUserMapButton = (Button) this.findViewById(R.id.viewUserMapButton);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeNewuserActivity();
            }
        });

        viewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeViewUserActivity();
            }
        });

        viewUserMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeViewMapActivity();
            }
        });

    }

    public void invokeNewuserActivity() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    public void invokeViewUserActivity() {
        Intent intent = new Intent(this, ViewUserActivity.class);
        startActivity(intent);
    }

    public void invokeViewMapActivity() {
        Intent intent = new Intent(this, ViewMapActivity.class);
        startActivity(intent);
    }
}
