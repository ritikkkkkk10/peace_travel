package com.ritikprajapati.peacetravel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    var firstName: TextInputEditText? = null
    var lastName: TextInputEditText? = null
    var email: TextInputEditText? = null
    var phone: TextInputEditText? = null
    var password: TextInputEditText? = null
    var register: Button? = null
    private var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firstName = findViewById(R.id.edit_text_first_name)
        lastName = findViewById(R.id.edit_text_last_name)
        email = findViewById(R.id.edit_text_email)
        phone = findViewById(R.id.edit_text_phone)
        password = findViewById(R.id.edit_text_password)
        register = findViewById(R.id.button_create_account)
        auth = FirebaseAuth.getInstance()

        val registerButton = register
        val txt_email = email
        val txt_password = password
        val txt_phone = phone
        registerButton?.setOnClickListener(View.OnClickListener {
            val emailText = txt_email?.text.toString()
            val passwordText = txt_password?.text.toString()
            val phoneText = txt_phone?.text.toString();
            if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(phoneText))
                Snackbar.make(this@RegisterActivity.currentFocus!!, "Missing Credentials!", Snackbar.LENGTH_SHORT).show()
            else if (passwordText.length < 6) {
                Snackbar.make(this@RegisterActivity.currentFocus!!, "Password too short!", Snackbar.LENGTH_SHORT).show()
            } else {
                registerUser( passwordText, phoneText, emailText)
            }
        })
    }

    private fun registerUser(txtPassword: String, txtPhone: String, txtEmail: String) {
        auth!!.createUserWithEmailAndPassword(txtEmail, txtPassword)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth!!.currentUser
                    val userUid = currentUser?.uid

                    // Store user details along with UID in your database
                    // For example, you can store them in Firebase Realtime Database or Firestore
                    // Here, we'll assume you're using Firebase Realtime Database
                    userUid?.let { uid ->

                        saveUidToSharedPreferences(uid)

                        val userData = mapOf(
                            "firstName" to firstName!!.text.toString(),
                            "lastName" to lastName!!.text.toString(),
                            "email" to txtEmail,
                            "phone" to txtPhone
                        )
                        FirebaseDatabase.getInstance().getReference("newUsers")
                            .child(uid)
                            .setValue(userData)
                            .addOnSuccessListener {
                    Snackbar.make(
                        this@RegisterActivity.currentFocus!!,
                        "Registering user successful!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
                            .addOnFailureListener { exception ->
                                Snackbar.make(
                                    this@RegisterActivity.currentFocus!!,
                                    "Registration failed!: ${exception.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                        else {
                    Snackbar.make(
                        this@RegisterActivity.currentFocus!!,
                        "Registration failed!: ${task.exception?.message}",
                        Snackbar.LENGTH_SHORT
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
