package com.project.hobnob.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.hobnob.R
import com.project.hobnob.databinding.ItemMessageBinding
import com.project.hobnob.model.Message

class ChatRecyclerAdapter(
    private val listener: OnItemClickListener,
    private val myName: String
) :
    RecyclerView.Adapter<ChatRecyclerAdapter.ChatRecyclerViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    var oldNewsList: List<Message> = emptyList()

    inner class ChatRecyclerViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                if (v == binding.root)
                    listener.onItemClick(position)
        }

        fun setChatData(message: Message) {
            binding.tvBody.text = message.body
            val sender: String
            if (message.sender?.equals(myName) == true) {
                sender = "I said:"
                val params = binding.msgCard.layoutParams as ConstraintLayout.LayoutParams
                params.endToEnd = binding.msgParent.id
                params.topToTop = binding.msgParent.id
                params.bottomToBottom = binding.msgParent.id
                params.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.msgContainer.setBackgroundResource(R.color.bgMessageSent)
                binding.msgCard.requestLayout()
            } else {
                sender = "${message.senderName} said:"
                val params = binding.msgCard.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = binding.msgParent.id
                params.topToTop = binding.msgParent.id
                params.bottomToBottom = binding.msgParent.id
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.msgContainer.setBackgroundResource(R.color.bgMessageReceived)
                binding.msgCard.requestLayout()
            }
            binding.tvSender.text = sender
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerViewHolder {
        return ChatRecyclerViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatRecyclerViewHolder, position: Int) {
        holder.setChatData(oldNewsList[position])
    }

    override fun getItemCount(): Int {
        return oldNewsList.size
    }

    fun setData(newNewsList: List<Message>) {
        val diffUtil = ChatDiffUtil(oldNewsList, newNewsList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldNewsList = newNewsList
        diffResult.dispatchUpdatesTo(this)
    }

}