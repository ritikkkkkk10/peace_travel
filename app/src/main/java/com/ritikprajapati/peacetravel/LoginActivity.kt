package com.ritikprajapati.peacetravel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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

        loginButton?.setOnClickListener(View.OnClickListener {
            val emailText = txt_email?.text.toString()
            val passwordText = txt_password?.text.toString()
            loginUser(emailText, passwordText)
        })
    }

    private fun loginUser(Email: String, Password: String) {
        auth!!.signInWithEmailAndPassword(Email.toString(), Password.toString())
            ?.addOnSuccessListener { authResult ->

                saveUidToSharedPreferences(authResult.user!!.uid)

                Snackbar.make(
                    this@LoginActivity.currentFocus!!,
                    "Login successful!",
                    Snackbar.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { exception ->
                // Check if the failure is due to the user not being registered
                if (exception.message?.contains("no user record") == true) {
                    Snackbar.make(
                        this@LoginActivity.currentFocus!!,
                        "No account found with this email. Please register first.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    // Handle other possible errors (e.g., wrong password)
                    Snackbar.make(
                        this@LoginActivity.currentFocus!!,
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