package com.example.sample

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var imgProfileUpload: ShapeableImageView

    // This opens the Android photo gallery
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imgProfileUpload.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        dbHelper = DatabaseHelper(this)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)
        imgProfileUpload = findViewById(R.id.imgProfileUpload)

        // Open gallery when picture is tapped
        imgProfileUpload.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val imageUriString = selectedImageUri?.toString() ?: ""

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Save to SQLite Database
                val isInserted = dbHelper.registerUser(email, password, name, imageUriString)

                if (isInserted) {
                    Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}