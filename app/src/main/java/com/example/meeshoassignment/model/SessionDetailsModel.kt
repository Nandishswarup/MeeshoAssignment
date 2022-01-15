package com.example.meeshoassignment.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SessionDetailsModel(
    @JsonProperty("location_id")var location_id:String,
    @JsonProperty("location_details")var location_details:String,
    @JsonProperty("price_per_min")var price_per_min:Float
):Serializable
