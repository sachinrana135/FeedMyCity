package com.alfanse.feedindia.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.models.TodoEntity
import kotlinx.android.synthetic.main.row_item_todo_task.view.*

class TodoListAdapter(
    private val context: Context
) : RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {

    var todoList: List<TodoEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        return TodoListViewHolder(
            layoutInflater.inflate(
                R.layout.row_item_todo_task,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {

        holder.taskTitle.text = todoList[position].title
        holder.userName.text = todoList[position].userName
        holder?.taskStatus.apply {
            if (todoList[position].completed)
                setImageResource(R.drawable.task_completed)
            else
                setImageResource(R.drawable.task_not_completed)
        }


    }

    class TodoListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.tvUserName
        val taskTitle = view.tvTaskTitle
        val taskStatus = view.imgTaskStatus
    }
}