package com.bgabi.travelit.auth

import com.bgabi.travelit.MainActivity
import com.bgabi.travelit.R

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        val btn_to_sign_in = findViewById<Button>(R.id.tv_to_sign_in)
        btn_to_sign_in.setOnClickListener {
            val intent : Intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        val et_register_email = findViewById<TextView>(R.id.et_register_email)
        val et_register_password = findViewById<TextView>(R.id.et_register_password)
        val et_confirm_password = findViewById<TextView>(R.id.et_confirm_password)
        //val et_register_name = findViewById<TextView>(R.id.et_register_name)
        //val et_register_phone = findViewById<TextView>(R.id.et_register_phone)

        val btn_register = findViewById<Button>(R.id.btn_register)
        btn_register.setOnClickListener {
            when {
                TextUtils.isEmpty(et_register_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter your email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                TextUtils.isEmpty(et_register_name.text.toString().trim { it <= ' ' }) -> {
//                    Toast.makeText(
//                        this@SignUpActivity,
//                        "Please enter your name.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
                TextUtils.isEmpty(et_register_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = et_register_email.text.toString().trim { it <= ' ' }
                    //val name: String = et_register_name.text.toString().trim { it <= ' ' }
                    //val phone: String = et_register_phone.text.toString().trim { it <= ' ' }
                    val password: String = et_register_password.text.toString().trim { it <= ' ' }
                    val confirmPassword: String =
                        et_confirm_password.text.toString().trim { it <= ' ' }

                    if (password == confirmPassword) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                OnCompleteListener<AuthResult> { task ->
                                    if (task.isSuccessful) {
                                        val firebaseUser: FirebaseUser = task.result!!.user!!

                                        //addDataToFirebase(email, name, phone, firebaseUser.uid)

                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Your account was successfully created!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(this@SignUpActivity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", firebaseUser.uid)
                                        intent.putExtra("user_email", email)
                                        //intent.putExtra("user_name", name)
                                        //intent.putExtra("user_phone", phone)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            task.exception!!.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Password is not matching",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

}
