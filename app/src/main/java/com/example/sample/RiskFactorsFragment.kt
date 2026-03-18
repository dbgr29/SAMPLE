// ... (imports remain the same)
private fun submitForm(view: View) {
    val ageText = view.findViewById<EditText>(R.id.etAge).text.toString()
    val glucoseText = view.findViewById<EditText>(R.id.etGlucose).text.toString()
    val bmiText = view.findViewById<EditText>(R.id.etBmi).text.toString()

    val hypertensionStr = getRadioString(view, R.id.rgHypertension)
    val heartDiseaseStr = getRadioString(view, R.id.rgHeart)
    val everMarried = getRadioString(view, R.id.rgMarried)
    val residence = getRadioString(view, R.id.rgResidence)
    val smokingStatus = view.findViewById<Spinner>(R.id.spinSmoking).selectedItem.toString()

    if (ageText.isEmpty() || glucoseText.isEmpty() || bmiText.isEmpty() ||
        hypertensionStr == null || heartDiseaseStr == null || everMarried == null || residence == null) {
        Toast.makeText(requireContext(), "Please answer all questions before submitting.", Toast.LENGTH_LONG).show()
        return
    }

    // 1. Pack data exactly as Kaggle/Python expects
    val answers = mapOf<String, Any>(
        "gender" to view.findViewById<Spinner>(R.id.spinGender).selectedItem.toString(),
        "age" to ageText.toDouble(),
        "hypertension" to getRadioInt(view, R.id.rgHypertension),
        "heart_disease" to getRadioInt(view, R.id.rgHeart),
        "ever_married" to everMarried,
        "work_type" to view.findViewById<Spinner>(R.id.spinWork).selectedItem.toString(),
        "Residence_type" to residence,
        "avg_glucose_level" to glucoseText.toDouble(),
        "bmi" to bmiText.toDouble(),
        "smoking_status" to smokingStatus
    )

    val userId = requireActivity().intent.getLongExtra("USER_ID", -1L)

    if (userId != -1L) {
        // 2. Map data to the ERD HealthRiskFactorProfile table constraints
        val isSmoker = if (smokingStatus.contains("smokes", ignoreCase = true)) 1 else 0
        val isDiabetic = if (glucoseText.toDouble() >= 126.0) 1 else 0 // Basic medical mapping

        val isSaved = dbHelper.updateRiskFactorsToERD(
            userId = userId,
            age = ageText.toInt(), // ERD stores age in User table
            hypertension = getRadioInt(view, R.id.rgHypertension),
            cardiacDisease = getRadioInt(view, R.id.rgHeart),
            bmi = bmiText.toDouble(),
            smoker = isSmoker,
            diabetes = isDiabetic
        )

        if (isSaved) {
            syncToDoctorDatabase(answers) // Send full Kaggle data to Python server
        } else {
            Toast.makeText(requireContext(), "Database Error.", Toast.LENGTH_SHORT).show()
        }
    }
}
// ... (syncToDoctorDatabase remains the same)