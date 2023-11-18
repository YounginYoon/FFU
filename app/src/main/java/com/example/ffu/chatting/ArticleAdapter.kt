package com.example.ffu.chatting


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ffu.R
import com.example.ffu.UserInformation.Companion.ANIMATION
import com.example.ffu.UserInformation.Companion.CHAT_LAST_LOG
import com.example.ffu.UserInformation.Companion.DATE_LAST_LOG
import com.example.ffu.databinding.ItemChattingArticleBinding
import com.example.ffu.utils.ChattingArticle
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticleAdapter(val onItemClicked: (ChattingArticle) -> Unit) : ListAdapter<ChattingArticle, ArticleAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemChattingArticleBinding): RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ResourceAsColor")
        fun bind(chattingArticleModel: ChattingArticle){
            val dateLastLog = DATE_LAST_LOG[chattingArticleModel.Id]
            val chatLastLog = CHAT_LAST_LOG[chattingArticleModel.Id]

            binding.itemChattingArticleName.text = chattingArticleModel.Name

            if (dateLastLog == null) { // 첫 매칭된 경우
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val formatted = current.format(formatter)

                binding.itemChattingArticleChatLog.text = "채팅방이 생성되었습니다."
                binding.itemChattingArticleDate.text = getLastDate(formatted.toString())
            } else {
                binding.itemChattingArticleDate.text = getLastDate(dateLastLog)
                binding.itemChattingArticleChatLog.text = getLastChat(chatLastLog)
            }

            if (ANIMATION[chattingArticleModel.Id]!!.permission) {
                Glide.with(binding.root)
                    .load(chattingArticleModel.imageUri)
                    .into(binding.itemChattingArticleThumbnailImageView)
            } else {
                binding.itemChattingArticleThumbnailImageView.setImageResource(R.drawable.profileimage)
            }

            binding.root.setOnClickListener {
                onItemClicked(chattingArticleModel)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getLastDate(lastDateLog: String?): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)

            val curDate = formatted.split("-")
            Log.d("check date", lastDateLog.toString() + " / " + curDate)
            val lastDate = lastDateLog!!.split("-")
            var result : String = ""

            if (curDate[0] == lastDate[0] && curDate[1] == lastDate[1]
                && curDate[2].split(" ")[0] == lastDate[2].split(" ")[0]) {
                result = lastDateLog.split(" ")[1]
                val hourInt = result.split(":")[0].toInt()
                val hour = result.split(":")[0].substring(0, 2)
                val minute = result.split(":")[1].substring(0, 2)

                if (hourInt < 12) {
                    result = "오전 " + hour + ":" + minute
                } else {
                    result = "오후 " + hour + ":" + minute
                }
            } else {
                val month = lastDate[1].toInt()
                val day = lastDate[2].split(" ")[0].toInt()

                result = month.toString() + "월 " + day.toString() + "일"
            }

            return result
        }

        private fun getLastChat(lastChatLog : String?) : String {
            val chatLength = lastChatLog!!.length

            return when(chatLength) {
                in 0..19 -> lastChatLog
                in 20..29 -> lastChatLog.substring(0, 20) + "\n" + lastChatLog.substring(20, chatLength)
                else -> lastChatLog.substring(0, 20) + "\n" + lastChatLog.substring(20, 30) + "..."
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(ItemChattingArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChattingArticle>() {
            override fun areItemsTheSame(oldItem: ChattingArticle, newItem: ChattingArticle): Boolean {
                return oldItem.Id == newItem.Id
            }

            override fun areContentsTheSame(oldItem: ChattingArticle, newItem: ChattingArticle): Boolean {
                return oldItem == newItem
            }
        }
    }

}