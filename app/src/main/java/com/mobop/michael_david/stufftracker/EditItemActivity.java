package com.mobop.michael_david.stufftracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Bundle extras = getIntent().getExtras();

        String tag;
        if(extras != null) {
            tag = extras.getString("TAG");
            TextView tvRfidId = (TextView)findViewById(R.id.tvRfidId);
            tvRfidId.append(" : " + tag);
        }
    }
}
