package com.example.ffu.matching

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ffu.R
import com.example.ffu.UserInformation
import com.example.ffu.UserInformation.Companion.ANIMATION
import com.example.ffu.UserInformation.Companion.CURRENT_USERID
import com.example.ffu.UserInformation.Companion.MATCH_USER
import com.example.ffu.UserInformation.Companion.PROFILE
import com.example.ffu.UserInformation.Companion.RECEIVED_LIKE_USER
import com.example.ffu.UserInformation.Companion.URI
import com.example.ffu.databinding.FragmentMatchingBinding
import com.example.ffu.recommend.RecommendData
import com.example.ffu.utils.History
import com.example.ffu.utils.LikeArticle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MatchingFragment: Fragment(R.layout.fragment_matching) {
    private lateinit var likeArticleAdapter: LikeArticleAdapter
    private val likeArticleList = mutableListOf<LikeArticle>()
    private var binding: FragmentMatchingBinding? = null
    private lateinit var userDB: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMatchingBinding = FragmentMatchingBinding.bind(view)

        binding = fragmentMatchingBinding
        likeArticleList.clear()

        likeArticleAdapter = LikeArticleAdapter(onItemClicked = { likeArticleModel ->
            showUserInformation(likeArticleModel)
        })

        addReceivedLikeArticleList()
        receivedLikeListener()

        fragmentMatchingBinding.matchingRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentMatchingBinding.matchingRecyclerView.adapter = likeArticleAdapter
    }

    private fun receivedLikeListener() {
        userDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("receivedLike")
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                addReceivedLikeArticleList()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                addReceivedLikeArticleList()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addReceivedLikeArticleList(){
        likeArticleList.clear()
        for (likeId in RECEIVED_LIKE_USER.keys) {
            if(RECEIVED_LIKE_USER[likeId] == true) {
                val name = PROFILE[likeId]?.nickname ?: ""
                val ageJob = PROFILE[likeId]?.age+ ", "+ PROFILE[likeId]?.job
                val introMe = PROFILE[likeId]?.introMe ?: ""
                //val gender = PROFILE[likeId]?.gender ?: ""
                //val birth = PROFILE[likeId]?.birth ?: ""
                val imageUri = URI[likeId]?:""
                //Log.d("name",name)
                likeArticleList.add(LikeArticle(likeId,name,ageJob,introMe,imageUri))
            }
        }
        likeArticleAdapter.submitList(likeArticleList)
        likeArticleAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        likeArticleAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showUserInformation(likeArticleModel: LikeArticle) {
        val userId = likeArticleModel.Id
        val dialog = AlertDialog.Builder(requireActivity()).create()
        val edialog : LayoutInflater = LayoutInflater.from(requireActivity())
        val mView : View = edialog.inflate(R.layout.dialog_userinformation,null)
        val image : CircleImageView = mView.findViewById(R.id.dialog_userinformation_photo)
        val age : TextView = mView.findViewById(R.id.dialog_userinformation_age)
        //val birth : TextView = mView.findViewById(R.id.dialog_userinformation_birth)
        val drinking : TextView = mView.findViewById(R.id.dialog_userinformation_drinking)
        val hobby : TextView = mView.findViewById(R.id.dialog_userinformation_hobby)
        val job : TextView = mView.findViewById(R.id.dialog_userinformation_job)
        val mbti : TextView = mView.findViewById(R.id.dialog_userinformation_mbti)
        val personality : TextView = mView.findViewById(R.id.dialog_userinformation_personality)
        val smoke: TextView = mView.findViewById(R.id.dialog_userinformation_smoke)
        val like : ToggleButton = mView.findViewById(R.id.dialog_userinformation_like)
        val dislike : ToggleButton = mView.findViewById(R.id.dialog_userinformation_dislike)
        val name : TextView = mView.findViewById(R.id.dialog_userinformation_name)
        val introMe : TextView = mView.findViewById(R.id.dialog_userinformation_introMe)

        age.text=PROFILE[userId]?.age
        name.text = PROFILE[userId]?.nickname
        introMe.text = PROFILE[userId]?.introMe
        //birth.text="생일 : "+ PROFILE[userId]?.birth
        drinking.text= PROFILE[userId]?.drinking
        hobby.text=PROFILE[userId]?.hobby
        job.text= PROFILE[userId]?.job
        mbti.text=PROFILE[userId]?.mbti
        personality.text=PROFILE[userId]?.personality
        smoke.text= PROFILE[userId]?.smoke
        var scaleAnimation: ScaleAnimation = ScaleAnimation(
            0.7f,
            1.0f,
            0.7f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.7f,
            Animation.RELATIVE_TO_SELF,
            0.7f
        )
        var bounceInterpolator: BounceInterpolator = BounceInterpolator()

        scaleAnimation.duration = 500
        scaleAnimation.interpolator = bounceInterpolator

        if (ANIMATION[likeArticleModel.Id]!!.permission) {
            Glide.with(this)
                .load(likeArticleModel.imageUrl)
                .into(image)
        } else {
            image.setImageResource(R.drawable.profileimage)
        }

        //  완료 버튼 클릭 시
        like.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            compoundButton.startAnimation(
                scaleAnimation
            )
            like.setBackgroundResource(R.drawable.ic_likefull)
            Toast.makeText(activity, "매칭 되었습니다!",Toast.LENGTH_SHORT).show()
            //상대방꺼에 나를 저장
            val otherMatchDB = Firebase.database.reference.child("likeInfo").child(userId).child("match")
            val otherMatchMap = mutableMapOf<String, Any>()

            otherMatchMap[CURRENT_USERID] = true
            otherMatchDB.updateChildren(otherMatchMap)

            //나에 상대방꺼 저장
            val myMatchDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("match")
            val myMatchMap = mutableMapOf<String, Any>()

            myMatchMap[userId] = true
            myMatchDB.updateChildren(myMatchMap)

            // 상대방에게 받은 receivedLike 를 지워야 하고, 상대방이 보낸 like 또한 지워야 함
            val receivedLikeDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID)
                .child("receivedLike").child(userId)
            val sendLikeDB = Firebase.database.reference.child("likeInfo").child(userId)
                .child("sendLike").child(CURRENT_USERID)

            receivedLikeDB.removeValue()
            sendLikeDB.removeValue()
            RECEIVED_LIKE_USER.remove(userId)

            val userHistoryDB = Firebase.database.reference.child("history").child(CURRENT_USERID)
            val otherHistoryDB = Firebase.database.reference.child("history").child(userId)
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter).toString()


            val matchUserHistoryItem = History(
                name = PROFILE[userId]?.nickname!!,
                time = formatted!!,
                type = History.MATCH_TYPE
            )

            val matchOtherUserHistoryItem = History(
                name = PROFILE[CURRENT_USERID]?.nickname!!,
                time = formatted!!,
                type = History.MATCH_TYPE
            )

            userHistoryDB.push().setValue(matchUserHistoryItem)
            otherHistoryDB.push().setValue(matchOtherUserHistoryItem)

            likeArticleList.clear()
            addReceivedLikeArticleList()
            likeArticleAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                //Do something
                dialog.dismiss()
            }, 400)
            //val myDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("match").child(userId)
            //dialog.dismiss()
            //dialog.cancel()
        })

        dislike.setOnCheckedChangeListener { compoundButton, isChecked ->
            compoundButton.startAnimation(
                scaleAnimation
            )
            dislike.setBackgroundResource(R.drawable.ic_dislikefull)
            val receivedLikeDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("receivedLike")
            val receiveLikeMap = mutableMapOf<String, Any>()

            receiveLikeMap[userId] = false
            receivedLikeDB.updateChildren(receiveLikeMap)

            RECEIVED_LIKE_USER[userId] = false
            likeArticleList.clear()
            addReceivedLikeArticleList()
            likeArticleAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                //Do something
                dialog.dismiss()
            }, 400)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }
}