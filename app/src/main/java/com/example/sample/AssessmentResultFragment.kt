package com.example.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class AssessmentResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_assessment_result, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val riskPercentage = arguments?.getInt("RISK_PERCENTAGE") ?: 0

        view.findViewById<TextView>(R.id.tvRiskPercentage).text = "$riskPercentage%"

        val tvYoloStatus = view.findViewById<TextView>(R.id.tvYoloStatus)
        tvYoloStatus.text = "Facial analysis data is currently being integrated. Please ensure you have completed a recent face scan."

        view.findViewById<MaterialButton>(R.id.btnReturnHome).setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}