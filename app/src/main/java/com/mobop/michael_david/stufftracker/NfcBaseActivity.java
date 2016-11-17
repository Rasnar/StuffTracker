package com.mobop.michael_david.stufftracker;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * NFC handler, making an activity respond to NFC events.
 * It also :
 * - detects if the device supports NFC or not.
 * - opens device's NFC settings if NFC is disabled.
 *
 */

public abstract class NfcBaseActivity extends AppCompatActivity {
    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    protected String[][] techList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Stop if the device doesn't support NFC
        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.device_doesnt_support_nfc, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Open device's NFC settings if NFC is disabled.
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, R.string.please_enable_nfc, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }

        // Create a generic PendingIntent that will be delivered to this activity. The NFC stack will
        // fill in the intent with the details of the discovered tag before delivering to this activity.
        pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);

        techList = new String[][]{};
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, techList);
    }

    @Override
    protected abstract void onNewIntent(Intent intent);
}

