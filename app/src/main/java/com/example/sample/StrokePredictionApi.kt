package com.example.sample

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StrokePredictionApi {

    @POST("predict_risk")
    fun getRiskPrediction(@Body patientData: PatientDataPayload): Call<PredictionResponse>

}