package com.annasblackhat.developer.contentprovider.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sasha Grey on 5/27/2016.
 */

public class WalletContract implements BaseColumns {
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + WalletContentProvider.AUTHORITY + "/wallet");

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jwei512.notes";

    public static final String WALLET_ID = "_id";

    public static final String TITLE = "title";

    public static final String TOTAL = "total";
}
