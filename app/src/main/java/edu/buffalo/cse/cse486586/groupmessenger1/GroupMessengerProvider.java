package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

  /*  public static final int VER = 1;
    private static final String GROUP_MESSENGER_DB = "groupMessenger.db"; // DB name
    private static final String GROUP_MESSENGER_TABLE = "groupMessengerTable"; // Table name

    private static final String KEY_ID = "_id";
    private static final String KEY_FIELD = "key"; // Table keys
    private static final String VALUE_FIELD = "value";

    private static final String CREATE_TABLE = "CREATE TABLE " + GROUP_MESSENGER_TABLE + " (" + KEY_ID + " TEXT , " + KEY_FIELD + " TEXT , " + VALUE_FIELD + " TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + GROUP_MESSENGER_TABLE;
    private static final String[] ALL_FIELDS = new String[]{KEY_ID, KEY_FIELD, VALUE_FIELD};


    private SQLiteDatabase db;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    protected void openDB() throws SQLiteException {
        db = mySQLiteOpenHelper.getWritableDatabase();
    }

    protected void closeDB() {
        db.close();
    }

    protected void clearDB()
    {
        db.execSQL("delete from " + GROUP_MESSENGER_TABLE);
    }*/

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        Log.v("insert", values.toString());


        String filename = values.getAsString("key");
        String value = values.getAsString("value") + "\n";
        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }

    /*    openDB();
        long rowID = db.insert(GROUP_MESSENGER_TABLE, null, values);
        closeDB();

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to add a record into " + uri);*/




        return uri;

    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        //mySQLiteOpenHelper = new MySQLiteOpenHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        Log.v("query", selection);

        /*openDB();
        Cursor cursor = db.query(GROUP_MESSENGER_TABLE, ALL_FIELDS, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        closeDB();
        return cursor;*/

        try {

            FileInputStream inputStream = getContext().openFileInput(selection);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String value = bufferedReader.readLine();
            Log.d(TAG, "Value is:" + value);
            bufferedReader.close();

            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"key", "value"});
            matrixCursor.addRow(new Object[]{selection, value});
            return matrixCursor;

        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }

        return null;
    }

 /*   class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public MySQLiteOpenHelper(Context context) {
            super(context, GROUP_MESSENGER_DB, null, VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (newVersion > oldVersion) {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }
        }
    }*/
}
