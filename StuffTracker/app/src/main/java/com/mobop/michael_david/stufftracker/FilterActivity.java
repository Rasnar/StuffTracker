package com.mobop.michael_david.stufftracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterActivity extends AppCompatActivity {

    TextView nameTextView;
    TextView brandTextView;
    TextView modelTextView;
    TextView nfcIdTextView;

    ArrayList<CheckBox> categoriesCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.filter_menu_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        nameTextView = (TextView)findViewById(R.id.item_name_filter);
        brandTextView = (TextView)findViewById(R.id.item_brand_filter);
        modelTextView = (TextView)findViewById(R.id.item_model_filter);
        nfcIdTextView = (TextView)findViewById(R.id.item_nfc_id_filter);

        // Retrieve constant categories
        ArrayList<String> categories = new ArrayList<>(Arrays.asList((getResources().getStringArray(R.array.categories_names))));


        LinearLayout categoriesCheckboxesLayout=(LinearLayout)findViewById(R.id.categoriesCheckboxesLayout);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        categoriesCheckboxes = new ArrayList<CheckBox>();

        // Dynamicly create checkboxes
        for (String categorie: categories) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setLayoutParams(params);
            checkBox.setText(categorie);
            checkBox.setTextColor(getResources().getColor(R.color.colorAccent));
            checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            categoriesCheckboxes.add(checkBox);

            categoriesCheckboxesLayout.addView(checkBox);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_validate_filter:
                // TODO : Test if return object is correct

                ArrayList<String> categoriesChecked = new ArrayList<String>();
                for (CheckBox checkBox: categoriesCheckboxes) {

                    // Find which textbox is selected
                    if(checkBox.isChecked()){
                        categoriesChecked.add(checkBox.getText().toString());
                    }
                }

                FilterStuffItems filterStuffItems = new FilterStuffItems(
                        nameTextView.getText().toString(),
                        brandTextView.getText().toString(),
                        modelTextView.getText().toString(),
                        nfcIdTextView.getText().toString(),
                        categoriesChecked);


                Intent i = new Intent();
                i.putExtra("filter_stuff_items", filterStuffItems);

                finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

}
