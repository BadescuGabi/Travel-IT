package com.bgabi.travelit.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bgabi.travelit.MainActivity
import com.bgabi.travelit.activities.HomeActivity
import com.bgabi.travelit.databinding.ActivitySignUpBinding
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var redirect_sign_in: TextView
    private lateinit var btn_register: Button
    private lateinit var register_email: TextView
    private lateinit var user_name: TextView
    private lateinit var register_password: TextView
    private lateinit var confirm_password: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private final var dbUrl: String =
        "https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()

        redirect_sign_in = binding.redirectSignIn
        redirect_sign_in.setOnClickListener {
            val intent: Intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        btn_register = binding.btnRegister
        btn_register.setOnClickListener {
            when {
                TextUtils.isEmpty(register_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter your email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(user_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter username.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(register_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = register_email.text.toString().trim { it <= ' ' }
                    val userName: String = user_name.text.toString().trim { it <= ' ' }
                    val password: String = register_password.text.toString().trim { it <= ' ' }
                    val confirmPassword: String =
                        confirm_password.text.toString().trim { it <= ' ' }

                    if (password == confirmPassword) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                OnCompleteListener<AuthResult> { task ->
                                    if (task.isSuccessful) {
                                        val firebaseUser: FirebaseUser = task.result!!.user!!
                                        FirebaseHelper.addUserToFirebase(firebaseUser.uid, email, userName)
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Your account was successfully created!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(this@SignUpActivity, HomeActivity::class.java)
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



    private fun initialize() {
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance(dbUrl).getReference("data/users")
        redirect_sign_in = binding.redirectSignIn
        btn_register = binding.btnRegister
        register_email = binding.etRegisterEmail
        user_name = binding.etUsername
        register_password = binding.etRegisterPassword
        confirm_password = binding.etConfirmPassword
    }

}
