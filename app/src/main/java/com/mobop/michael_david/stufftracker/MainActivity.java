package com.mobop.michael_david.stufftracker;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.mobop.michael_david.stufftracker.utils.BitmapUtils;
import com.mobop.michael_david.stufftracker.utils.StringUtils;

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

        // Check if the app has the "dangerous" permissions enabled (this is needed since API level 23)
        // See : https://developer.android.com/training/permissions/requesting.html
        //TODO : if rights are not granted, ask again when the user wants to take a picture (and block the functionality otherwise).
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

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
     * @param intent the intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if ((intent != null) && (intent.getAction() != null) && (intent.getAction().contains("android.nfc"))) {
            Tag tagData = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagId = StringUtils.bytesToHex(tagData.getId());

            // Do not redisplay the fragment if it's already active with a tag id
            if(!editItemFragment.isVisible()) {
                editItemFragment.setScannedNfcTagId(tagId);
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
            String nfcTagId = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_TAG));
            String note = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NOTE));
            byte[]pictureBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHandler.COLUMN_PICTURE));
            Bitmap picture;
            if(pictureBlob != null) {
                picture = BitmapUtils.getBitmap(pictureBlob);
            }
            else {
                picture = BitmapFactory.decodeResource(getResources(),R.drawable.default_photo);
            }

            stuffItemsManager.addStuffItem(new StuffItem(
                    picture,
                    name,
                    note,
                    "categories",
                    nfcTagId,
                    null,
                    null));
        }

        /**
         * TESTS StuffItemsManager
         * TODO : Remove before final build
         */

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, 1988);
//        cal.set(Calendar.MONTH, Calendar.JANUARY);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        Date date1 = cal.getTime();
//
//        cal.set(Calendar.YEAR, 1988);
//        cal.set(Calendar.MONTH, Calendar.JANUARY);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        Date date2 = cal.getTime();
//
//        stuffItemsManager.addStuffItem(new StuffItem(BitmapFactory.decodeResource(getResources(),
//                R.drawable.default_photo), "TEST OBJECT 1",
//                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
//                "PC, Tablet, Dinosaur, mommy",
//                "01020305060405",
//                date1,
//                date2));

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
                fragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, editItemFragment)
                        .addToBackStack(null)
                        .commit();
            }

            // Start a new item creation fragment without an NFC number
            if (actionId == StuffItemsListFragment.ACTION_ID_ADD_NEW_ITEM) {
                // TODO : When the NFC taf is null the textview should be editable for the user to add his owm ID
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
