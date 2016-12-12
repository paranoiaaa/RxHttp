package com.wqd.app;

/**
 * Created by wqd on 16-12-3.
 */

public interface ResponseCallBack<T> {
    public void onStart();
    public void onCompleated();
    public abstract void onError(Throwable e);
    public abstract void onSucess(T response);
}
