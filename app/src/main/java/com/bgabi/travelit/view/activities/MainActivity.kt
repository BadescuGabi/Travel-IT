package com.bgabi.travelit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.view.activities.SavedPreference
import com.bgabi.travelit.view.activities.SignInActivity
import com.bgabi.travelit.view.activities.SignUpActivity
import com.bgabi.travelit.databinding.ActivityMainBinding
import com.bgabi.travelit.databinding.ActivityHomeBinding
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val tag = "Login"
    private lateinit var btn_facebook: ImageView
    private lateinit var btn_google: ImageView
    var condition = false
    val Req_Code: Int = 123
    private lateinit var binding: ActivityMainBinding
    private lateinit var  binding1: ActivityHomeBinding


//    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
//        val user = firebaseAuth.currentUser?.uid
//        user?.let {
////            val intent = Intent(this, MainActivity::class.java)
////            startActivity(intent)
////            finish()
//            //this.recreate()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TravelIT2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        firebaseAuth = Firebase.auth
        firebaseUser = firebaseAuth.currentUser

        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)

        val button_sign_in: Button = binding.buttonSignUp
        button_sign_in.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
        }

        val button_sign_up: Button = binding.buttonSignIn
        button_sign_up.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btn_google = binding.signInGmailButton
        btn_google.setOnClickListener { view: View? ->
            signInGoogle()
        }

        callbackManager = CallbackManager.Factory.create()

        btn_facebook = binding.signInFacebookButton
        btn_facebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("TAG", "Success Login")
                handleFacebookAccessToken(result.accessToken)
                firebaseAuth = Firebase.auth
                firebaseUser = firebaseAuth.currentUser

                GlobalScope.launch {
                    delay(500)
                    if (firebaseUser != null) {
                        condition = true
                    }
                }

                val result = runBlocking {
                    println("waiting for result started")
                    waitForCondition(1000, 100)
                }

                goToHomeActivity()
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@MainActivity, "bla bla fb fail", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun signInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SavedPreference.setEmail(this, account.toString())
                SavedPreference.setUsername(this, account.displayName.toString())
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    tailrec suspend fun waitForCondition(maxDelay: Long, checkPeriod: Long): Boolean {
        if (maxDelay < 0) return false
        if (condition) return true
        delay(checkPeriod)
        return waitForCondition(maxDelay - checkPeriod, checkPeriod)
    }

    private fun getFacebookData(jsonObject: JSONObject?) {
        val profilePic = "https://graph.facebook.com/${jsonObject
            ?.getString("id")}/picture?width=500&height=500"
        Glide.with(this)
            .load(profilePic)
            .into(findViewById(R.id.profile_pic))

//        val name = jsonObject?.getString("name")
//        val birthday = jsonObject?.getString("birthday")
//        val gender = jsonObject?.getString("gender")
//        val email = jsonObject?.getString("email")
//
//        binding.userName.text = "Name: ${name}"
//        binding.userEmail.text = "Email: ${email}"
//        binding.userBDay.text = "Birthday: ${birthday}"
//        binding.userGender.text = "Gender: ${gender}"
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(
                        this@MainActivity,
                        "You successfully signed in!",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToHomeActivity()
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(tag, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
//        var request : GraphRequest? = null
//        request = GraphRequest.newMeRequest(
//            token,
//            object : GraphRequest.GraphJSONObjectCallback {
//                override fun onCompleted(
//                    obj: JSONObject?,
//                    response: GraphResponse?
//                ) {
//                    getFacebookData(obj)
//                }
//            })
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        user.email?.let { FirebaseHelper.addUserToFirebase(user.uid, it,it) }
                    }
                } else {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(
                            baseContext, "Authentication failed---------.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

object SavedPreference {

    const val EMAIL = "email"
    const val USERNAME = "username"

    private fun getSharedPreference(ctx: Context?): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    private fun editor(context: Context, const: String, string: String) {
        getSharedPreference(
            context
        )?.edit()?.putString(const, string)?.apply()
    }

    fun getEmail(context: Context) = getSharedPreference(
        context
    )?.getString(EMAIL, "")

    fun setEmail(context: Context, email: String) {
        editor(
            context,
            EMAIL,
            email
        )
    }

    fun setUsername(context: Context, username: String) {
        editor(
            context,
            USERNAME,
            username
        )
    }

    fun getUsername(context: Context) = getSharedPreference(
        context
    )?.getString(USERNAME, "")

}



