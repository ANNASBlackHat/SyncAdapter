package com.annasblackhat.developer.contentprovider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.annasblackhat.developer.contentprovider.database.WalletContract;
import com.annasblackhat.developer.contentprovider.sync.WalletSyncAdapter;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("xxxx Mainactivity...");

        WalletSyncAdapter.initializeSyncAdapter(this);
        loadData(null);

        getSupportLoaderManager().initLoader(0,null, this);
    }

    public void save(View view){
        String ket = ((EditText) findViewById(R.id.ket)).getText().toString();
        String total = ((EditText) findViewById(R.id.total)).getText().toString();


        ContentValues cv = new ContentValues();
        cv.put(WalletContract.TITLE, ket);
        cv.put(WalletContract.TOTAL, total);
        Uri uri = getContentResolver().insert(WalletContract.CONTENT_URI, cv);

        Toast.makeText(this, "Inserted ID : "+ ContentUris.parseId(uri),Toast.LENGTH_SHORT).show();

        //loadData(null);
    }

    private void loadData(Cursor cursor){
        //LOAD DATA
        if(cursor == null)
            cursor = getContentResolver().query(WalletContract.CONTENT_URI, null, null, null, null);
        String value = "";
        while(cursor.moveToNext()){
            value += cursor.getString(cursor.getColumnIndex(WalletContract.TITLE))+" - "+cursor.getString(cursor.getColumnIndex(WalletContract.TOTAL))+"\n";
        }
        ((TextView)findViewById(R.id.txt_value)).setText(value);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                WalletContract.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



}
