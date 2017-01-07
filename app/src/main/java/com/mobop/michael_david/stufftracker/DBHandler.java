package com.mobop.michael_david.stufftracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Local database handler.
 * - CRUD operations.
 * - Import and Export methods.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class DBHandler extends SQLiteOpenHelper {
    // To update the database, for example after changing the database schema,
    // the database version must be incremented.
    public static final String DATABASE_NAME = "stufftracker.db";
    public static final int DATABASE_VERSION = 2; // v2 : added 'categories' field.

    private static final String BLOB_TYPE = " BLOB";
    private static final String INT_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String DELIMITER = ",";

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_BORROWER = "borrower";
    public static final String COLUMN_LOAN_START = "loan_start";
    public static final String COLUMN_LOAN_END = "loan_end";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHandler.TABLE_ITEMS + " (" +
                    DBHandler.COLUMN_ID + TEXT_TYPE + DELIMITER +
                    DBHandler.COLUMN_NAME + TEXT_TYPE + DELIMITER +
                    DBHandler.COLUMN_BRAND + TEXT_TYPE + DELIMITER +
                    DBHandler.COLUMN_MODEL + TEXT_TYPE + DELIMITER +
                    DBHandler.COLUMN_NOTE + TEXT_TYPE + DELIMITER +
                    DBHandler.COLUMN_PICTURE + BLOB_TYPE +
                    DBHandler.COLUMN_CATEGORIES + TEXT_TYPE +
                    DBHandler.COLUMN_BORROWER + TEXT_TYPE +
                    DBHandler.COLUMN_LOAN_START + INT_TYPE + DELIMITER +
                    DBHandler.COLUMN_LOAN_END + INT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBHandler.TABLE_ITEMS;


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Same as onUpgrade, but keeps the actual version number.
    // Useful for testing purpose.
    public void resetDB(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    /**
     * Import a SQLite database from file.
     *
     * Helpful documentation : http://stackoverflow.com/a/6542214/1975002
     * @param context Context.
     * @param importedDBFilepath the path (on ExternalStorage) where the database file to import is located.
     * @return <code>true</code> if the export was successful, <code>false</code> otherwise.
     */
    public boolean importDB(Context context, String importedDBFilepath) {

        File applicationDB = context.getDatabasePath(DATABASE_NAME);
        FileChannel src = null, dst = null;
        try {
            File importedDB = new File(importedDBFilepath);

            if (!applicationDB.exists()) {
                // Open and close the SQLiteOpenHelper so it will commit the created empty database to internal storage.
                getWritableDatabase();
                close();
            }
            //TODO-optional: if database already exists, ask for confirmation before overwriting.
            src = new FileInputStream(importedDB).getChannel();
            dst = new FileOutputStream(applicationDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            return true;

        } catch(IOException e) {
            e.printStackTrace();

        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dst != null) {
                try {
                    dst.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Export (copy) the SQLite database file to the location specified.
     * @param context Application context.
     * @param absolutePath the absolute path to the exported database. (/.../mydir/DATABASE_NAME.db)
     * @return <code>true</code> if the export was successful, <code>false</code> otherwise.
     */
    public boolean exportDB(Context context, String absolutePath) {
        boolean result = false;
        FileChannel src = null, dst = null;

        File applicationDB = context.getDatabasePath(DATABASE_NAME);

        if(applicationDB.exists()) {
            if (isExternalStorageWritable()) {
                try {
                    if(new File(absolutePath.substring(0,absolutePath.lastIndexOf("/"))).mkdirs()) { // Create all dirs from filepath if they don't already exists
                        File exportedDB = new File(absolutePath);
                        src = new FileInputStream(applicationDB).getChannel();
                        dst = new FileOutputStream(exportedDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        result = true;
                    } else {
                        Toast.makeText(context, "Erreur, impossible de créer le dossier de destination.", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (src != null) {
                        try {
                            src.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (dst != null) {
                        try {
                            dst.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Erreur, impossible d'écrire dans la mémoire.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(context, "Erreur, base de données inexistante. Avez-vous déjà ajouté des données ?", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    /**
     * Search in the database if the provided id exists.
     * @param id the id to search for.
     * @return a Cursor to the results, null if the id doesn't exist yet in the database.
     */
    public Cursor getDataFromIdIfExists(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_ID + "= ?";
        String[] whereArgs;
        if(id != null) {
            whereArgs = new String[] {id};
        }
        else {
            whereArgs = null;
        }

        try {
            cursor = db.query(
                    DBHandler.TABLE_ITEMS,
                    null,           // all columns, as in SELECT * FROM...
                    whereClause,
                    whereArgs,
                    null,           // groupBy
                    null,           // having
                    null            // orderBy
            );
            Log.i("DB Query", "Number of results : " + cursor.getCount());
        } catch (SQLiteException e) {
            Log.e("DB Query", "Error while executing the query.");
        }

        return cursor;
    }

    /**
     * Gets all the items stored in the database.
     * @return a Cursor to the results.
     */
    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DBHandler.TABLE_ITEMS,
                    null,           // all columns, as in SELECT * FROM...DBHandler.TABLE_ITEMS,
                    null,
                    null,
                    null,           // groupBy
                    null,           // having
                    null            // orderBy
                    );
            Log.i("DB Query", "Number of results : " + cursor.getCount());
        } catch (SQLiteException e) {
            Log.e("DB Query", "Error while executing the query.");
        }

        return cursor;
    }

    /**
     * Delete the item with the provided id.
     * @param id the id of the item to delete.
     */
    public void deleteItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DBHandler.COLUMN_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};
        db.delete(DBHandler.TABLE_ITEMS, whereClause, whereArgs);
    }


    /* Checks if external storage is available for read and write */
    // Source : https://developer.android.com/guide/topics/data/data-storage.html#filesExternal
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
}
