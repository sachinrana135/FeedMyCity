package com.alfanse.feedindia.ui.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.models.CommentEntity
import kotlinx.android.synthetic.main.row_item_comment_item.view.*
import kotlinx.android.synthetic.main.row_item_needier_item.view.tvUserName

class CommentListAdapter(
    private val context: Context
) : PagedListAdapter<CommentEntity, CommentListAdapter.CommentListViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        return CommentListViewHolder(
            layoutInflater.inflate(
                R.layout.row_item_comment_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        holder.userName.text = getItem(position)?.userName
        holder.comment.text = getItem(position)?.comment
        holder.date.text = getItem(position)?.date
    }

    class CommentListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.tvUserName
        val comment: TextView = view.tv_comment
        val date: TextView = view.date

    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<CommentEntity>() {
        override fun areItemsTheSame(
            oldItem: CommentEntity,
            newItem: CommentEntity
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CommentEntity,
            newItem: CommentEntity
        ): Boolean {
            return oldItem.userName == newItem.userName
                    && oldItem.comment == newItem.comment
        }

    }
}