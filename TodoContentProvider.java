package com.eugenesumaryev.floatingbuttons;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by eugenesumaryev on 10/20/17.
 */
public class TodoContentProvider extends ContentProvider{

    public static final Uri CONTENT_URI =
            Uri.parse("content://com.eugenesumaryev.todoscontentprovider/todoitems");

    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_CREATION_DATE = "creation_date";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_NOTE = "note";
    public static final String KEY_DUEDATE = "due_date";

    private MySQLiteOpenHelper myOpenHelper;


    @Override
    public boolean onCreate(){
        // Construct the underlying database.
        // Defer opening the database until you need to perform
        // a query or transaction.
        myOpenHelper = new MySQLiteOpenHelper(getContext(),
                MySQLiteOpenHelper.DATABASE_NAME, null,
                MySQLiteOpenHelper.DATABASE_VERSION);

        return true;
    }

    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;

    //Populate the UriMatcher object, where a URI ending in 'todoitems' will
    //correspond to a request for all items, and 'todoitems/[rowID]'
    //represents a single row.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.eugenesumaryev.todoscontentprovider", "todoitems", ALLROWS);
        uriMatcher.addURI("com.eugenesumaryev.todoscontentprovider", "todoitems/#", SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // Return a string that identifies the MIME type
        // for a Content Provider URI
        switch (uriMatcher.match(uri)) {
            case ALLROWS: return "vnd.android.cursor.dir/vnd.todoitems";
            case SINGLE_ROW: return "vnd.android.cursor.item/vnd.todoitems";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }



    @Override
    public Cursor query(Uri url, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        // Open a read-only database.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // Replace these with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

        // If this is a row query, limit the result set to the passed in row.
        switch (uriMatcher.match(url)) {
            case SINGLE_ROW :
                String rowID = url.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowID);
            default: break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }


    @Override
    public Uri insert(Uri url, ContentValues values)	{

        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // To add empty rows to your database by passing in an empty Content Values
        // object, you must use the null column hack parameter to specify the name of
        // the column that can be set to null.
        String nullColumnHack = null;

        // Insert the values into the table
        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE,
                nullColumnHack, values);

        if (id > -1) {
            // Construct and return the URI of the newly inserted row.
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(insertedId, null);

            return insertedId;
        }
        else

            return null;
    }

    @Override
    public int delete(Uri url, String selection, String[] selectionArgs){

        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(url)) {
            case SINGLE_ROW :
                String rowID = url.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default: break;
        }

        // To return the number of deleted items, you must specify a where
        // clause. To delete all rows and return a value, pass in "1".
        if (selection == null)
            selection = "1";

        // Execute the deletion.
        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(url, null);

        return deleteCount;

    }

    @Override
    public int update(Uri url, ContentValues values,
                      String selection, String[]selectionArgs){

        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(url)) {
            case SINGLE_ROW :
                String rowID = url.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default: break;
        }

        // Perform the update.
        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE,
                values, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(url, null);

        return updateCount;
    }


    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "todosDatabase.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "todosItemTable";




        public MySQLiteOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        // SQL statement to create a new database.
        private static final String DATABASE_CREATE = "create table " +
                DATABASE_TABLE + " (" + KEY_ID +
                " integer primary key autoincrement, " +
                KEY_PRIORITY + " INTEGER, " +
                KEY_TASK + " TEXT, " +
                KEY_CREATION_DATE + " LONG, " +
                KEY_NOTE + " TEXT, " +
                KEY_DUEDATE + " TEXT);";



        //Called when no database exists on disk and the helper class needs to
        //create a new one
        @Override
        public void onCreate(SQLiteDatabase db){
            //Create database tables
            db.execSQL(DATABASE_CREATE);
        }

        //Called when there is a database version mismatch meaning that the
        //version of the databases on disk needs to be upgraded to the current
        //version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //upgrade database
            // Log the version upgrade.
            Log.w("TaskDBAdapter", "Upgrading from version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

            // Upgrade the existing database to conform to the new version. Multiple
            // previous versions can be handled by comparing oldVersion and newVersion
            // values.

            // The simplest case is to drop the old table and create a new one.
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
            // Create a new one.
            onCreate(db);

        }

    }


}
