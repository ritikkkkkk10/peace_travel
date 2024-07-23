package com.ritikprajapati.peacetravel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var email: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var login: Button? = null
    private var auth: FirebaseAuth? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.text_email)
        password = findViewById(R.id.text_password)
        login = findViewById(R.id.button_login)
        auth = FirebaseAuth.getInstance()

        val loginButton = login
        val txt_email = email
        val txt_password = password

        loginButton?.setOnClickListener{
            val emailText = txt_email?.text.toString()
            val passwordText = txt_password?.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                hideKeyboard()
                val view = currentFocus ?: findViewById<View>(android.R.id.content)
                Snackbar.make(view, "Please enter both email and password", Snackbar.LENGTH_LONG).show()
            } else {
                hideKeyboard()
                loginUser(emailText, passwordText)
            }
        }

        findViewById<Button>(R.id.button_forgot_password).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun loginUser(Email: String, Password: String) {
        auth!!.signInWithEmailAndPassword(Email, Password)
            ?.addOnSuccessListener { authResult ->

                saveUidToSharedPreferences(authResult.user!!.uid)

                val view = currentFocus ?: findViewById<View>(android.R.id.content)
                Snackbar.make(
                    view,
                    "Login successful!",
                    Snackbar.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { exception ->
                val view = currentFocus ?: findViewById<View>(android.R.id.content)
                // Check if the failure is due to the user not being registered
                if (exception.message?.contains("no user record") == true) {
                    Snackbar.make(
                        view,
                        "No account found with this email. Please register first.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    // Handle other possible errors (e.g., wrong password)
                    Snackbar.make(
                        view,
                        "Login failed: ${exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }



            }
    }
    private fun saveUidToSharedPreferences(uid: String) {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("UID", uid)
        editor.apply()
    }

}