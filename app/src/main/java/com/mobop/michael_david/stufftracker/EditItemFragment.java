package com.mobop.michael_david.stufftracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditItemFragment extends Fragment {

    public static final int FRAGMENT_ID  = 3;

    Button btnAddEditItem;
    EditText edtName, edtBrand, edtModel, edtNote;
    TextView tvNfcId;

    private String nfcTag;
    private DBHandler dbHandler;

    // Listener to communicate with activity
    OnFragmentInteractionListener mListener;

    public EditItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dbHandler = new DBHandler(getActivity().getApplicationContext());

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.edit_item_fragment, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.edit_item_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if(((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        btnAddEditItem = (Button)view.findViewById(R.id.btnAddEdit);
        btnAddEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditItem();
            }
        });
        edtName = (EditText)view.findViewById(R.id.edtName);
        edtBrand = (EditText)view.findViewById(R.id.edtBrand);
        edtModel = (EditText)view.findViewById(R.id.edtModel);
        edtNote = (EditText)view.findViewById(R.id.edtNote);
        tvNfcId = (TextView)view.findViewById(R.id.tvNfcId);

        // Set views
        tvNfcId.setText(getResources().getString(R.string.nfc_tag_id, nfcTag));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener"); }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_validate_edit_menu:
                // TODO : Validate edit to database and quit fragment
                addEditItem(); // Add item to database

                // Report to main activity to change the current fragment and refresh recycler view
                mListener.onFragmentQuit(FRAGMENT_ID);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void setNfcTag(String nfcTag) {

        this.nfcTag = nfcTag;
    }

    public void addEditItem() {
        // Prepare the values to insert in the database
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_NAME, edtName.getText().toString());
        values.put(DBHandler.COLUMN_BRAND, edtBrand.getText().toString());
        values.put(DBHandler.COLUMN_MODEL, edtModel.getText().toString());
        values.put(DBHandler.COLUMN_NOTE, edtNote.getText().toString());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.insert(DBHandler.TABLE_ITEMS, null, values);
        Toast.makeText(getActivity(), "Element stored in the database.", Toast.LENGTH_SHORT).show();

        // Report to main activity to change the current fragment and refresh recycler view
        mListener.onFragmentQuit(FRAGMENT_ID);
    }
}
