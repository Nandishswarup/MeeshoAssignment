package com.example.interviewassignment.networking

import android.util.Log
import androidx.lifecycle.MutableLiveData
 import com.example.meeshoassignment.networking.MyResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiManagger {
    companion object {
        var API_FAILURE="API_FAILURE"
        var LOW_INTERNET_CONNECTION="LOW_INTERNET_CONNECTION"



    }

}


