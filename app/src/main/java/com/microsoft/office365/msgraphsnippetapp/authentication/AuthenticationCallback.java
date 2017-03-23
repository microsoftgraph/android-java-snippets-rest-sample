package com.microsoft.office365.msgraphsnippetapp.authentication;

public interface AuthenticationCallback<T, V> {
    void onSuccess(T data1, V data2);
    void onError(Exception e);
}
