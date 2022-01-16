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
    public final String message;


    MyResponse()
    {

        this.status = null;
         this.message = null;
     }

    public MyResponse(@NonNull Status status, @Nullable String message) {
        this.status = status;
         this.message = message;
    }

    public static <T> MyResponse<T> success() {
        return new MyResponse<>(Status.SUCCESS, null);
    }

    public static <T> MyResponse<T> error(String msg) {
        return new MyResponse<>(Status.ERROR, msg);
    }

    public static <T> MyResponse<T> loading() {
        return new MyResponse<>(Status.LOADING, null);
    }


    public enum Status { SUCCESS, ERROR, LOADING }
}