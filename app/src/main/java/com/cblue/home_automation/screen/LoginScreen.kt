package com.cblue.home_automation.screen

import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.edit
import com.cblue.home_automation.R
import com.cblue.home_automation.databinding.LoginActivityBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.progressindicator.CircularProgressIndicator

class LoginScreen : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInButton: Button

    private val REQ_ONE_TAP = 100  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private val PREFS_NAME = "auth_prefs"
    private val KEY_ID_TOKEN = "id_token"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInButton = binding.buttonGoogleSignIn

        oneTapClient = Identity.getSignInClient(this)

        if (isTokenCached()) return

        initialiseSignInRequest()

        googleSignInButton.setOnClickListener {
            showGoogleLoading(true)
            googleSignIn()
        }

    }

    private fun isTokenCached(): Boolean {
        val cachedToken = getToken()
        if (cachedToken != null) {
            if (isNetworkAvailable()) {
                navigateToHome(cachedToken)
                return true
            } else {
                forceLogout()
                return true
            }
        }
        return false
    }

    private fun initialiseSignInRequest() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("247378962438-o3ldmr4hj5km4b6kae9aj2e69qdp286g.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            // ✅ SAVE TOKEN
                            saveToken(idToken)

                            navigateToHome(idToken)


                            // move to next screen
                            val intent = Intent(this, DeviceListScreen::class.java)
                            intent.putExtra("ID_TOKEN", idToken)
                            startActivity(intent)
                            finish()  // prevents returning back to login

                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("TAG", "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("TAG", "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d("TAG", "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHome(token: String) {
        val intent = Intent(this, DeviceListScreen::class.java)
        intent.putExtra("ID_TOKEN", token)
        startActivity(intent)
        finish()
    }

    private fun forceLogout() {
        clearToken()
        oneTapClient.signOut()
        // Stay on login screen
    }

    private fun googleSignIn(){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                showGoogleLoading(false)
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    showGoogleLoading(false)
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { _ ->
                showGoogleLoading(false)
                Toast.makeText(
                    this,
                    "No Google account found. Please add one to continue.",
                    Toast.LENGTH_LONG
                ).show()
                openAddGoogleAccount()
            }
    }

    private fun showGoogleLoading(isLoading: Boolean) {
        val button = binding.buttonGoogleSignIn

        if (isLoading) {
            button.isEnabled = false
            button.text = "Signing in..."
            button.icon = null

            val progress = CircularProgressIndicator(this).apply {
                isIndeterminate = true
                setIndicatorSize(48)
            }

            button.icon = progress.indeterminateDrawable
        } else {
            button.isEnabled = true
            button.text = "Sign in with Google"
            button.icon = AppCompatResources.getDrawable(this, R.drawable.ic_google_logo)
        }
    }

    private fun openAddGoogleAccount() {
        try {
            startActivity(Intent(android.provider.Settings.ACTION_ADD_ACCOUNT))
        } catch (e: Exception) {
            Log.e("TAG", "Unable to open account settings", e)
        }
    }

    private fun saveToken(token: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit {
                putString(KEY_ID_TOKEN, token)
            }
    }

    private fun getToken(): String? {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_ID_TOKEN, null)
    }

    private fun clearToken() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit {
                clear()
            }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}