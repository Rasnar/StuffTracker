package com.mobop.michael_david.stufftracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends NfcBaseActivity implements
        OnFragmentInteractionListener {

    private DBHandler dbHandler;

    private static final String TAG = MainActivity.class.getSimpleName();

    private StuffTrackerManager stuffTrackerManager;

    private FragmentManager fragmentManager;

    private StuffItemsListFragment stuffItemsListFragment;
    private FilterFragment filterFragment;

    private EditItemFragment editItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_container);
        dbHandler = new DBHandler(getApplicationContext());

        initStuffManager();

        fragmentManager = getFragmentManager();

        stuffItemsListFragment = new StuffItemsListFragment();
        stuffItemsListFragment.setStuffTrackerManager(stuffTrackerManager);

        filterFragment = new FilterFragment();
        editItemFragment = new EditItemFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, stuffItemsListFragment)
                .commit();
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
//                Intent editItemActivity = new Intent(this, EditItemActivity.class);
//                editItemActivity.putExtra("TAG", tagId);
//                startActivity(editItemActivity);

                editItemFragment.setNfcTag(tagId);

                fragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, editItemFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }
    }

    public void initStuffManager() {

        stuffTrackerManager = StuffTrackerManager.getInstance();


        stuffTrackerManager.deleteAllItems();

        //TODO:test when nothing in database
        Cursor cursor = dbHandler.getAllItems();
        Toast.makeText(this, "Number of items : " + cursor.getCount(), Toast.LENGTH_SHORT).show();

        if(cursor.moveToFirst()) {    // Result(s) found;
            while (cursor.moveToNext()) {
                stuffTrackerManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
                        R.drawable.default_photo), cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.COLUMN_NAME)),
                        "description",
                        "categories",
                        "",
                        null,
                        null));
            }
        }
        else {
            Toast.makeText(this, "Aucun objet enregistré.", Toast.LENGTH_LONG).show();
        }

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

        /**
         * END TESTS
         */
    }

    @Override
    public void onFragmentQuit(int fragmentCaller) {
        if (fragmentCaller == StuffItemsListFragment.FRAGMENT_ID) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment, filterFragment)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == FilterFragment.FRAGMENT_ID) {

            //actionBar.setDisplayHomeAsUpEnabled(true);

            fragmentManager.popBackStack();
            filterFragment.getFilterStuffItems();

        }
    }
}
