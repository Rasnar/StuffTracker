package com.mobop.michael_david.stufftracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class MainActivity extends NfcBaseActivity {

    private DBHandler dbHandler;

    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private StuffTrackerManager stuffTrackerManager;


    private RecyclerItemClickListener.OnItemClickListener stuffListListener
            = new RecyclerItemClickListener.OnItemClickListener() {

        public void onItemClick(View v, int position) {
            System.gc();

            Log.d(TAG, "onItemClick: ItemClicked" + position);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_menu_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        initStuffManager();

        dbHandler = new DBHandler(getApplicationContext());
    }

    /**
     * Called when a new intent occurs. In our case, we check for NFC intents.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if ((intent != null) && (intent.getAction() != null) && (intent.getAction().contains("android.nfc"))) {
            Tag tagData = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagId = StringUtils.bytesToHex(tagData.getId());

            // Query database
            Cursor cursor = dbHandler.getDataFromTagIfExists(tagId);
            if (cursor.moveToFirst()) { // Result(s) found;

            }
            else {
                //TODO : tag doesn't exist yet in database
                Toast.makeText(this, "Tag id : " + tagId, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void initStuffManager() {

        stuffTrackerManager = new StuffTrackerManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(stuffTrackerManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, stuffListListener));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                // TODO : Start the new activity with the filter menu
                return true;

            case R.id.action_refresh:
                //TODO : Refresh list elements with the StuffTrackerManager
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
