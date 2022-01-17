package com.example.meeshoassignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.interviewassignment.networking.ApiManagger
import com.example.meeshoassignment.model.SessionDetailsModel
import com.example.meeshoassignment.model.SubmitSessionModel
import com.example.meeshoassignment.networking.MyResponse
import kotlin.math.roundToInt

class SubmitSessionViewModel : ViewModel() {
    var submitSessionModel = MutableLiveData<MyResponse<SubmitSessionModel>>()

    fun getSessionLiveData(): LiveData<MyResponse<SubmitSessionModel>> {
        return submitSessionModel
    }
    fun SendToServer(sessionDetailsModel: SessionDetailsModel, timeSpent: String, endTime: String)
    {
       var timeInMin=calculateTimeInMin(timeSpent)
        var session:SubmitSessionModel=prepareData(sessionDetailsModel,timeInMin,endTime)
        submitSessionModel= ApiManagger.submitSession(session)
    }


    private fun prepareData(sessionDetailsModel: SessionDetailsModel,timeSpent: Int, endTime: String): SubmitSessionModel {

        var submitSessionModel=SubmitSessionModel(sessionDetailsModel.location_id,timeSpent,endTime.toLong())
        return submitSessionModel


    }
    fun calculateTimeInMin(timeSpent: String):Int
    {
        var splittime=timeSpent.split(":")
        var hour=splittime[0]
        var minute=splittime[1]
        var second=splittime[2]
        return ((hour.toInt()*60)+minute.toInt()+(Math.ceil(second.toDouble()/60))).toInt()
    }


     init {
         submitSessionModel = MutableLiveData<MyResponse<SubmitSessionModel>>()
     }


}