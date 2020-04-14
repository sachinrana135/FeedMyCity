package com.alfanse.feedmycity.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.CommentEntity
import com.alfanse.feedmycity.data.models.SaveCommentRequest
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.utils.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.comment_fragment.*
import javax.inject.Inject

class CommentFragment private constructor() : Fragment() {

    companion object {
        fun newInstance() = CommentFragment()
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var adapter: CommentListAdapter
    private lateinit var viewModel: CommentViewModel
    private var mNeedierItemId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comment_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity().applicationContext as FeedMyCityApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CommentViewModel::class.java)

        adapter = CommentListAdapter(requireActivity())
        rvCommentList.adapter = adapter

        initListener()
        viewModel.saveCommentLiveData.observe(viewLifecycleOwner, saveCommentObserver)

    }

    private fun initListener() {
        imgSaveComment.setOnClickListener{
            if(edt_comment.text.isNullOrEmpty()) {
                edt_comment.error = requireActivity().getString(R.string.error_mandatory)
            }
            else{
                viewModel.saveComment(SaveCommentRequest(
                    comment = edt_comment.text.toString(),
                    member_id = User.userId!!,
                    needier_item_id = mNeedierItemId
                ))
            }

        }
    }

    fun fetchComments(needierItemId: String) {
        mNeedierItemId = needierItemId
        rvCommentList.showShimmer()
        viewModel.getComments(mNeedierItemId!!)

        viewModel.commentResourceLiveData.observe(viewLifecycleOwner, observer)
        viewModel.commentLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    fun refreshComments() {
        viewModel.refresh()
    }

    private var observer = Observer<Resource<List<CommentEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                rvCommentList.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                rvCommentList.visibility = View.VISIBLE
                rvCommentList.hideShimmer()
            }
            Status.ERROR -> {
                rvCommentList.hideShimmer()
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {
                rvCommentList.hideShimmer()
                rvCommentList.visibility = View.GONE
            }
        }
    }

    private var saveCommentObserver = Observer<Resource<Any>> {
        when (it.status) {
            Status.LOADING -> {
                saveCommentProgressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                saveCommentProgressBar.visibility = View.GONE
                edt_comment?.text?.clear()
                refreshComments()
            }
            Status.ERROR -> {
                saveCommentProgressBar.visibility = View.GONE
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

}
