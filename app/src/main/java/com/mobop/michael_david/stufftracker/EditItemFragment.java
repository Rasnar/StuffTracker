package com.mobop.michael_david.stufftracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobop.michael_david.stufftracker.utils.BitmapUtils;
import com.mobop.michael_david.stufftracker.utils.ImageUtils;
import com.mobop.michael_david.stufftracker.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditItemFragment extends Fragment {

    public static final int FRAGMENT_ID = 3;

    /**
     * Can be used to store the index (in StuffItemsManager) of an existing item,
     * so its data can be retrieved to display and/or update.
     */
    public static Integer selectedItemIndex;

    private enum EDIT_MODE {READ_ONLY, EDITABLE}

    private StuffItem currentItem;
    private Uri cameraImageUri;
    private Bitmap rotatedFinalImage;
    private static final int PICTURE_REQUEST = 1;
    private Button btnSelectCategories, btnDateStart, btnDateStop;
    private EditText edtName, edtBrand, edtModel, edtNote, edtNfcTagId, edtLoanPersonName;
    private TextView tvPersonLoan, tvDateStart, tvDateEnd;
    private SwitchCompat swEnableLoan;
    private boolean changeFragment;

    ImageView ivStuffPicture;

    ArrayList<String> selectedCategories = new ArrayList<String>();

    boolean newItem = true;

    EDIT_MODE currentEditMode = EDIT_MODE.READ_ONLY;

    private String scannedNfcTagId;
    private DBHandler dbHandler;

    // Listener to communicate with activity
    OnFragmentInteractionListener mListener;

    public EditItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHandler = new DBHandler(getActivity().getApplicationContext());

        // Always start with an empty StuffItem.
       currentItem = new StuffItem();

        // If an item index has been set, we get the corresponding StuffItem.
        if (selectedItemIndex != null) {
            setCurrentItemFromIndex(selectedItemIndex);
            newItem = false;
            selectedItemIndex = null;
        }

        // If a NFC tag has been scanned, we add its id to the current item,
        // and we check if it's already in the database.
        if (scannedNfcTagId != null) {
            currentItem.setNfcTagId(scannedNfcTagId);
            checkIfTagIdExists(scannedNfcTagId);
            scannedNfcTagId = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.edit_item_fragment, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.edit_item_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        btnSelectCategories = (Button) view.findViewById(R.id.btnCategories);
        btnSelectCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategories();
            }
        });
        edtBrand = (EditText) view.findViewById(R.id.edtBrand);
        edtModel = (EditText) view.findViewById(R.id.edtModel);
        edtName = (EditText) view.findViewById(R.id.edtName);
        edtNfcTagId = (EditText) view.findViewById(R.id.edtNfcTagId);
        edtNote = (EditText) view.findViewById(R.id.edtNote);
        ivStuffPicture = (ImageView) view.findViewById(R.id.ivStuffPicture);

        swEnableLoan = (SwitchCompat) view.findViewById(R.id.swEnableLoan);

        edtLoanPersonName = (EditText) view.findViewById(R.id.edtLoanPersonName);
        btnDateStart = (Button) view.findViewById(R.id.btnDateStart);
        btnDateStop = (Button) view.findViewById(R.id.btnDateStop);
        tvPersonLoan = (TextView) view.findViewById(R.id.tvPersonLoan);
        tvDateStart = (TextView) view.findViewById(R.id.tvDateStart);
        tvDateEnd = (TextView) view.findViewById(R.id.tvDateEnd);

        // Set listeners
        ivStuffPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrModifyPicture();
            }
        });

        swEnableLoan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    setLoanConfigurationVisible(true);
                }
                else {
                    setLoanConfigurationVisible(false);
                }
            }
        });

        btnDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog fromDatePickerDialog;
            }
        });


        if(newItem) {
            setContentMode(EDIT_MODE.EDITABLE);
        } else {
            setContentMode(EDIT_MODE.READ_ONLY);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set views
        // We do this in onResume instead of onCreateView, otherwise the views can't be correctly
        // updated. See http://stackoverflow.com/q/13303469/1975002 for more explanation.
        //TODO edtBrand.setText(currentItem.getBrand);
        //TODO edtModel.setText(currentItem.getModel);
        //TODO extract selectedCategories
        edtName.setText(currentItem.getName());
        edtNfcTagId.setText(currentItem.getNfcTagId());
        edtNote.setText(currentItem.getDescription());
        if (currentItem.getImage() != null) {
            ivStuffPicture.setImageBitmap(currentItem.getImage());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
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
        changeFragment = false;
        switch (item.getItemId()) {
            case R.id.action_validate_edit_menu:
                // TODO : Validate edit to database and quit fragment
                addEditItem(); // Add item to database
                changeFragment = true;
                break;

            case R.id.action_delete_item:
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle(R.string.delete_item_from_database_title)
                        .setMessage(R.string.delete_item_from_database_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete item from database
                                dbHandler.deleteItem(currentItem.getNfcTagId());
                                changeFragment = true;
                            }


                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;

            case R.id.action_edit_edit_menu:
                setContentMode(EDIT_MODE.EDITABLE);
                getActivity().invalidateOptionsMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        if (changeFragment) {
            // Tell to main activity to change the current fragment and refresh recycler view
            mListener.onFragmentQuit(FRAGMENT_ID, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (currentEditMode == EDIT_MODE.EDITABLE) {
            inflater.inflate(R.menu.edit_menu, menu);
        } else if (currentEditMode == EDIT_MODE.READ_ONLY) {
            inflater.inflate(R.menu.info_menu, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Store the id of the NFC tag that has been scanned.
     * @param nfcTagId the NFC tag id.
     */
    public void setScannedNfcTagId(String nfcTagId) {
        this.scannedNfcTagId = nfcTagId;
    }

    /**
     * Set the current StuffItem as the item at the specified index in StuffItemsManager.
     * @param index index of the item.
     */
    private void setCurrentItemFromIndex(int index) {
        this.currentItem = StuffItemsManager.getInstance().getItem(index);
    }

    /**
     * Check if the tag id already exists in the database.
     * If a result is found, then it becomes the currentItem.
     *
     * @param nfcTagId the NFC tag id to check.
     */
    public void checkIfTagIdExists(String nfcTagId) {
        // Query database
        Cursor cursor = dbHandler.getDataFromTagIfExists(nfcTagId);
        if (cursor.moveToFirst()) { // Result found; create a StuffItem from it
            currentItem.setDescription(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NOTE)));
            currentItem.setName(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NAME)));
            byte[] pictureByteArray = cursor.getBlob(cursor.getColumnIndex(DBHandler.COLUMN_PICTURE));
            if(pictureByteArray != null) {
                currentItem.setImage(BitmapUtils.getBitmap(pictureByteArray));
            }
            //TODO: set other fields.
            newItem = false;
        } else { // The NFC tag id is not yet known.
            newItem = true;
        }
    }

    public void addEditItem() {
        // Prepare the values to insert in the database
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_TAG, edtNfcTagId.getText().toString());
        values.put(DBHandler.COLUMN_NAME, edtName.getText().toString());
        values.put(DBHandler.COLUMN_BRAND, edtBrand.getText().toString());
        values.put(DBHandler.COLUMN_MODEL, edtModel.getText().toString());
        values.put(DBHandler.COLUMN_NOTE, edtNote.getText().toString());
        if (rotatedFinalImage != null) {
            values.put(DBHandler.COLUMN_PICTURE, BitmapUtils.getByteArray(rotatedFinalImage));
        }

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
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }

                Uri selectedImageUri;
                if (isCamera) {
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

    private void selectCategories(){

        AlertDialog dialog;
        //following code will be in your activity.java file

        final ArrayList<String> categoriesList = new ArrayList<>(Arrays.asList((getResources().getStringArray(R.array.categories_names))));
        final CharSequence[] categoriesItems = categoriesList.toArray(new CharSequence[categoriesList.size()]);

        // arraylist to keep the selected items
        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

        final boolean[] checkedItems = new boolean[categoriesList.size()];

        // Already selected categories
        for(String category : selectedCategories){
            checkedItems[categoriesList.indexOf(category)] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select categories");
        builder.setMultiChoiceItems(categoriesItems, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            checkedItems[indexSelected] = true;
                        } else if (selectedItems.contains(indexSelected)) {
                            checkedItems[indexSelected] = false;
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedCategories = new ArrayList<String>();

                        for (int i = 0; i < categoriesList.size(); i++){
                            if(checkedItems[i]){
                                selectedCategories.add(categoriesItems[i].toString());
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
    }

    /**
     * Set an EditText to a read-only or editable state.
     * Useful link : http://stackoverflow.com/a/4297791/1975002
     * @param edtx
     * @param editable
     */
    private void setEditableEditText(EditText edtx, boolean editable) {
        if(editable){
            edtx.setInputType(InputType.TYPE_CLASS_TEXT);
            edtx.getBackground().clearColorFilter();
        } else {
            edtx.setInputType(InputType.TYPE_NULL);
            edtx.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        }

    }

    /**
     * Set the Fragment content as read-only or editable.
     * @param mode an EDIT_MODE.
     */
    private void setContentMode(EDIT_MODE mode) {

        currentEditMode = mode;

        boolean editable = false;
        if (mode == EDIT_MODE.EDITABLE) {
            editable = true;
        } else if (mode == EDIT_MODE.READ_ONLY) {
            editable = false;
        }
        setEditableEditText(edtName, editable);
        setEditableEditText(edtBrand, editable);
        setEditableEditText(edtModel, editable);
        setEditableEditText(edtNote, editable);
        setEditableEditText(edtNfcTagId, editable);

        ivStuffPicture.setFocusable(editable);
        ivStuffPicture.setClickable(editable);

        btnSelectCategories.setEnabled(editable);

        if(editable){
            btnSelectCategories.setVisibility(View.VISIBLE); // Show button
        } else {
            btnSelectCategories.setVisibility(View.GONE); // Hide button
        }
    }

    /**
     * Set if the configuration box for the loan is visible or not.
     * @param visible show everything if true, nothing otherwise.
     */
    private void setLoanConfigurationVisible(boolean visible) {
        if(visible) {
            tvPersonLoan.setVisibility(View.VISIBLE);
            tvDateStart.setVisibility(View.VISIBLE);
            tvDateEnd.setVisibility(View.VISIBLE);
            btnDateStart.setVisibility(View.VISIBLE);
            btnDateStop.setVisibility(View.VISIBLE);
            edtLoanPersonName.setVisibility(View.VISIBLE);
        } else {
            tvPersonLoan.setVisibility(View.GONE);
            tvDateStart.setVisibility(View.GONE);
            tvDateEnd.setVisibility(View.GONE);
            btnDateStart.setVisibility(View.GONE);
            btnDateStop.setVisibility(View.GONE);
            edtLoanPersonName.setVisibility(View.GONE);
        }
    }


    /**
     * Converts a ContentUri (currently only if pointing to MediaStore) to the corresponding filepath.
     * Example : content://media/external/images/media/66911 --> /storage/A4F8-A472/DCIM/100ANDRO/DSC_0814.JPG
     * <p>
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

            if (cursor == null) { // provided Uri is probably a filepath already.
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
