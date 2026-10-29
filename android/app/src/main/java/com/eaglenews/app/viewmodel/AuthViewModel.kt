package com.eaglenews.app.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()
    var registrationComplete by mutableStateOf(false)
        private set

    private val googleClient: GoogleSignInClient

    private var registering = false
        set(value) {
            field = value
        }
    private var pendingName = ""
    private var pendingEmail = ""

    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set

    var alertMessage by mutableStateOf("")
        private set

    init {
        auth.addAuthStateListener {
            _user.value = it.currentUser
        }

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(
                "350505976567-1brd7mb154gupn7ir9tvt7o4ti5ec3ri.apps.googleusercontent.com"
            )
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(application, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleClient.signInIntent
    }

    fun beginRegistration(
        name: String,
        email: String
    ) {
        registering = true
        pendingName = name.trim()
        pendingEmail = email.trim()
    }

    fun beginLogin() {
        registering = false
    }

    fun handleGoogleResult(data: Intent?) {
        try {
            val account = GoogleSignIn
                .getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)


            val credential =
                GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user ?: return@addOnSuccessListener

                    if (registering) {
                        registerUserIfNeeded(
                            firebaseUser.uid,
                            pendingName,
                            pendingEmail
                        )
                    } else {
                        subscribeExistingUser(firebaseUser.uid)
                        _user.value = firebaseUser
                    }

                    _user.value = firebaseUser
                }
                .addOnFailureListener {
                    Log.e("GOOGLE", "Firebase sign in failed", it)
                    Log.e(
                        "GOOGLE",
                        "Firebase credential failed",
                        it
                    )

                    showError(
                        it.message ?: "Authentication failed."
                    )
                }

        } catch (e: ApiException) {

            Log.e(
                "GOOGLE",
                "Google ApiException status=${e.statusCode}",
                e
            )

        } catch (e: Exception) {

            Log.e(
                "GOOGLE",
                "Google exception",
                e
            )
        }
    }

    private fun registerUserIfNeeded(
        uid: String,
        name: String,
        email: String
    ) {
        val ref =
            db.collection("users").document(uid)

        ref.get().addOnSuccessListener { doc ->

            if (!doc.exists()) {

                val data = hashMapOf(
                    "id" to uid,
                    "name" to name,
                    "email" to email,
                    "joined" to System.currentTimeMillis() / 1000,
                    "activeRoles" to listOf("student"),
                    "eventMinutesBefore" to 5
                )

                ref.set(data)
                    .addOnSuccessListener {
                        subscribeToTopics(uid, listOf("student"))
                        registrationComplete = true
                        showSuccess()
                        _user.value = auth.currentUser
                    }
                    .addOnFailureListener {
                        showError(it.localizedMessage ?: "Failed to create account.")
                        Log.e("REGISTER", "Failed", it)
                    }

            } else {
                val roles =
                    doc.get("activeRoles")
                            as? List<String>
                        ?: emptyList()
                subscribeToTopics(uid, roles)
            }
        }
    }

    private fun subscribeExistingUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val roles =
                    doc.get("activeRoles")
                            as? List<String>
                        ?: emptyList()
                subscribeToTopics(uid, roles)
            }
    }

    private fun subscribeToTopics(
        uid: String,
        roles: List<String>
    ) {
        roles.forEach { role ->
            FirebaseMessaging
                .getInstance()
                .subscribeToTopic(
                    topicName(role)
                )

        }
        FirebaseMessaging
            .getInstance()
            .subscribeToTopic("user_$uid")

        FirebaseMessaging
            .getInstance().token
            .addOnSuccessListener {
                Log.d("FCM", "TOKEN = $it")
            }
    }

    private fun topicName(role: String): String {
        return role
            .trim()
            .lowercase()
            .replace("\\s+".toRegex(), "_")
            .replace(
                "[^a-z0-9\\-_.~%]".toRegex(),
                ""
            )
    }

    fun logout() {
        auth.signOut()

        googleClient
            .signOut()
            .addOnCompleteListener {

                registering = false
                pendingName = ""
                pendingEmail = ""

                _user.value = null
            }
    }

    private fun showError(message: String) {
        alertMessage = message
        activeAlert = ActiveAlert.ERROR
    }

    private fun showSuccess(message: String = "Your account has been created.") {
        alertMessage = message
        activeAlert = ActiveAlert.SUCCESS
    }

    fun clearAlert() {
        activeAlert = null
        alertMessage = ""
    }

    fun finishRegistration() {
        registrationComplete = false
    }
}