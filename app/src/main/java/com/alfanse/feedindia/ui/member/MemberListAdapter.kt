package com.alfanse.feedindia.ui.member

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.models.UserEntity
import kotlinx.android.synthetic.main.row_item_needier_item.view.*

class MemberListAdapter(
    private val context: Context,
    private val callHandler: (UserEntity?)-> Unit
) : PagedListAdapter<UserEntity, MemberListAdapter.MemberListViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        return MemberListViewHolder(
            layoutInflater.inflate(
                R.layout.row_item_member_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MemberListViewHolder, position: Int) {
        holder.userName.text = getItem(position)?.name
        if (getItem(position)?.isAdmin!!) {
            holder.status.visibility = View.VISIBLE
            holder.status.text = context.getString(R.string.txt_admin)
        }
        holder.locationAddress.text = getItem(position)?.address

        holder.callAction.setOnClickListener {
                callHandler(getItem(position))
        }

    }

    class MemberListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.tvUserName
        val status: TextView = view.status
        val callAction: ImageView = view.callAction
        val locationAddress: TextView = view.tv_address
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(
            oldItem: UserEntity,
            newItem: UserEntity
        ): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: UserEntity,
            newItem: UserEntity
        ): Boolean {
            return oldItem == newItem
        }

    }
}