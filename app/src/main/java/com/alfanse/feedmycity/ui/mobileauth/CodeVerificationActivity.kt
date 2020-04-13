package com.alfanse.feedmycity.ui.mobileauth

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.UserEntity
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.receiver.SmsBroadcastReceiver
import com.alfanse.feedmycity.ui.donor.DonorDetailsActivity
import com.alfanse.feedmycity.ui.donor.DonorHomeActivity
import com.alfanse.feedmycity.ui.groupdetails.GroupDetailsActivity
import com.alfanse.feedmycity.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedmycity.ui.member.AddMemberActivity
import com.alfanse.feedmycity.utils.FirebaseAuthHandler
import com.alfanse.feedmycity.utils.UserType
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_code_verification.*
import javax.inject.Inject

class CodeVerificationActivity : AppCompatActivity() {
    private var mContext = this
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private lateinit var codeVerificationViewModel: CodeVerificationViewModel
    private var phoneNumber: String? = null
    private var userType: String? = null
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_verification)
        title = getString(R.string.phone_verification_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as FeedMyCityApplication).appComponent.inject(this)
        codeVerificationViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(CodeVerificationViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        readVerificationId()
        initListener()
        observeLiveData()
        readUserType()

        startSmsUserConsent()
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun startSmsUserConsent(){
        SmsRetriever.getClient(this).also {
            //We can add user phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_USER_CONSENT -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val code = message?.let { fetchVerificationCode(it) }

                    etOtp.setText(code)
                }
            }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver().also {
            it.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    intent?.let { context -> startActivityForResult(context, REQ_USER_CONSENT) }
                }

                override fun onFailure() {
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }

    private fun readUserType() {
        userType = intent.getStringExtra(MobileVerificationActivity.USER_TYPE_KEY)
        val phone = intent.getStringExtra(MOBILE_NUM_KEY)
    }

    private fun initListener(){
        btnVerify.setOnClickListener {
            if (storedVerificationId != null){
                if (!etOtp.text.isNullOrBlank()){
                    progressBar.visibility = View.VISIBLE
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, etOtp.text.toString().trim())
                    val firebaseAuthHandler = FirebaseAuthHandler(this, auth, object: FirebaseAuthHandler.FirebaseAuthListener {
                        override fun onSuccess(user: FirebaseUser?) {
                            progressBar.visibility = View.GONE
                            if (user != null){
                                phoneNumber = user.phoneNumber
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
            if(it) {
                codeVerificationViewModel.getUserByMobile(phoneNumber!!)
            }
        })

        codeVerificationViewModel.userLiveData.observe(
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
                                Toast.makeText(applicationContext, "This donor is already registered with this mobile number",
                                    Toast.LENGTH_LONG).show()
                                val intent = Intent(mContext, DonorHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            UserType.MEMBER -> {
                                //navigate to member screen
                                Toast.makeText(applicationContext, "This member is already registered with this mobile number",
                                    Toast.LENGTH_LONG).show()
                                val intent = Intent(mContext, GroupHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    Status.ERROR -> {
                        navigateToUserTypeDetailsScreen(phoneNumber)
                    }
                    Status.EMPTY -> {
                        navigateToUserTypeDetailsScreen(phoneNumber)
                    }
                }
            })
    }

    private fun navigateToUserTypeDetailsScreen(phone: String?) {
        var intent: Intent? = null
        when (userType) {
            UserType.DONOR -> {
                intent = Intent(this, DonorDetailsActivity::class.java).also {
                    it.putExtra(MOBILE_NUM_KEY, phone)
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            UserType.MEMBER -> {

                if(codeVerificationViewModel.isGroupIdExist()){
                    intent = Intent(this, AddMemberActivity::class.java).also {
                        it.putExtra(MOBILE_NUM_KEY, phone)
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }else {
                    intent = Intent(this, GroupDetailsActivity::class.java).also {
                        it.putExtra(MOBILE_NUM_KEY, phone)
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            }
        }

        startActivity(intent)
        finish()
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
        const val REQ_USER_CONSENT = 100
    }
}
