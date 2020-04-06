package com.alfanse.feedindia.ui.needier

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.factory.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_needier_list.*
import javax.inject.Inject

class NeedierListActivity : AppCompatActivity() {

    private lateinit var adapter: NeedierListAdapter

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: NeedierListViewModel
    val status = DEFAULT_NEEDIER_STATUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needier_list)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.txt_Needier)

        (application as FeedIndiaApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NeedierListViewModel::class.java)

        viewModel.getNeediers(status)
        viewModel.needierLiveData.observe(this, observer)

        adapter = NeedierListAdapter(this)
        rvNeedierList.adapter = adapter

        initListener()
    }

    private fun initListener(){
        mainLayout.setOnRefreshListener {
            viewModel.getNeediers(status)
        }
    }

    private var observer = Observer<Resource<PagedList<NeedieritemEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                rvNeedierList.showShimmer()
            }
            Status.SUCCESS -> {
                rvNeedierList.hideShimmer()
                adapter.submitList(it.data)
            }
            Status.ERROR -> {
                rvNeedierList.hideShimmer()
                Snackbar.make(findViewById(android.R.id.content), it.message!!,
                    Snackbar.LENGTH_SHORT).show()
            }
            Status.EMPTY ->{
                //Todo no data handling
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
    }
}
