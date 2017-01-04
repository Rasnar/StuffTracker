package com.mobop.michael_david.stufftracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends NfcBaseActivity implements
        OnFragmentInteractionListener {

    public static int lastSelectedItemIndex = -1;
    private DBHandler dbHandler;

    private static final String TAG = MainActivity.class.getSimpleName();

    private StuffItemsManager stuffItemsManager;
    private FragmentManager fragmentManager;
    private FilterFragment filterFragment;
    private EditItemFragment editItemFragment;

    FilterStuffItems filterStuffItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_container);
        dbHandler = new DBHandler(getApplicationContext());

        updateStuffManager();

        fragmentManager = getFragmentManager();

        StuffItemsListFragment stuffItemsListFragment = new StuffItemsListFragment();
        stuffItemsListFragment.setStuffItemsManager(stuffItemsManager);

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

            // Do not redisplay the fragment if it's already active with a tag id
            if(!editItemFragment.isVisible()){
                editItemFragment.setNfcTag(tagId);
                fragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, editItemFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    /**
     * Load the database data and refresh the recycler view fragment
     */
    public void updateStuffManager() {
        stuffItemsManager = StuffItemsManager.getInstance();
        stuffItemsManager.deleteAllItems();

        Cursor cursor;
        if(filterStuffItems == null) {
            cursor = dbHandler.getAllItems();
        } else {
            // TODO : use filter parameters to request the database
            cursor = dbHandler.getAllItems();
        }

        //TODO: Toast.makeText(this, "Number of items : " + cursor.getCount(), Toast.LENGTH_SHORT).show();

        //TODO: complete with all StuffItem fields, when they are known.
        while (cursor.moveToNext()) {
            // Get the data
            String name = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NAME));
            String brand = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_BRAND));
            String model = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_MODEL));
            String note = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NOTE));

            stuffItemsManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_photo),
                    name,
                    note,
                    "categories",
                    "",
                    null,
                    null));
        }

        /**
         * TESTS StuffItemsManager
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

        stuffItemsManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method called every time a fragment want to communicate with the main activity
     * @param fragmentCaller An ID to define which fragment made a call
     * @param actionId An action can be defined to a specific fragment
     */
    @Override
    public void onFragmentQuit(int fragmentCaller, int actionId) {
        if (fragmentCaller == StuffItemsListFragment.FRAGMENT_ID) {
            if(actionId == StuffItemsListFragment.ACTION_ID_REFRESH_LIST) {
                updateStuffManager();
            }

            if(actionId == StuffItemsListFragment.ACTION_ID_START_FILTER_FRAGMENT) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, filterFragment)
                        .addToBackStack(null)
                        .commit();
            }

            // An item as been selected in the list, show related info.
            if (actionId == StuffItemsListFragment.ACTION_ID_SHOW_ITEM_INFO) {
                editItemFragment.setCurrentItem(StuffItemsManager.getInstance().getItem(lastSelectedItemIndex));
                fragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, editItemFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

        // New filter selected
        if (fragmentCaller == FilterFragment.FRAGMENT_ID) {

            fragmentManager.popBackStack();

            // Todo : Refresh recycler view with new filter
            FilterStuffItems filter = filterFragment.getFilterStuffItems();
            updateStuffManager();
        }

        // New element added to the database
        if (fragmentCaller == EditItemFragment.FRAGMENT_ID) {

            fragmentManager.popBackStack();

            // Todo : Refresh recycler view with all elements
            updateStuffManager();
        }
    }
}
