package com.mobop.michael_david.stufftracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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

import java.util.Calendar;
import java.util.Date;

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
        setContentView(R.layout.activity_main);

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
                //TODO : go to InfoItemActivity ...
            }
            else { // tag doesn't exist yet in database ; go to next activity to add it.
                Intent editItemActivity = new Intent(this, EditItemActivity.class);
                editItemActivity.putExtra("TAG", tagId);
                startActivity(editItemActivity);
            }

        }
    }

    public void initStuffManager() {

        stuffTrackerManager = StuffTrackerManager.getInstance();

        /**
         * TESTS StuffTrackerManager
         * TODO : Remove before final build
         */

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date1 = cal.getTime();

        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date2 = cal.getTime();

        stuffTrackerManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
                R.drawable.default_photo), "TEST OBJECT 1",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));

        stuffTrackerManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
                R.drawable.default_photo), "TEST OBJECT 2",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));

        stuffTrackerManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
                R.drawable.default_photo), "TEST OBJECT 3",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));

        /**
         * END TESTS
         */

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
                // TODO : Start the new activity with the filter menu and implement return object parcelable
                Intent intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
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
