package com.annasblackhat.developer.contentprovider.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Sasha Grey on 6/8/2016.
 */

public class WalletSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static WalletSyncAdapter walletSyncAdapter = null;

    @Override
    public void onCreate() {
        System.out.println("xxxx oncreate walletsyncservice...");
        synchronized (sSyncAdapterLock){
            if(walletSyncAdapter == null){
                walletSyncAdapter = new WalletSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("xxxx onbind service....");
        return walletSyncAdapter.getSyncAdapterBinder();
    }
}
