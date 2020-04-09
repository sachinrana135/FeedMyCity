package com.alfanse.feedindia.ui.needier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.utils.BUNDLE_KEY_NEEDIER_ITEM
import com.alfanse.feedindia.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_needier_list.*
import javax.inject.Inject

class NeedierListActivity : AppCompatActivity() {

    private lateinit var adapter: NeedierListAdapter

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: NeedierListViewModel
    val status = DEFAULT_NEEDIER_STATUS
    var user: NeedieritemEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needier_list)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.txt_Needier)

        (application as FeedIndiaApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NeedierListViewModel::class.java)

        rvNeedierList.showShimmer()
        viewModel.getNeediers(status)

        viewModel.needierResourceLiveData.observe(this, observer)
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
            viewModel.getNeediers(status)
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
                    findViewById(android.R.id.content), it.message!!,
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
