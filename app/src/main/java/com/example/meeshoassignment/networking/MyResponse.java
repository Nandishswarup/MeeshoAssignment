package com.example.meeshoassignment.networking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyResponse<T> implements Serializable {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;


    MyResponse()
    {

        this.status = null;
        this.data = null;
        this.message = null;
     }

    private MyResponse(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> MyResponse<T> success(@NonNull T data) {
        return new MyResponse<>(Status.SUCCESS, data, null);
    }

    public static <T> MyResponse<T> error(String msg, @Nullable T data ) {
        return new MyResponse<>(Status.ERROR, data, msg);
    }

    public static <T> MyResponse<T> loading(@Nullable T data ) {
        return new MyResponse<>(Status.LOADING, data, null);
    }


    public enum Status { SUCCESS, ERROR, LOADING }
}