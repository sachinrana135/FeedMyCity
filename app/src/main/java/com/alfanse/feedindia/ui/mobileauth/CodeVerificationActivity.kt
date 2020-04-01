package com.alfanse.feedindia.ui.mobileauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alfanse.feedindia.R
import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.utils.FirebaseAuthHandler
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_code_verification.*

class CodeVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_verification)
        title = getString(R.string.phone_verification_screen_label)
        auth = FirebaseAuth.getInstance()

        readVerificationId()
        initListener()
    }

    private fun initListener(){
        btnVerify.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            if (storedVerificationId != null){
                if (!etOtp.text.isNullOrBlank()){
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, etOtp.text.toString())
                    val firebaseAuthHandler = FirebaseAuthHandler(this, auth, object: FirebaseAuthHandler.FirebaseAuthListener {
                        override fun onSuccess(user: FirebaseUser?) {
                            progressBar.visibility = View.GONE
                            navigateToNextScreen()
                        }

                        override fun onError(msg: String?) {
                            progressBar.visibility = View.GONE
                            Snackbar.make(findViewById(android.R.id.content), "Something went wrong",
                                Snackbar.LENGTH_SHORT).show()
                        }

                        override fun invalidCode(error: String) {
                            progressBar.visibility = View.GONE
                            Snackbar.make(findViewById(android.R.id.content), "Invalid Code",
                                Snackbar.LENGTH_SHORT).show()
                        }
                    })
                    firebaseAuthHandler.signInWithPhoneAuthCredential(credential)
                }
            }
        }
    }

    private fun navigateToNextScreen(){
        startActivity(Intent(this, DonorDetailsActivity::class.java))
    }
    private fun readVerificationId(){
        storedVerificationId = intent.getStringExtra(VERIFICATION_ID_KEY)
    }


    companion object {
        private const val TAG = "MobileVerification"
        const val VERIFICATION_ID_KEY = "VerificationIdKey"
    }
}
