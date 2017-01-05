package com.mobop.michael_david.stufftracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditItemFragment extends Fragment {

    public static final int FRAGMENT_ID  = 3;

    private StuffItem currentItem = new StuffItem();
    private Uri cameraImageUri;
    private Bitmap rotatedFinalImage;
    public static boolean checkInDatabase = true;
    private static final int PICTURE_REQUEST = 1;
    Button btnAddEditItem;
    EditText edtName, edtBrand, edtModel, edtNote;
    ImageView ivStuffPicture;
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
        if(checkInDatabase) checkIfTagIdExists(nfcTag);
        checkInDatabase = true;
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
        edtBrand = (EditText)view.findViewById(R.id.edtBrand);
        edtModel = (EditText)view.findViewById(R.id.edtModel);
        edtName = (EditText)view.findViewById(R.id.edtName);
        edtNote = (EditText)view.findViewById(R.id.edtNote);
        ivStuffPicture = (ImageView)view.findViewById(R.id.ivStuffPicture);
        tvNfcId = (TextView)view.findViewById(R.id.tvNfcId);

        // Set listeners
        ivStuffPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrModifyPicture();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set views
        // We do this in onResume instead of onCreateView, otherwise the views can't be correctly
        // updated. See http://stackoverflow.com/q/13303469/1975002 for more explanation.
        tvNfcId.setText(getResources().getString(R.string.nfc_tag_id, nfcTag));
        //TODO edtBrand.setText(currentItem.getBrand);
        //TODO edtModel.setText(currentItem.getModel);
        edtName.setText(currentItem.getName());
        edtNote.setText(currentItem.getDescription());
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
        //TODO : reset here the value of MainActivity.lastItemIndex ?
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_validate_edit_menu:
                // TODO : Validate edit to database and quit fragment
                addEditItem(); // Add item to database

                // Report to main activity to change the current fragment and refresh recycler view
                mListener.onFragmentQuit(FRAGMENT_ID, 0);
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

    public void setCurrentItem(StuffItem currentItem) {
        this.currentItem = currentItem;
        checkInDatabase = false;
    }

    /**
     * Check if the tag id already exists in the database.
     * If a result is found, then it becomes the currentItem.
     * @param nfcTagId the NFC tag id to check.
     */
    public void checkIfTagIdExists(String nfcTagId) {
        // Query database
        Cursor cursor = dbHandler.getDataFromTagIfExists(nfcTagId);
        if (cursor.moveToFirst()) { // Result found; create a StuffItem from it
            currentItem =  new StuffItem();
            currentItem.setDescription(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NOTE)));
            currentItem.setName(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NAME)));
            currentItem.setNfcId(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_TAG)));
            //TODO: set other fields.
        }
        else { // The NFC tag id is not yet known.
            //TODO: 'resetting' the currentItem should be somewhere else.
            currentItem =  new StuffItem(); // reset currentItem by creating a new, empty one
        }
    }

    public void addEditItem() {
        // Prepare the values to insert in the database
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_TAG, nfcTag);
        values.put(DBHandler.COLUMN_NAME, edtName.getText().toString());
        values.put(DBHandler.COLUMN_BRAND, edtBrand.getText().toString());
        values.put(DBHandler.COLUMN_MODEL, edtModel.getText().toString());
        values.put(DBHandler.COLUMN_NOTE, edtNote.getText().toString());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.insert(DBHandler.TABLE_ITEMS, null, values);
        Toast.makeText(getActivity(), "Element stored in the database.", Toast.LENGTH_SHORT).show();

        // Report to main activity to change the current fragment and refresh recycler view
        mListener.onFragmentQuit(FRAGMENT_ID, 0);
    }

    public void addOrModifyPicture() {
        openImageIntent();
    }

    /**
     * Image Picker, allowing user to select a picture from a gallery app or by taking one with the Camera.
     */
    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "StuffTracker" + File.separator);
        root.mkdirs();
        final String filename = StringUtils.getDateTimeFilename(".jpg");
        final File sdImageMainDirectory = new File(root, filename);
        cameraImageUri = Uri.fromFile(sdImageMainDirectory);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo resolveInfo : listCam) {
            final String packageName = resolveInfo.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraIntents.add(intent);
        }

        // Filesystem
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        // Chooser of filesystem options
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.add_picture_from));

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, PICTURE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Process the result if it's OK (user finished the action)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera) {
                    selectedImageUri = cameraImageUri;
                } else {
                    selectedImageUri = data.getData();
                }
                Bitmap resizedImage = BitmapUtils.decodeAndResizeBitmapFromFile(getFilepathFromContentUri(selectedImageUri), 300, 300);
                rotatedFinalImage = BitmapUtils.getRotatedImage(ImageUtils.getImageRotation(getActivity().getApplicationContext(), selectedImageUri), resizedImage);

                ivStuffPicture.setImageBitmap(rotatedFinalImage);
            }
        }
    }

    /**
     * Converts a ContentUri (currently only if pointing to MediaStore) to the corresponding filepath.
     * Example : content://media/external/images/media/66911 --> /storage/A4F8-A472/DCIM/100ANDRO/DSC_0814.JPG
     *
     * If the Uri is already a filepath (like file:///storage/emulated/0/file.ext), returns the corresponding String.
     *
     * @param contentUri A MediaStore ContentUri.
     * @return the corresponding filepath.
     */
    public String getFilepathFromContentUri(Uri contentUri) {
        Cursor cursor = null;
        String path = null;

        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);

            if(cursor == null) { // provided Uri is probably a filepath already.
                path = contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
}
