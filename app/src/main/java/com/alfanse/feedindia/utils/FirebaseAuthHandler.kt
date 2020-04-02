package com.alfanse.feedindia.utils

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

class FirebaseAuthHandler(activity: Activity, auth: FirebaseAuth, firebaseAuthListener: FirebaseAuthListener) {
    private var mAuth: FirebaseAuth? = null
    private var mActivity: Activity = activity
    private var mFirebaseAuthCallback: FirebaseAuthListener = firebaseAuthListener

    init {
        mAuth = auth
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(mActivity) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    mFirebaseAuthCallback.onSuccess(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        mFirebaseAuthCallback.invalidCode("Invalid Code")
                    } else{
                        mFirebaseAuthCallback.onError(task.exception?.message)
                    }
                }
            }
    }

    interface FirebaseAuthListener {
        fun onSuccess(user: FirebaseUser?)
        fun onError(msg: String?)
        fun invalidCode(error: String)
    }
}