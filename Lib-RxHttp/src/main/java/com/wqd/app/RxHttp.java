package com.wqd.app;

import android.content.Context;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wqd on 16-12-3.
 */

public final class RxHttp {
    public static CommonApiService apiService;
    private static Retrofit.Builder retrofitBuilder;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder okhttpBuilder;
    private static OkHttpClient okHttpClient;
    private static HttpLoggingInterceptor mInterceptor;
    private final String baseUrl ;

    RxHttp(String baseUrl, CommonApiService apiService) {
        this.baseUrl=baseUrl;
        this.apiService=apiService;

    }

    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    /**
     * @param subscriber
     */
    public <T> T call(Observable<T> observable, Subscriber<T> subscriber) {
        observable.compose(schedulersTransformer)
                .subscribe(subscriber);
        return null;
    }

    final Observable.Transformer schedulersTransformer = new  Observable.Transformer() {
        @Override public Object call(Object observable) {
            return ((Observable)  observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };


    public static final class Builder{
        private Context mContext;
        private String baseUrl;
        private Boolean isLog = false;
        private static final int DEFAULT_TIMEOUT = 5;
        private CallAdapter.Factory callAdapterFactory;
        private Converter.Factory converterFactory;


        public Builder(Context context) {
            this.mContext=context;
            retrofitBuilder=new Retrofit.Builder();
            okhttpBuilder=new OkHttpClient.Builder();

        }

        public Builder client(OkHttpClient client){
            if (client!=null){
                retrofitBuilder.client(client);
            }
            return this;
        }

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        public Builder baseUrl(String baseUrl){
            if (!TextUtils.isEmpty(baseUrl)){
                this.baseUrl=baseUrl;
            }
            return this;
        }

        public Builder addNetworkInterceptor (Interceptor interceptor){
            okhttpBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        public Builder addInterceptor (Interceptor interceptor){
            okhttpBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        public Builder addConverterFactory(GsonConverterFactory factory){
            this.converterFactory =factory;
            return this;
        }

        public Builder connectTimeout (int timeout, TimeUnit unit){
            if (timeout!=-1){
                okhttpBuilder.connectTimeout(timeout,unit);
            }else {
                okhttpBuilder.connectTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder writeTimeout(int timeout,TimeUnit unit){
            if (timeout!=-1){
                okhttpBuilder.writeTimeout(timeout,unit);
            }else {
                okhttpBuilder.writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder readTimeout(int timeout,TimeUnit unit){
            if (timeout!=-1){
                okhttpBuilder.readTimeout(timeout,unit);
            }else {
                okhttpBuilder.readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
            }
            return this;
        }

        public RxHttp build(){
            if (baseUrl==null){
                throw new IllegalStateException("Base Url is Null");
            }
            retrofitBuilder.baseUrl(baseUrl);

            if (okhttpBuilder == null) {
                throw new IllegalStateException("okhttpBuilder required.");
            }

            if (retrofitBuilder==null){
                throw new IllegalStateException("retrofitBuilder  is Null");
            }

            if (converterFactory==null){
                converterFactory= GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);

            if (callAdapterFactory==null){
                callAdapterFactory=RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (isLog){
                okhttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            okhttpBuilder.writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
            okhttpBuilder.readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
            okhttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            okHttpClient=okhttpBuilder.build();
            retrofitBuilder.client(okHttpClient);

            retrofit=retrofitBuilder.build();

            apiService=retrofit.create(CommonApiService.class);

            return new RxHttp(baseUrl,apiService);

        }
    }
}
