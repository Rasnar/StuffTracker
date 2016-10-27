package com.mobop.michael_david.stufftracker;

import android.content.Intent;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends NfcBaseActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
