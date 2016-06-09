package com.annasblackhat.developer.contentprovider.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Sasha Grey on 6/8/2016.
 */

public class WalletAuthenticatorService extends Service {

    private WalletAuthenticator walletAuthenticator;

    @Override
    public void onCreate() {
        walletAuthenticator = new WalletAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return walletAuthenticator.getIBinder();
    }

}
