package com.project.hobnob.adapter

import androidx.recyclerview.widget.DiffUtil
import com.project.hobnob.model.Message

class ChatDiffUtil(
    private val oldList: List<Message>,
    private val newList: List<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].body != newList[newItemPosition].body -> false
            oldList[oldItemPosition].receiver != newList[newItemPosition].receiver -> false
            oldList[oldItemPosition].sender != newList[newItemPosition].sender -> false
            oldList[oldItemPosition].timestamp != newList[newItemPosition].timestamp -> false
            else -> true
        }
    }
}