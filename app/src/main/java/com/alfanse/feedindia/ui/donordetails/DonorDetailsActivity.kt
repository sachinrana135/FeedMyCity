package com.alfanse.feedindia.ui.donordetails

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import kotlinx.android.synthetic.main.activity_donor_details.*
import javax.inject.Inject

class DonorDetailsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var donorDetailViewModel: DonorDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        title = getString(R.string.donor_details_screen_label)
        (application as FeedIndiaApplication).appComponent.inject(this)
        donorDetailViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(DonorDetailsViewModel::class.java)
        initListener()
        donorDetailViewModel.saveDonorLiveData.observe(this, observer)
    }

    private fun readPhoneNum(): String? {
        return intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)
    }

    private fun initListener(){
        var status = 0
        rbActive.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                status = 1
            }
        })

        btnSave.setOnClickListener {
            if (etName.text.toString().trim().isEmpty()){
                etName.error = "Enter name"
            } else if (etDonationInfo.text.toString().trim().isEmpty()){
                etDonationInfo.error = "Enter your donation"
            }

            donorDetailViewModel.saveDonorDetails(etName.text.toString(),
                etDonationInfo.text.toString(), status.toString(),
                "-123.456789",
                "-123.456789",
                "8010788258")
                //readPhoneNum()!!)
        }
    }

    private var observer = Observer<Resource<String>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
                //Log.d(TAG, it.data?.response?.userId)
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
            }
            Status.EMPTY, Status.ERROR -> {
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "DonorDetailsActivity"
    }
}
