package com.alfanse.feedmycity.ui.needier

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
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.NeedierItemStatusEntity
import com.alfanse.feedmycity.data.models.NeedieritemEntity
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.utils.BUNDLE_KEY_NEEDIER_ITEM
import com.alfanse.feedmycity.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_needier_list.*
import javax.inject.Inject

class NeedierListActivity : AppCompatActivity() {

    private lateinit var adapter: NeedierListAdapter

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: NeedierListViewModel
    var user: NeedieritemEntity? = null
    private var needItemStatusList = listOf<NeedierItemStatusEntity>()
    private var selectedStatus:String? = null
    private var selectedStatusId:String = DEFAULT_NEEDIER_STATUS
    private var needItem: NeedieritemEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needier_list)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.txt_Needier)

        (application as FeedMyCityApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NeedierListViewModel::class.java)

        rvNeedierList.showShimmer()
        viewModel.getNeediers(selectedStatusId)
        viewModel.getNeedierItemStatus()

        viewModel.needierResourceLiveData.observe(this, observer)
        viewModel.needierStatusTypesLiveData.observe(this, needierStatusObserver)
        viewModel.needierLiveData.observe(this, Observer {
            adapter.submitList(it)
        })

        adapter = NeedierListAdapter(this, {
            user = it
            requestPermission(user)
        }, {
                val detailIntent = Intent(this, NeedierDetailActivity::class.java)
                detailIntent.putExtra(BUNDLE_KEY_NEEDIER_ITEM, it?.needierItemId)
                startActivity(detailIntent)
            }
        )
        rvNeedierList.adapter = adapter

        initListener()
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
                    callIntent.data = Uri.parse("tel:{${user?.mobile}}")
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

    private fun initListener() {
        mainLayout.setOnRefreshListener {
            rvNeedierList.showShimmer()
            viewModel.refresh()
            viewModel.getNeediers(selectedStatusId)
        }
    }

    private var observer = Observer<Resource<List<NeedieritemEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                rvNeedierList.visibility = View.VISIBLE
                txt_no_data.visibility = View.GONE
            }
            Status.SUCCESS -> {
                rvNeedierList.visibility = View.VISIBLE
                rvNeedierList.hideShimmer()
                mainLayout.isRefreshing = false
            }
            Status.ERROR -> {
                rvNeedierList.hideShimmer()
                mainLayout.isRefreshing = false
                Snackbar.make(
                    findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {
                mainLayout.isRefreshing = false
                rvNeedierList.hideShimmer()
                rvNeedierList.visibility = View.GONE
                txt_no_data.visibility = View.VISIBLE
            }
        }
    }

    private var needierStatusObserver = Observer<Resource<List<NeedierItemStatusEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                rvNeedierList.showShimmer()
            }
            Status.SUCCESS -> {
                rvNeedierList.hideShimmer()
                needItemStatusList = it.data!!

                var spinnerArray = needItemStatusList.map {status ->
                    status.name
                }

                val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, spinnerArray)
                arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                status_spinner.adapter = arrayAdapter

                status_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        selectedStatus = needItemStatusList[position].name
                        selectedStatusId = needItemStatusList[position].id!!
                        rvNeedierList.showShimmer()
                        viewModel.refresh()
                        viewModel.getNeediers(selectedStatusId!!)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Code to perform some action when nothing is selected
                    }
                }


            }
            Status.ERROR -> {
                rvNeedierList.hideShimmer()
                Snackbar.make(
                    findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
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
        private const val TAG = "NeedierListActivity"
        private const val DEFAULT_NEEDIER_STATUS = "1"
        private const val PHONE_CALL_PERMISSION_REQUEST_CODE = 888
    }
}
