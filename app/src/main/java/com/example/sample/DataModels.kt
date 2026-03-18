package com.example.sample

import com.google.gson.annotations.SerializedName

// This maps your Kotlin data into the
// exact JSON format expected by your Python Pandas DataFrame
data class PatientDataPayload(
    val age: Int,
    val gender: String, // e.g., "Male" or "Female"
    val hypertension: Int, // 1 or 0

    @SerializedName("heart_disease")
    val heartDisease: Int, // Maps to your SQLite cardiac_disease (1 or 0)

    val bmi: Double,

    @SerializedName("smoking_status")
    val smokingStatus: String // e.g., "smokes", "never smoked"
)

// This unpacks the
// {"success": True, "risk_score": 0.75}
// JSON response from app.py
data class PredictionResponse(
    val success: Boolean,

    @SerializedName("risk_score")
    val riskScore: Double?,

    val error: String?
)