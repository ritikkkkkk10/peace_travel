package com.ritikprajapati.peacetravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var resetPasswordButton: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        email = findViewById(R.id.text_email)
        resetPasswordButton = findViewById(R.id.button_reset_password)
        auth = FirebaseAuth.getInstance()

        resetPasswordButton.setOnClickListener {
            val emailText = email.text.toString()
            if (emailText.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Please enter your email", Snackbar.LENGTH_LONG).show()
            } else {
                sendPasswordResetEmail(emailText)
            }
        }
    }
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(findViewById(android.R.id.content), "Password reset email sent!", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Error: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}