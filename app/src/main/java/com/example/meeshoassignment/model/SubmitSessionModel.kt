package com.example.meeshoassignment.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmitSessionModel(
@JsonProperty("location_id")var location_id:String,
@JsonProperty("time_spent")var time_spent:Int,
@JsonProperty("end_time")var end_time:Long
):Serializable
