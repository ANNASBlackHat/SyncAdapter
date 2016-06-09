package com.annasblackhat.developer.contentprovider;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sasha Grey on 6/9/2016.
 */

public interface WalletApi {
    @GET("getdata.php")
    Call<List<Wallet>> getAllData();
}
