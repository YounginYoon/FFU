package com.example.ffu.recommend

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ffu.R
import com.example.ffu.UserInformation
import com.example.ffu.UserInformation.Companion.CURRENT_USERID
import com.example.ffu.UserInformation.Companion.PROFILE
import com.example.ffu.UserInformation.Companion.RECOMMEND
import com.example.ffu.utils.RecommendArticle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import java.lang.Math.*
import kotlin.math.pow


class RecommendFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    private val radius = 6372.8 * 1000
    private var recommendLatitude: Double = 0.0
    private var recommendLongitude: Double = 0.0

    private lateinit var recommendButton: Button
    private lateinit var locationMsg: TextView

    private lateinit var MBTIButton: Button
    private lateinit var hobbyButton: Button
    private lateinit var personalityButton: Button
    private lateinit var smokingButton: Button

    private var auth: FirebaseAuth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeButtons(view)
        bottomSheetListener()
        getMatchedUsers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_recommend, container, false)

        mapView = rootView.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return rootView
    }

    private fun getMatchedUsers() {

        recommendButton.setOnClickListener {
            //RecommendData.MBTISet.forEach { v -> Log.d("MBTISet", "${v}") }
            //RecommendData.hobbySet.forEach { v -> Log.d("hobbySet", "${v}") }
            //RecommendData.personalitySet.forEach { v -> Log.d("personalitySet", "${v}") }

            val usersUid: ArrayList<String> = UserInformation.MAP_USER
            val myRadius = RecommendData.myRadius / 1000.0
            val recommendUsersUid = ArrayList<String>()
            val userMatched = mutableMapOf<String, Int>()
            var realMatched = mutableMapOf<String, Int>()

            for (uid in usersUid) {
                if (uid == auth.uid || uid == "null") continue

                val distance: Double =
                    getDistance(
                        RECOMMEND[uid]?.latitude, RECOMMEND[uid]?.longitude,
                        recommendLatitude, recommendLongitude
                    ) / 1000.0

                if (distance < myRadius) {
                    recommendUsersUid.add(uid)
                    RecommendData.distanceUsers[uid] = distance * 1000.0
                    userMatched[uid] = 0
                }
            }

//            if (RecommendData.MBTISet.isEmpty() || RecommendData.hobbySet.isEmpty() || RecommendData.personalitySet.isEmpty()) {
//                Toast.makeText(requireContext(), "추천할 대상이 없습니다.", Toast.LENGTH_SHORT).show()
//
//            } else {
            for (mbti in RecommendData.MBTISet) {
                for (uid in recommendUsersUid) {
                    if (PROFILE[uid]?.mbti?.contains(mbti) == true) {
                        userMatched[uid] = 1
                    }
                }
            }

            for (hobby in RecommendData.hobbySet) {
                for (uid in recommendUsersUid) {
                    if (PROFILE[uid]?.hobby?.contains(hobby) == true) {
                        userMatched[uid] = userMatched[uid]!! + 1
                    }
                }
            }

            for (personality in RecommendData.personalitySet) {
                for (uid in recommendUsersUid) {
                    if (PROFILE[uid]?.personality?.contains(personality) == true) {
                        userMatched[uid] = userMatched[uid]!! + 1
                    }
                }
            }

            if (!RecommendData.smokingCheck) {
                for (uid in recommendUsersUid) {
                    if (PROFILE[uid]?.smoke?.equals("흡연") == true) {
                        userMatched[uid] = 0
                    }
                }
            }

            userMatched.forEach { (k, v) -> Log.d("finalMatched", "${k}, ${v}") }

            for(userId in userMatched.keys){
                //이미 LIKE 또는 DISLIKE를 보내거나 받은 유저이면 recommend에 뜨지 않게 한다.
                if(UserInformation.CURRENT_USERID !=userId && !UserInformation.SEND_LIKE_USER.containsKey(userId)
                    && !UserInformation.RECEIVED_LIKE_USER.containsKey(userId) && userMatched[userId] != 0){
                    realMatched[userId] = userMatched[userId]!!
                }
            }

            realMatched = realMatched.toList().sortedByDescending { it.second }.toMap().toMutableMap()
            realMatched.forEach { (k, v) -> Log.d("realfinal", "${k}: ${v}") }

            if (realMatched.isEmpty()) {
                Toast.makeText(requireContext(), "추천할 대상이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val bottomSheet = RecommendList(realMatched)
                bottomSheet.show(childFragmentManager, RecommendList.TAG)
            }
//            }
        }
    }

    private fun initializeButtons(view: View) {
        recommendButton = view.findViewById(R.id.recommendButton)
        locationMsg = view.findViewById(R.id.locationMsg)
        RecommendData.distanceButton = view.findViewById(R.id.selectKm)
        MBTIButton = view.findViewById(R.id.selectMBTI)
        hobbyButton = view.findViewById(R.id.selectHobby)
        personalityButton = view.findViewById(R.id.selectPersonality)
        smokingButton = view.findViewById(R.id.selectSmoking)
    }

    private fun bottomSheetListener() {
        val distanceFrag = BottomSheetDistance()
        val MBTIFrag = BottomSheetMBTI()
        val hobbyFrag = BottomSheetHobby()
        val personalityFrag = BottomSheetPersonality()
        val SmokingFrag = BottomSheetSmoking()

        RecommendData.distanceButton.setOnClickListener {
            distanceFrag.show(childFragmentManager, distanceFrag.tag)
        }

        MBTIButton.setOnClickListener {
            MBTIFrag.show(childFragmentManager, MBTIFrag.tag)
        }

        hobbyButton.setOnClickListener {
            hobbyFrag.show(childFragmentManager, hobbyFrag.tag)
        }

        personalityButton.setOnClickListener {
            personalityFrag.show(childFragmentManager, personalityFrag.tag)
        }

        smokingButton.setOnClickListener {
            SmokingFrag.show(childFragmentManager, SmokingFrag.tag)
        }
    }


    private fun getDistance(lat1: Double?, lon1: Double?, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1!!)
        val dLon = Math.toRadians(lon2 - lon1!!)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(
            Math.toRadians(lat2)
        )
        val c = 2 * asin(sqrt(a))
        return (radius * c)
    }

    override fun onMapReady(nMap: NaverMap) {
        RecommendData.naverMap = nMap
        val circle = CircleOverlay()

        // 최대, 최소 줌
        RecommendData.naverMap.minZoom = 7.0
        RecommendData.naverMap.maxZoom = 14.0

        RecommendData.naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(RECOMMEND[CURRENT_USERID]!!.latitude, RECOMMEND[CURRENT_USERID]!!.longitude)))
//        RecommendData.naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(37.5509, 126.9410)))
        RecommendData.naverMap.uiSettings.isLocationButtonEnabled = true
        RecommendData.naverMap.locationSource =
            FusedLocationSource(this@RecommendFragment, REQUEST_ACCESS_LOCATION_PERMISSIONS)

        val marker = Marker()
        marker.position = LatLng(37.497885, 127.027512)
        marker.map = RecommendData.naverMap
        marker.icon = MarkerIcons.LIGHTBLUE
        marker.iconTintColor = Color.rgb(159, 214, 253)
        marker.isHideCollidedSymbols = true
        // 카메라 움직임에 대한 이벤트
        RecommendData.naverMap.addOnCameraChangeListener { reason, animated ->
            marker.position = LatLng(
                RecommendData.naverMap.cameraPosition.target.latitude,
                RecommendData.naverMap.cameraPosition.target.longitude
            )
            circle.map = null
            recommendButton.visibility = View.INVISIBLE
            locationMsg.visibility = View.INVISIBLE
        }
        // 카메라 움직임 종료에 대한 이벤트
        RecommendData.naverMap.addOnCameraIdleListener {
            Log.i("RecommendData.naverMap", "카메라 종료")
            recommendButton.visibility = View.VISIBLE
            locationMsg.visibility = View.VISIBLE
            marker.position = LatLng(
                RecommendData.naverMap.cameraPosition.target.latitude,
                RecommendData.naverMap.cameraPosition.target.longitude
            )
            circle.center = LatLng(
                RecommendData.naverMap.cameraPosition.target.latitude,
                RecommendData.naverMap.cameraPosition.target.longitude
            )
            circle.radius = RecommendData.myRadius * 1.0
            circle.map = RecommendData.naverMap
            circle.outlineWidth = 8
            circle.color = Color.argb(40, 159, 214, 253)
            circle.outlineColor = Color.rgb(159, 214, 253)

            recommendLatitude = RecommendData.naverMap.cameraPosition.target.latitude
            recommendLongitude = RecommendData.naverMap.cameraPosition.target.longitude
            RecommendData.curLatitude = recommendLatitude
            RecommendData.curLongitude = recommendLongitude
            Log.d("recommendLocation", "${recommendLatitude}, ${recommendLongitude}")
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
    }
}