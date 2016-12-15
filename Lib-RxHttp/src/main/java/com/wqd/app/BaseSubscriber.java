package com.wqd.app;

import android.app.Activity;
import android.content.Context;

import rx.Subscriber;

/**
 * Created by wqd on 16-12-2.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {
    public Context context;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ExceptionHandle.ResponeThrowable){
            onError((ExceptionHandle.ResponeThrowable)e);
        }else {
            onError(new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN));
        }

    }

    @Override
    public void onNext(T o) {

    }
}
