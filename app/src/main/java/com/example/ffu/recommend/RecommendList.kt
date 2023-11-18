package com.example.ffu.recommend

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ffu.R
import com.example.ffu.UserInformation.Companion.CURRENT_USERID
import com.example.ffu.UserInformation.Companion.PROFILE
import com.example.ffu.UserInformation.Companion.RECEIVED_LIKE_USER
import com.example.ffu.UserInformation.Companion.SEND_LIKE_USER
import com.example.ffu.UserInformation.Companion.URI
import com.example.ffu.databinding.FragmentBottomsheetBinding
import com.example.ffu.utils.History
import com.example.ffu.utils.History.Companion.RECEIVE_TYPE
import com.example.ffu.utils.History.Companion.SEND_TYPE
import com.example.ffu.utils.RecommendArticle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import com.example.ffu.UserInformation.Companion.ANIMATION
import com.example.ffu.UserInformation.Companion.MATCH_USER
import org.w3c.dom.Text

class RecommendList(recommendUsersUid: MutableMap<String, Int>) : BottomSheetDialogFragment() {

    private val recommendUsers = recommendUsersUid // 거리에 매치되는 user 리스트
    // firebase
    private lateinit var userDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val recommendUserList = mutableListOf<RecommendArticle>()

    // user View
    private var binding: FragmentBottomsheetBinding? = null
    private lateinit var recommendAdapter: RecommendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_bottomsheet, container, false)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendUserList.clear()
        //userDB = Firebase.database.reference.child(DB_PROFILE)

        // bottomsheet view
        val fragmentBottomsheetBinding = FragmentBottomsheetBinding.bind(view)
        binding = fragmentBottomsheetBinding

        recommendAdapter = RecommendAdapter(onItemClicked = { RecommendArticleModel->
            showUserInformation(RecommendArticleModel)
        })

        recommendListener()
        addRecommendUserList()
        downListener()

        fragmentBottomsheetBinding.recommendedUsersView.layoutManager = LinearLayoutManager(context)
        fragmentBottomsheetBinding.recommendedUsersView.adapter = recommendAdapter

        //userDB.addChildEventListener(listener)
    }

    private fun recommendListener() {
        userDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("sendLike")
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                addRecommendUserList()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun downListener(){
        binding?.fragmentBottomsheetDown?.setOnClickListener{
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener{ dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        // 위 수치는 기기 높이 대비 80%로 다이얼로그 높이를 설정
        return getWindowHeight() * 100 / 100
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onDestroy() {
        super.onDestroy()
        //userDB.removeEventListener(listener)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        recommendAdapter.notifyDataSetChanged()
    }
    companion object {
        const val TAG = "RecommendList"
    }

    private fun addRecommendUserList(){
        recommendUserList.clear()
        for(userId in recommendUsers.keys){
            //이미 LIKE 또는 DISLIKE를 보내거나 받은 유저이면 recommend에 뜨지 않게 한다.
            if(CURRENT_USERID!=userId && !SEND_LIKE_USER.containsKey(userId)&& !RECEIVED_LIKE_USER.containsKey(userId)
                && !MATCH_USER.containsKey(userId)){
                val nickname = PROFILE[userId]?.nickname ?: ""
                val ageJob = PROFILE[userId]?.age+ ", "+ PROFILE[userId]?.job
                val introMe = PROFILE[userId]?.introMe ?: ""
               // val gender = PROFILE[userId]?.gender ?: ""
                //val birth = PROFILE[userId]?.birth ?: ""
                val imageUri = URI[userId]?:""
                recommendUserList.add(RecommendArticle(userId,nickname,ageJob,introMe,imageUri))
            }
        }
        recommendAdapter.submitList(recommendUserList)
        recommendAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showUserInformation(recommendArticleModel: RecommendArticle) {
        val userId = recommendArticleModel.Id
        val dialog = AlertDialog.Builder(requireActivity()).create()
        val edialog : LayoutInflater = LayoutInflater.from(requireActivity())
        val mView : View = edialog.inflate(R.layout.dialog_userinformation,null)
        val image : CircleImageView = mView.findViewById(R.id.dialog_userinformation_photo)
        val name : TextView = mView.findViewById(R.id.dialog_userinformation_name)
        val introMe : TextView = mView.findViewById(R.id.dialog_userinformation_introMe)
        val age : TextView = mView.findViewById(R.id.dialog_userinformation_age)
        val drinking : TextView = mView.findViewById(R.id.dialog_userinformation_drinking)
        val hobby : TextView = mView.findViewById(R.id.dialog_userinformation_hobby)
        val job : TextView = mView.findViewById(R.id.dialog_userinformation_job)
        val mbti : TextView = mView.findViewById(R.id.dialog_userinformation_mbti)
        val personality : TextView = mView.findViewById(R.id.dialog_userinformation_personality)
        val smoke: TextView = mView.findViewById(R.id.dialog_userinformation_smoke)
        val like : ToggleButton = mView.findViewById(R.id.dialog_userinformation_like)
        val dislike : ToggleButton = mView.findViewById(R.id.dialog_userinformation_dislike)
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

        name.text = PROFILE[userId]?.nickname
        introMe.text = PROFILE[userId]?.introMe
        age.text = PROFILE[userId]?.age
        drinking.text = PROFILE[userId]?.drinking
        hobby.text = PROFILE[userId]?.hobby
        job.text = PROFILE[userId]?.job
        mbti.text = PROFILE[userId]?.mbti
        personality.text = PROFILE[userId]?.personality
        smoke.text =PROFILE[userId]?.smoke

        if (ANIMATION[recommendArticleModel.Id]!!.permission) {
            Glide.with(this)
                .load(recommendArticleModel.imageUrl)
                .into(image)
        } else {
            image.setImageResource(R.drawable.profileimage)
        }

        val receivedLikeDB = Firebase.database.reference.child("likeInfo").child(userId).child("receivedLike")
        val sendLikeDB = Firebase.database.reference.child("likeInfo").child(CURRENT_USERID).child("sendLike")

        like.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->

            compoundButton.startAnimation(
                scaleAnimation
            )
            like.setBackgroundResource(R.drawable.ic_likefull)

            val receiveLikeMap = mutableMapOf<String, Any>()
            val sendLikeMap = mutableMapOf<String, Any>()

            receiveLikeMap[CURRENT_USERID] = true
            sendLikeMap[userId] = true

            receivedLikeDB.updateChildren(receiveLikeMap)
            sendLikeDB.updateChildren(sendLikeMap)

            val userHistoryDB = Firebase.database.reference.child("history").child(CURRENT_USERID)
            val otherHistoryDB = Firebase.database.reference.child("history").child(userId)
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter).toString()

            val sendHistoryItem = History(
                name = PROFILE[userId]?.nickname!!,
                time = formatted!!,
                type = SEND_TYPE
            )

            val receiveHistoryItem = History(
                name = PROFILE[CURRENT_USERID]?.nickname!!,
                time = formatted!!,
                type = RECEIVE_TYPE
            )

            userHistoryDB.push().setValue(sendHistoryItem)
            otherHistoryDB.push().setValue(receiveHistoryItem)

            Handler(Looper.getMainLooper()).postDelayed({
                //Do something
                dialog.dismiss()
            }, 400)


        })

        dislike.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            val sendLikeMap = mutableMapOf<String, Any>()
            compoundButton.startAnimation(
                scaleAnimation
            )

            dislike.setBackgroundResource(R.drawable.ic_dislikefull)

            sendLikeMap[userId] = false
            sendLikeDB.updateChildren(sendLikeMap)

            Handler(Looper.getMainLooper()).postDelayed({
                //Do something
                dialog.dismiss()
            }, 400)

        })

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }


        /*
        like.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            compoundButton.startAnimation(
                scaleAnimation
            )

            val receiveLikeMap = mutableMapOf<String, Any>()
            val sendLikeMap = mutableMapOf<String, Any>()

            receiveLikeMap[CURRENT_USERID] = true
            sendLikeMap[userId] = true

            receivedLikeDB.updateChildren(receiveLikeMap)
            sendLikeDB.updateChildren(sendLikeMap)

            val userHistoryDB = Firebase.database.reference.child("history").child(CURRENT_USERID)
            val otherHistoryDB = Firebase.database.reference.child("history").child(userId)
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter).toString()

            val sendHistoryItem = History(
                name = PROFILE[userId]?.nickname!!,
                time = formatted!!,
                type = SEND_TYPE
            )

            val receiveHistoryItem = History(
                name = PROFILE[CURRENT_USERID]?.nickname!!,
                time = formatted!!,
                type = RECEIVE_TYPE
            )

            userHistoryDB.push().setValue(sendHistoryItem)
            otherHistoryDB.push().setValue(receiveHistoryItem)

            //dialog.dismiss()
            //dialog.cancel()

            //dialog.dismiss()
            //dialog.cancel()
        })

        dislike.setOnClickListener{
            val sendLikeMap = mutableMapOf<String, Any>()

            sendLikeMap[userId] = false
            sendLikeDB.updateChildren(sendLikeMap)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }*/
}