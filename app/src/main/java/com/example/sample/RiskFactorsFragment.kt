package com.example.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RiskFactorsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risk_factors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // 1. Set the text for all 10 questions dynamically
        setupQuestion(view, R.id.qHypertension, "1. Do you have a history of Hypertension (High Blood Pressure)?")
        setupQuestion(view, R.id.qDiabetes, "2. Do you have a history of Diabetes Mellitus?")
        setupQuestion(view, R.id.qSmoking, "3. Do you currently smoke or have a history of smoking?")
        setupQuestion(view, R.id.qDyslipidemia, "4. Do you have Dyslipidemia (High Cholesterol)?")
        setupQuestion(view, R.id.qObesity, "5. Have you been diagnosed with Obesity?")
        setupQuestion(view, R.id.qPriorStroke, "6. Have you ever had a Prior Stroke or TIA (Mini-stroke)?")
        setupQuestion(view, R.id.qAtrialFib, "7. Do you have a history of Atrial Fibrillation (Irregular Heartbeat)?")
        setupQuestion(view, R.id.qSedentary, "8. Would you describe your lifestyle as Sedentary (Lack of physical activity)?")
        setupQuestion(view, R.id.qAlcohol, "9. Do you engage in regular or heavy Alcohol Consumption?")
        setupQuestion(view, R.id.qKidneyDisease, "10. Do you have Chronic Kidney Disease?")

        val btnSubmit = view.findViewById<MaterialButton>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            submitForm(view)
        }

        // --- Bottom Navigation Setup ---
        view.findViewById<FloatingActionButton>(R.id.btnCamera)?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_scan)
        }
        view.findViewById<ImageView>(R.id.btnHome)?.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    private fun setupQuestion(parentView: View, includeId: Int, questionText: String) {
        val questionLayout = parentView.findViewById<View>(includeId)
        val tvQuestion = questionLayout.findViewById<TextView>(R.id.tvQuestion)
        tvQuestion.text = questionText
    }

    // returns an Integer (1, 0, or -1 for unanswered)
    private fun getAnswerAsInt(parentView: View, includeId: Int): Int {
        val questionLayout = parentView.findViewById<View>(includeId)
        val radioGroup = questionLayout.findViewById<RadioGroup>(R.id.radioGroup)

        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId == -1) return -1 // -1 means they skipped it

        val radioButton = questionLayout.findViewById<RadioButton>(selectedId)
        return if (radioButton.text.toString() == "Yes") 1 else 0
    }

    private fun submitForm(view: View) {
        // Gather all answers as 1s and 0s
        val answers = mapOf(
            "hypertension" to getAnswerAsInt(view, R.id.qHypertension),
            "diabetes" to getAnswerAsInt(view, R.id.qDiabetes),
            "smoking" to getAnswerAsInt(view, R.id.qSmoking),
            "dyslipidemia" to getAnswerAsInt(view, R.id.qDyslipidemia),
            "obesity" to getAnswerAsInt(view, R.id.qObesity),
            "prior_stroke" to getAnswerAsInt(view, R.id.qPriorStroke),
            "atrial_fibrillation" to getAnswerAsInt(view, R.id.qAtrialFib),
            "sedentary" to getAnswerAsInt(view, R.id.qSedentary),
            "alcohol" to getAnswerAsInt(view, R.id.qAlcohol),
            "ckd" to getAnswerAsInt(view, R.id.qKidneyDisease)
        )

        // Prevent submission if ANY question was skipped (value is -1)
        if (answers.containsValue(-1)) {
            Toast.makeText(requireContext(), "Please answer all questions before submitting.", Toast.LENGTH_LONG).show()
            return
        }

        val stringAnswers = answers.mapValues { it.value.toString() }


        // Save Locally in SQLite
        val isSavedLocally = dbHelper.insertRiskFactors(answers)

        if (isSavedLocally) {

            // use Retrofit to send the data to Python,
            // send the original integer 'answers' map, NOT the string map!

            Toast.makeText(requireContext(), "Saved locally! Syncing securely to doctor's database...", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Database Error. Could not save.", Toast.LENGTH_SHORT).show()
        }
    }
}