package com.example.ffu.chatting


import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ffu.R
import com.example.ffu.databinding.ItemHistoryBinding
import com.example.ffu.utils.History
import com.example.ffu.utils.History.Companion.MATCH_TYPE
import com.example.ffu.utils.History.Companion.RECEIVE_TYPE
import com.example.ffu.utils.History.Companion.SEND_TYPE

class HistoryAdapter() : ListAdapter<History, HistoryAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(historyModel: History){
            when(historyModel.type){
                SEND_TYPE->{
                    binding.itemHistoryMessage.text=historyModel.name+" 님에게 좋아요 를 보냈습니다."
                    binding.itemHistoryIc.setImageResource(R.drawable.ic_likefull)
                }
                RECEIVE_TYPE->{
                    binding.itemHistoryMessage.text=historyModel.name+" 님이 좋아요 를 보냈습니다."
                    binding.itemHistoryIc.setImageResource(R.drawable.ic_likefull)
                }
                MATCH_TYPE->{
                    binding.itemHistoryMessage.text=historyModel.name+" 님과 매칭 되었습니다."
                    binding.itemHistoryIc.setImageResource(R.drawable.ic_match)
                }
            }
            val timeSplit: List<String> = historyModel.time.split(" ")
            binding.itemHistoryDate.text=timeSplit[0]
            binding.itemHistoryTime.text=timeSplit[1]

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
        }
    }

}