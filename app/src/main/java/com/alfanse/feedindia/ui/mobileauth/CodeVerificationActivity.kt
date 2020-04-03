package com.alfanse.feedindia.ui.mobileauth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.utils.FirebaseAuthHandler
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_code_verification.*
import javax.inject.Inject

class CodeVerificationActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private lateinit var codeVerificationViewModel: CodeVerificationViewModel
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_verification)
        title = getString(R.string.phone_verification_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as FeedIndiaApplication).appComponent.inject(this)
        codeVerificationViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(CodeVerificationViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        readVerificationId()
        initListener()
        observeLiveData()
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
                            if (user != null){
                                phoneNumber = user.phoneNumber?.replace("+91","")
                                codeVerificationViewModel.saveFirebaseUserId(user.uid)
                            }
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

    private fun observeLiveData(){
        codeVerificationViewModel.firebaseUserIdLiveData.observe(this, Observer<Boolean>{
            if(phoneNumber == null){
                phoneNumber = ""
            }
            if(it) navigateToNextScreen(phoneNumber!!)
        })
    }


    private fun navigateToNextScreen(phone: String){
        val intent = Intent(this, DonorDetailsActivity::class.java)
        intent.putExtra(MOBILE_NUM_KEY, phone)
        startActivity(intent)
    }


    private fun readVerificationId(){
        storedVerificationId = intent.getStringExtra(VERIFICATION_ID_KEY)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        private const val TAG = "MobileVerification"
        const val VERIFICATION_ID_KEY = "VerificationIdKey"
        const val MOBILE_NUM_KEY = "mobileNum"
    }
}
