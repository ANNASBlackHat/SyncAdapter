package com.annasblackhat.developer.contentprovider.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.annasblackhat.developer.contentprovider.R;
import com.annasblackhat.developer.contentprovider.Wallet;
import com.annasblackhat.developer.contentprovider.WalletApi;
import com.annasblackhat.developer.contentprovider.database.WalletContentProvider;
import com.annasblackhat.developer.contentprovider.database.WalletContract;

import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sasha Grey on 6/8/2016.
 */

public class WalletSyncAdapter extends AbstractThreadedSyncAdapter {
                     //Refresh every 5 seconds
    public static final int SYNC_INTERVAL = 5;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private WalletApi walletApi;
    private final String BASE_URL = "http://192.168.0.54:8080/wallet/";

    public WalletSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        System.out.println("xxxx constructor syncadapter....");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        walletApi = retrofit.create(WalletApi.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        System.out.println("xxxx onPerformSync...");
        walletApi.getAllData().enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(Call<List<Wallet>> call, Response<List<Wallet>> response) {
                if(response.isSuccessful()){
                    List<Wallet> wallets = response.body();
                    saveData(wallets);
                    System.out.println("xxxx data from server : "+wallets.size());
                    for(Wallet w : wallets){
                        System.out.println("xxxx Ket : "+w.getKeterangan());
                    }
                }else{
                    System.out.println("xxxx error, Response Code "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Wallet>> call, Throwable t) {

            }
        });
    }

    private void saveData(List<Wallet> walletList){
        Vector<ContentValues> cVVector = new Vector<ContentValues>(walletList.size());
        for (Wallet w : walletList){
            ContentValues cv = new ContentValues();
            cv.put(WalletContract.WALLET_ID, w.getId());
            cv.put(WalletContract.TITLE, w.getKeterangan());
            cv.put(WalletContract.TOTAL, String.valueOf(w.getTotal()));
            cVVector.add(cv);
        }

        int inserted = 0;
        if(walletList.size()>0){
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(WalletContract.CONTENT_URI, cvArray);
        }
        System.out.println("xxxx inserted success. "+inserted+" inserted");
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = WalletContentProvider.AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), WalletContentProvider.ACOOUNT_TYPE);

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        WalletSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, WalletContentProvider.AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                WalletContentProvider.AUTHORITY, bundle);
        Log.d("SunshineSyncAdapter", "syncImmediately: ");
        System.out.println("xxxx syncImmediately");
    }

    public static void initializeSyncAdapter(Context context) {
        System.out.println("xxxx initialize syncadapter....");
        getSyncAccount(context);
    }
}
