package com.example.interviewassignment.networking

 import com.example.meeshoassignment.model.SubmitSessionModel
 import com.example.meeshoassignment.networking.MyResponse
import retrofit2.Response
 import retrofit2.http.Body
 import retrofit2.http.GET
 import retrofit2.http.POST
 import retrofit2.http.Path

interface ApiInterace {

 @POST("submit-session")
 suspend fun submitSession( @Body sessionModel: SubmitSessionModel?): Response<MyResponse<SubmitSessionModel?>?>


}