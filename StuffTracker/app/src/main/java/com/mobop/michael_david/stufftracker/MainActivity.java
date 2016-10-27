package com.mobop.michael_david.stufftracker;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends NfcBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }
    }
}
