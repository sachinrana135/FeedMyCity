package com.alfanse.feedindia.ui.mobileauth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.UserEntity
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.donor.DonorDetailsActivity
import com.alfanse.feedindia.ui.donor.DonorHomeActivity
import com.alfanse.feedindia.ui.groupdetails.GroupDetailsActivity
import com.alfanse.feedindia.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedindia.utils.FirebaseAuthHandler
import com.alfanse.feedindia.utils.UserType
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_mobile_verification.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MobileVerificationActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val mContext = this
    private lateinit var auth: FirebaseAuth

    private var verificationInProgress = false
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var firebaseAuthHandler: FirebaseAuthHandler
    private lateinit var mobileVerificationViewModel: MobileVerificationViewModel
    private var phoneNumber: String? = null
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_verification)
        title = getString(R.string.phone_verification_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (application as FeedIndiaApplication).appComponent.inject(this)
        mobileVerificationViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MobileVerificationViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        initListener()
        observeLiveData()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressBar.visibility = View.GONE
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                verificationInProgress = false
                firebaseAuthHandler = FirebaseAuthHandler(
                    mContext,
                    auth,
                    object : FirebaseAuthHandler.FirebaseAuthListener {
                        override fun onSuccess(user: FirebaseUser?) {
                            // If success then navigate user to Donor details screen
                            if (user != null) {
                                phoneNumber = user.phoneNumber
                                mobileVerificationViewModel.saveFirebaseUserId(user.uid)
                            }
                        }

                        override fun onError(msg: String?) {
                            //Show error msg
                        }

                        override fun invalidCode(error: String) {
                            //No usage in this case
                        }
                    })
                firebaseAuthHandler.signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                progressBar.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    etPhone.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d(TAG, "Project Quota exceeded")
                }

                //btnVerifyNumber.isEnabled = false
                //btnResend.isEnabled = true
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressBar.visibility = View.GONE
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                navigateToCodeVerificationScreen(storedVerificationId, phoneNumber)
            }
        }

        readUserType()
    }

    private fun readUserType() {
        userType = intent.getStringExtra(USER_TYPE_KEY)
    }


    private fun observeLiveData() {
        mobileVerificationViewModel.firebaseUserIdLiveData.observe(this, Observer<Boolean> {
            if (phoneNumber == null) {
                phoneNumber = ""
            }
            if (it) navigateToUserTypeDetailsScreen(phoneNumber!!)
        })

        mobileVerificationViewModel.userLiveData.observe(
            this,
            Observer<Resource<UserEntity>> { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        when (resource.data?.userType) {
                            UserType.DONOR -> {
                                val intent = Intent(mContext, DonorHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            UserType.MEMBER -> {
                                //navigate to member screen
                                val intent = Intent(mContext, GroupHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    Status.ERROR -> {
                        startPhoneNumberVerification(addCodeToPhoneNumber(etPhone.text.toString()))
                    }
                    Status.EMPTY -> {
                        startPhoneNumberVerification(addCodeToPhoneNumber(etPhone.text.toString()))
                    }
                }
            })
    }

    private fun navigateToUserTypeDetailsScreen(phone: String?) {
        var intent: Intent? = null
        when (userType) {
            UserType.DONOR -> {
                intent = Intent(mContext, DonorDetailsActivity::class.java).also {
                    it.putExtra(CodeVerificationActivity.MOBILE_NUM_KEY, phone)
                    it.putExtra(USER_TYPE_KEY, userType)
                }
            }
            UserType.MEMBER -> {
                intent = Intent(mContext, GroupDetailsActivity::class.java).also {
                    it.putExtra(CodeVerificationActivity.MOBILE_NUM_KEY, phone)
                    it.putExtra(USER_TYPE_KEY, userType)
                }
            }
        }

        startActivity(intent)
    }

    private fun navigateToCodeVerificationScreen(verificationId: String?, phone: String?) {
        val intent = Intent(mContext, CodeVerificationActivity::class.java).also {
            it.putExtra(CodeVerificationActivity.VERIFICATION_ID_KEY, verificationId)
            it.putExtra(CodeVerificationActivity.MOBILE_NUM_KEY, phone)
            it.putExtra(USER_TYPE_KEY, userType)
        }
        startActivity(intent)
    }

    private fun initListener() {
        btnVerifyNumber.setOnClickListener {
            if (!validatePhoneNumber()) {
                return@setOnClickListener
            }
            mobileVerificationViewModel.getUserByMobile(etPhone.text.toString())
        }

        btnResend.setOnClickListener {
            resendVerificationCode(addCodeToPhoneNumber(etPhone.text.toString()), resendToken)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )

//        progressBar.visibility = View.VISIBLE
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = etPhone.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhone.error = "Invalid phone number."
            return false
        }

        return true
    }

    private fun addCodeToPhoneNumber(phoneInput: String): String {
        return if (phoneInput.contains(CODE)) {
            val array = phoneInput.split("+91");
            CODE + SPACE + array[1]
        } else {
            "$CODE$SPACE$phoneInput"
        }
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
        private const val SPACE = " "
        private const val CODE = "+91"
        const val USER_TYPE_KEY = "UserTypeKey"
    }
}
