package com.wqd.app;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by wqd on 16-12-3.
 */

public interface CommonApiService {
    @POST()
    <T> Observable<BaseResponse> post(
            @Url String url,
            @QueryMap Map<String, T> maps
    );

    @GET()
    <T> Observable<BaseResponse> get(
            @Url String url,
            @QueryMap Map<String, T> maps
    );





}
