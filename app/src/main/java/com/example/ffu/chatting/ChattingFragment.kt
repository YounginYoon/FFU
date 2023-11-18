package com.example.ffu.chatting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffu.R
import com.example.ffu.UserInformation
import com.example.ffu.UserInformation.Companion.CURRENT_USERID
import com.example.ffu.UserInformation.Companion.DATE_LAST_LOG
import com.example.ffu.UserInformation.Companion.MATCH_USER
import com.example.ffu.UserInformation.Companion.URI
import com.example.ffu.chatdetail.ChatRoomActivity
import com.example.ffu.databinding.FragmentChattingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.example.ffu.UserInformation.Companion.PROFILE
import com.example.ffu.utils.ChattingArticle
import com.example.ffu.utils.Listener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChattingFragment: Fragment(R.layout.fragment_chatting) {
    companion object {
        val articleList = mutableListOf<ChattingArticle>()
    }
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var pathReference : StorageReference


    private var binding: FragmentChattingBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentChattingBinding.bind(view)

        binding = fragmentHomeBinding
        articleList.clear()
        userDB = Firebase.database.reference
        storage = FirebaseStorage.getInstance()
        pathReference = storage.reference

        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                // 로그인을 한 상태
                if (CURRENT_USERID != articleModel.Id) {
                    // 채팅방으로 이동 하는 코드
                    context?.let {
                        val intent = Intent(it, ChatRoomActivity::class.java)
                        //chatKey 수정해야됨
                        //intent.putExtra("OtherName", articleModel.Name)
                        //Log.d("OtherName",articleModel.Name!!)
                        intent.putExtra("OtherName", articleModel.Name)
                        intent.putExtra("OtherId", articleModel.Id)
                        startActivity(intent)
                    }
                    //Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()
                }
            }
        })

        addArticleList()
        chattingListener()

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter
    }

    private fun chatLastLog(userId : String) {
        userDB = Firebase.database.reference.child("Chats").child(CURRENT_USERID).child(userId)
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                addArticleList()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun chattingListener() {
        userDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("match")
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                addArticleList()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                addArticleList()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        userDB = Firebase.database.reference.child("Chats").child(CURRENT_USERID)
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val matchUserId = snapshot.key.toString()
                chatLastLog(matchUserId)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendDateToLong(lastSendDate : String?) : Long {
        var sendDate = lastSendDate

        if (lastSendDate == null) // 첫 매칭된 유저가 가장 상단에 배치되도록 우선순위 부여
        {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)

            sendDate = formatted.toString()
        }

        val year = sendDate!!.split("-")[0]
        val month = sendDate.split("-")[1]
        val day = sendDate.split("-")[2].split(" ")[0]
        val hour = sendDate.split(" ")[1].split(":")[0]
        val minute = sendDate.split(" ")[1].split(":")[1]

        if (minute.last() == '_')
            return (year + month + day + hour + minute.substring(0, 2)).toLong()
        return (year + month + day + hour + minute).toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addArticleList() {
        articleList.clear()
        for (matchId in MATCH_USER.keys) {
            val name = PROFILE[matchId]?.nickname ?: ""
            val gender = PROFILE[matchId]?.gender ?: ""
            val birth = PROFILE[matchId]?.birth ?: ""
            val imageUri = URI[matchId]
            val sendDate = sendDateToLong(DATE_LAST_LOG[matchId])

            articleList.add(ChattingArticle(matchId,name,gender,birth,imageUri,sendDate))
        }

        val sortedArticleList = articleList.sortedByDescending { it.sendDate }
        articleAdapter.submitList(sortedArticleList)
        articleAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        articleList.clear()
        addArticleList()
        articleAdapter.notifyDataSetChanged()
    }
}