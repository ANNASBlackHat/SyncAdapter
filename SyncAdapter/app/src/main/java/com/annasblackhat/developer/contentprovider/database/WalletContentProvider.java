package com.annasblackhat.developer.contentprovider.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Sasha Grey on 5/27/2016.
 */

public class WalletContentProvider extends ContentProvider {

    private static final String TAG = "NotesContentProvider";

    private static final String DATABASE_NAME = "wallet.db";

    private static final int DATABASE_VERSION = 1;

    private static final String WALLET_TABLE_NAME = "wallet";

    public static final String AUTHORITY = "com.annasblackhat.developer.contentprovider";

    public static final String ACOOUNT_TYPE = "com.annasblackhat.developer.contentprovider";

    private static final UriMatcher sUriMatcher;

    private static final int WALLET = 1;

    private static final int WALLET_ID = 2;

    private static HashMap<String, String> notesProjectionMap;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, WALLET_TABLE_NAME, WALLET);
        sUriMatcher.addURI(AUTHORITY, WALLET_TABLE_NAME + "/#", WALLET_ID);

        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(WalletContract.WALLET_ID, WalletContract.WALLET_ID);
        notesProjectionMap.put(WalletContract.TITLE, WalletContract.TITLE);
        notesProjectionMap.put(WalletContract.TOTAL, WalletContract.TOTAL);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(WALLET_TABLE_NAME);
        qb.setProjectionMap(notesProjectionMap);

        switch (sUriMatcher.match(uri)) {
            case WALLET:
                break;
            case WALLET_ID:
                selection = selection + WalletContract.WALLET_ID + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WALLET:
                return WalletContract.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case WALLET:
                break;
            case WALLET_ID:
                where = where + WalletContract.WALLET_ID +uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(WALLET_TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case WALLET:
                count = db.update(WALLET_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != WALLET) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(WALLET_TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(WalletContract.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (sUriMatcher.match(uri) != WALLET) {
            return super.bulkInsert(uri, values);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for(ContentValues value : values){
                long rowId = db.insert(WALLET_TABLE_NAME, null, value);
                if(rowId != -1){
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + WALLET_TABLE_NAME + " (" + WalletContract.WALLET_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + WalletContract.TITLE + " VARCHAR(255)," + WalletContract.TOTAL + " VARCHAR(255)" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}