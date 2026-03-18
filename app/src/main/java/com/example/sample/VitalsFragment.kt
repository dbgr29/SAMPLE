package com.example.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import kotlin.math.pow

class VitalsFragment : Fragment() {
    // ... (onCreateView remains the same)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ... (dropdown setup remains exactly the same)

        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveVitals)
        btnSave.setOnClickListener {
            val dropdownHeight = view.findViewById<AutoCompleteTextView>(R.id.dropdownHeight)
            val dropdownWeight = view.findViewById<AutoCompleteTextView>(R.id.dropdownWeight)

            val heightCm = dropdownHeight.text.toString().toDoubleOrNull() ?: 0.0
            val weightKg = dropdownWeight.text.toString().toDoubleOrNull() ?: 0.0

            // ERD Mapping: The ERD only has BMI. So we calculate it here.
            var bmi = 0.0
            if (heightCm > 0) {
                val heightMeters = heightCm / 100
                bmi = weightKg / (heightMeters.pow(2))
            }

            val userId = requireActivity().intent.getLongExtra("USER_ID", -1L)
            val dbHelper = DatabaseHelper(requireContext())

            if (userId != -1L) {
                // Save ONLY the BMI to the ERD's Health Profile
                val isSaved = dbHelper.updateVitalsToERD(userId, bmi)

                if (isSaved) {
                    Toast.makeText(requireContext(), "Vitals Saved to Profile!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to save vitals.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // ... (navigation logic remains the same)
    }
}