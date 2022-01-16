package com.example.interviewassignment.networking

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meeshoassignment.model.SubmitSessionModel
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


        fun submitSession(session:SubmitSessionModel): MutableLiveData<MyResponse<SubmitSessionModel>> {

            var sessionLiveData: MutableLiveData<MyResponse<SubmitSessionModel>>  = MutableLiveData<MyResponse<SubmitSessionModel>> ()

            CoroutineScope(Dispatchers.Default).launch {

                launch(Dispatchers.IO) {
                    val apiInterface = ApiInterfaceBuilder.getApiInterface()
                    var response: Response<MyResponse<SubmitSessionModel?>?>?=null
                    try {
                        response = apiInterface?.submitSession(session)
                    }catch (e:Exception)
                    {
                        withContext(Dispatchers.Default)
                        {
                            if (e is SocketTimeoutException || e is UnknownHostException || e is ConnectException || e is SocketException) {
                                sessionLiveData.postValue(
                                    MyResponse.error(
                                        LOW_INTERNET_CONNECTION
                                    )
                                )
                            } else {
                                sessionLiveData.postValue(
                                    MyResponse.error(
                                        API_FAILURE
                                    )
                                )
                            }
                        }

                    }



                    withContext(Dispatchers.Default)
                    {
                        response?.let {
                            if (response.isSuccessful()) {

                                sessionLiveData.postValue(MyResponse.success())
                                Log.e("checkpoint", "onresponse")

                            } else {
                                sessionLiveData.postValue(MyResponse.error(response.message()))

                            }

                        }
                    }
                }

            }
            return sessionLiveData
        }


    }

}


