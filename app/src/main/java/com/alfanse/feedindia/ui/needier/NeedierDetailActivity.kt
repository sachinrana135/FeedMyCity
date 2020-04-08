package com.alfanse.feedindia.ui.needier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.NeedierItemStatusEntity
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.data.models.UpdateNeedierItemStatusRequest
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.comment.CommentFragment
import com.alfanse.feedindia.utils.BUNDLE_KEY_NEEDIER_ITEM
import com.alfanse.feedindia.utils.PermissionUtils
import com.alfanse.feedindia.utils.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_donor_details.progressBar
import kotlinx.android.synthetic.main.activity_needier_detail.*
import javax.inject.Inject


class NeedierDetailActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: NeedierDetailViewModel
    private var needierItemId: String? = null

    private var needItem: NeedieritemEntity? = null
    private var needItemStatusList = listOf<NeedierItemStatusEntity>()
    private lateinit var commentFragment:CommentFragment
    private var selectedStatus:String? = null
    private var selectedStatusId:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needier_detail)

        readIntent()

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.txt_Needier_detail)

        (application as FeedIndiaApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NeedierDetailViewModel::class.java)

        viewModel.getNeedier(needierItemId!!)
        viewModel.getNeedierItemStatus()

        viewModel.needierLiveData.observe(this, needierObserver)
        viewModel.needierStatusTypesLiveData.observe(this, needierStatusObserver)
        viewModel.updateneedierStatusLiveData.observe(this, updateNeedierStatusObserver)

        if (savedInstanceState == null) {
            commentFragment = CommentFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.commentContainer, commentFragment)
                .commitNow()
        }

        initListener()
    }

    private fun initListener() {
        callAction.setOnClickListener {
            requestPermission(needItem)
        }

        btnUpdate.setOnClickListener {
            if(!selectedStatus?.equals(needItem?.status)!!) {
                viewModel.updateNeedierItemStatus(
                    UpdateNeedierItemStatusRequest(
                        needier_item_id = needierItemId!!,
                        status_id = selectedStatusId!!,
                        member_id = User.userId!!
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission(user: NeedieritemEntity?) {
        if (PermissionUtils.isAccessPhoneCallGranted(this)) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:{${user?.mobile}}")
            startActivity(callIntent)
        } else {
            PermissionUtils.requestAccessPhoneCallPermission(
                this,
                PHONE_CALL_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PHONE_CALL_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:{${needItem?.mobile}}")
                    startActivity(callIntent)
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.call_permission_not_granted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private var needierObserver = Observer<Resource<NeedieritemEntity>> {
        when (it.status) {
            Status.LOADING -> {
                data_container.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                needItem = it.data
                progressBar.visibility = View.GONE
                data_container.visibility = View.VISIBLE
                tvUserName.text = it.data?.name
                status.text = it.data?.status
                tv_items_need.text = it.data?.needItems
                tv_address.text = it.data?.address
                commentFragment.fetchComments(it.data?.needierItemId!!)
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(
                    findViewById(android.R.id.content), it.message!!,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private var needierStatusObserver = Observer<Resource<List<NeedierItemStatusEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                data_container.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                data_container.visibility = View.VISIBLE
                needItemStatusList = it.data!!

                var spinnerArray = needItemStatusList.map {status ->
                     status.name
                }

                val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, spinnerArray)
                arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                status_spinner.adapter = arrayAdapter
                status_spinner.setSelection(spinnerArray.indexOf(needItem?.status))

                status_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedStatus = needItemStatusList[position].name
                        selectedStatusId = needItemStatusList[position].id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Code to perform some action when nothing is selected
                    }
                }


            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(
                    findViewById(android.R.id.content), it.message!!,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private var updateNeedierStatusObserver = Observer<Resource<Any>> {
        when (it.status) {
            Status.LOADING -> {
                updateStatusProgressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                updateStatusProgressBar.visibility = View.GONE
                status.text = selectedStatus
                commentFragment.refreshComments()
                Snackbar.make(
                    findViewById(android.R.id.content), getString(R.string.txt_save_success),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.ERROR -> {
                Snackbar.make(
                    findViewById(android.R.id.content), it.message!!,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private fun readIntent() {
        if (intent != null) {
            needierItemId = intent.getStringExtra(BUNDLE_KEY_NEEDIER_ITEM)!!
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
        private const val TAG = "NeedierDetailActivity"
        private const val PHONE_CALL_PERMISSION_REQUEST_CODE = 888
    }
}
