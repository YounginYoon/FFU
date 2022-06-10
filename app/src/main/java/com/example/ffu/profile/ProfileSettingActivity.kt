package com.example.ffu.profile

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ffu.BackgroundActivity
import com.example.ffu.R
import com.example.ffu.UserInformation
import com.example.ffu.UserInformation.Companion.ANIMATION
import com.example.ffu.UserInformation.Companion.PROFILE
import com.example.ffu.UserInformation.Companion.URI
import com.example.ffu.utils.Animation
import com.example.ffu.utils.DBKey.Companion.DB_ANIMATION
import com.example.ffu.utils.DBKey.Companion.DB_PROFILE
import com.example.ffu.utils.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.metagalactic.dotprogressbar.DotProgressBar
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.net.Socket
import com.example.ffu.UserInformation.Companion.CURRENT_USERID
import java.text.SimpleDateFormat


class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var userDB: DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private val ip = "59.9.212.155"
    private val port = 30000
    private lateinit var socket: Socket
    private lateinit var dos: DataOutputStream

    private var personalities = ArrayList<String>()
    private var hobbies = ArrayList<String>()
    private var mbti: String = ""
    private var religion: String = ""
    private var birth: String = ""
    private var gender: String = ""

    private lateinit var nickname : String
    private lateinit var job : String
    private lateinit var introMe : String
    private lateinit var smoke : String
    private lateinit var drinking : String
    private lateinit var progressBar : DotProgressBar

    private lateinit var editTextArray : Array<EditText>
    private lateinit var radioButtonArray : Array<RadioButton>
    private lateinit var buttonArray : Array<Button>
    val dkblue = "#303F9F"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setting)

        initializeInformation()
        setEditableArray()
        setMbti()
        setPersonality()
        setHobby()
        setReligion()
        setPhoto()
        saveProfile()
    }

    // 취미를 '/' 으로 split 하여 리스트 형태로 반환
    private fun setHobbies(hobbies : String) : ArrayList<String> {
        val resultList = ArrayList<String>()
        val hobbyList = hobbies.split("/")

        for (hobby in hobbyList) {
            resultList.add(hobby)
        }
        return resultList
    }

    // 성격을 '/' 으로 split 하여 리스트 형태로 반환
    private fun setPersonalities(personalities : String) : ArrayList<String> {
        val resultList = ArrayList<String>()
        val personalityList = personalities.split("/")

        for (personality in personalityList) {
            resultList.add(personality)
        }
        return resultList
    }

    // 이전에 기입했던 정보들이 editText 에 남도록 설정
    private fun initializeInformation() {
        val image = findViewById<ImageButton>(R.id.profile_setting_imageAddButton)

        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        progressBar = findViewById<DotProgressBar>(R.id.profile_setting_progressbar)

        if (intent.hasExtra("birth")) {
            birth = intent.getStringExtra("birth").toString()
            gender = intent.getStringExtra("gender").toString()
        }

        findViewById<EditText>(R.id.profile_setting_inputNickname).setText(PROFILE[CURRENT_USERID]?.nickname)
        findViewById<EditText>(R.id.profile_setting_inputJob).setText(PROFILE[CURRENT_USERID]?.job)
        findViewById<EditText>(R.id.profile_setting_inputIntroduce).setText(PROFILE[CURRENT_USERID]?.introMe)

        when(PROFILE[CURRENT_USERID]?.smoke) {
            "비흡연" -> findViewById<RadioButton>(R.id.profile_setting_smoke_radioButton1).isChecked = true
            "흡연" -> findViewById<RadioButton>(R.id.profile_setting_smoke_radioButton2).isChecked = true
        }
        when(PROFILE[CURRENT_USERID]?.drinking) {
            "비음주" -> findViewById<RadioButton>(R.id.profile_setting_drink_radioButton1).isChecked = true
            "음주" -> findViewById<RadioButton>(R.id.profile_setting_drink_radioButton2).isChecked = true
        }

        mbti = PROFILE[CURRENT_USERID]?.mbti ?: ""
        religion = PROFILE[CURRENT_USERID]?.religion ?: ""
        personalities = setPersonalities(PROFILE[CURRENT_USERID]?.personality ?: "")
        hobbies = setHobbies(PROFILE[CURRENT_USERID]?.hobby ?: "")

        if (URI[CURRENT_USERID] != null) {
            Glide.with(this@ProfileSettingActivity)
                .load(URI[CURRENT_USERID])
                .circleCrop()
                .into(image)
        }
    }

    private fun setEditableArray() {
        editTextArray = arrayOf(findViewById<EditText>(R.id.profile_setting_inputNickname),
            findViewById<EditText>(R.id.profile_setting_inputJob),
            findViewById<EditText>(R.id.profile_setting_inputIntroduce),
            findViewById<EditText>(R.id.profile_setting_inputNickname))

        radioButtonArray = arrayOf(findViewById<RadioButton>(R.id.profile_setting_smoke_radioButton1),
            findViewById<RadioButton>(R.id.profile_setting_smoke_radioButton2),
            findViewById<RadioButton>(R.id.profile_setting_drink_radioButton1),
            findViewById<RadioButton>(R.id.profile_setting_drink_radioButton2))


        buttonArray = arrayOf(findViewById<Button>(R.id.profile_setting_mbtiButton),
            findViewById<Button>(R.id.profile_setting_personalButton),
            findViewById<Button>(R.id.profile_setting_religionButton),
            findViewById<Button>(R.id.profile_setting_hobbyButton),
            findViewById<Button>(R.id.profile_setting_saveButton))
    }

    private fun setMbti() {
        val mbtiButton = findViewById<Button>(R.id.profile_setting_mbtiButton)

        mbtiButton.setText(mbti.toString())
        mbtiButton.setTextColor(Color.parseColor(dkblue))

        mbtiButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_profile_mbti, null)
            val choice: Button = mView.findViewById(R.id.dialog_profile_mbti_choice)
            val items = arrayOf(
                "ESTJ", "ESFJ", "ENFJ", "ENTJ",
                "ENTP", "ENFP", "ESFP", "ESTP",
                "INTP", "INFP", "ISFP", "ISTP",
                "ISTJ", "ISFJ", "INFJ", "INTJ"
            )
            val mbtiRadioButtons = arrayOf(
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ESTJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ESFJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ENFJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ENTJ)
                ),
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ENTP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ENFP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ESFP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ESTP)
                ),
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_INTP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_INFP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ISFP),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ISTP)
                ),
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ISTJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_ISFJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_INFJ),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_INTJ)
                )
            )
            var checkMBTI = 0
            var prevX = -1
            var prevY = -1

            for (item in items) {
                if (mbti == item) {
                    mbtiRadioButtons[checkMBTI / 4][checkMBTI % 4].isChecked = true
                    prevX = checkMBTI / 4
                    prevY = checkMBTI % 4
                    break
                }
                checkMBTI++
            }
            for (curX in 0..3) {
                for (curY in 0..3) {
                    mbtiRadioButtons[curX][curY].setOnClickListener {
                        if (prevX != -1 && prevY != -1)
                            mbtiRadioButtons[prevX][prevY].isChecked = false
                        prevX = curX
                        prevY = curY
                    }
                }
            }

            choice.setOnClickListener {
                if (prevX != -1 && prevY != -1) {
                    mbti = items[4 * prevX + prevY]
                    mbtiButton.setText(mbti)
                    mbtiButton.setTextColor(Color.parseColor(dkblue))
                }

                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.create()
            dialog.show()
        }
    }

    // 3개 이상 출력 시 줄바꿈 처리
    private fun changeLine(List : List<String>) : String{
        val splitString = List.joinToString(",", "", "", 6).split(",")
        var result = ""
        var i = 0

        for (tmp in splitString) {
            if (i == 3) {
                if (tmp.equals(splitString.last())) {
                    result += "\n" + tmp
                } else {
                    result += "\n" + tmp + ","
                }
            } else {
                if (tmp.equals(splitString.last())) {
                    result += tmp
                } else {
                    result += tmp + ","
                }
            }
            i++
        }
        return result
    }

    private fun setPersonality() {
        val personalityButton = findViewById<Button>(R.id.profile_setting_personalButton)

        personalityButton.setText(changeLine(personalities))
        personalityButton.setTextColor(Color.parseColor(dkblue))
        personalityButton.setOnClickListener {
            val items = arrayOf("적극적인", "조용한", "엉뚱한", "진지한",
                "자유로운", "즉흥적인", "꼼꼼한", "감성적인",
                "성실한", "논리적인", "침착한", "자신감있는",
                "애교있는", "어른스러운", "예의 바른", "유머러스한",
                "허세 없는", "지적인", "소심한", "쿨한",
                "또라이같은", "친절한", "계획적인", "당당한")
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_profile_personality, null)
            val choice: Button = mView.findViewById(R.id.dialog_profile_personality_choice)
            val personalityCheckBoxes = arrayOf(
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityActive),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityQuiet),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityZany),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalitySerious)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityFreely),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityImprovised),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityDetailed),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalitySensitive)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityDiligent),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityLogical),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityCalm),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityConfident)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityCharming),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityMature),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityPolite),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityHumorous)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityHumble),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityIntel),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityTimid),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityCool)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityDdorai),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityKind),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalityPlanned),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_personalitySelfConfident)
                )
            )

            for (personality in personalities) {
                var i = 0
                for (item in items) {
                    if (personality == item) {
                        personalityCheckBoxes[i / 4][i % 4].isChecked = true
                        break
                    }
                    i++
                }
            }

            choice.setOnClickListener {
                personalities.clear()
                for (i in 0..5) {
                    for (j in 0..3) {
                        if (personalityCheckBoxes[i][j].isChecked) {
                            personalities.add(items[4 * i + j])
                        }
                    }
                }
                personalityButton.setText(changeLine(personalities))
                personalityButton.setTextColor(Color.parseColor(dkblue))
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.create()
            dialog.show()
        }

    }

    private fun setReligion() {
        val religionButton = findViewById<Button>(R.id.profile_setting_religionButton)

        religionButton.setText(religion.toString())
        religionButton.setTextColor(Color.parseColor(dkblue))

        religionButton.setOnClickListener {
            val items = arrayOf("무교", "기독교", "불교", "천주교",
                "이슬람교", "힌두교", "개신교", "기타")
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_profile_religion, null)
            val choice: Button = mView.findViewById(R.id.dialog_profile_religion_choice)
            val religionRadioButtons = arrayOf(
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_non_religion),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_christianity),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_buddhism),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_catholic)
                ),
                arrayOf(
                    mView.findViewById<RadioButton>(R.id.dialog_profile_islam),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_hindu),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_protestantism),
                    mView.findViewById<RadioButton>(R.id.dialog_profile_etc)
                )
            )
            var checkReligion = 0
            var prevX = -1
            var prevY = -1

            for (item in items) {
                if (religion == item) {
                    religionRadioButtons[checkReligion / 4][checkReligion % 4].isChecked = true
                    prevX = checkReligion / 4
                    prevY = checkReligion % 4
                    break
                }
                checkReligion++
            }
            for (curX in 0..1) {
                for (curY in 0..3) {
                    religionRadioButtons[curX][curY].setOnClickListener {
                        if (prevX != -1 && prevY != -1)
                            religionRadioButtons[prevX][prevY].isChecked = false
                        prevX = curX
                        prevY = curY
                    }
                }
            }

            choice.setOnClickListener {
                if (prevX != -1 && prevY != -1) {
                    religion =  items[4 * prevX + prevY]
                    religionButton.setText(religion)
                    religionButton.setTextColor(Color.parseColor(dkblue))
                }
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.create()
            dialog.show()
        }
    }

    private fun setHobby() {
        val hobbyButton = findViewById<Button>(R.id.profile_setting_hobbyButton)

        hobbyButton.setText(changeLine(hobbies))
        hobbyButton.setTextColor(Color.parseColor(dkblue))

        hobbyButton.setOnClickListener {
            val items = arrayOf(
                "영화", "독서", "맛집 탐방", "운동",
                "캠핑", "코딩", "카페", "등산",
                "맥주", "여행","쇼핑","산책",
                "수다", "야구 보기", "러닝", "클라이밍",
                "악기 연주", "드라이브", "재테크", "사진 찍기",
                "요리", "게임", "코인노래방", "라이딩")
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_profile_hobby, null)
            val choice: Button = mView.findViewById(R.id.dialog_profile_hobby_choice)
            val hobbyCheckBoxes = arrayOf(
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyMovie),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyReading),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyEating),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyWorkOut)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyCamping),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyCoding),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyCafe),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyHiking)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyBeer),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyTrip),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyShopping),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyWalking)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyTalking),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyBaseball),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyRunning),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyClimbing)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyInstrument),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyDriving),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyInvest),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyPhoto)
                ),
                arrayOf(
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyCook),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyGame),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbySing),
                    mView.findViewById<CheckBox>(R.id.dialog_profile_hobbyRiding)
                )
            )

            for (hobby in hobbies) {
                var i = 0
                for (item in items) {
                    if (hobby == item) {
                        hobbyCheckBoxes[i / 4][i % 4].isChecked = true
                        break
                    }
                    i++
                }
            }

            choice.setOnClickListener {
                hobbies.clear()
                for (i in 0..5) {
                    for (j in 0..3) {
                        if (hobbyCheckBoxes[i][j].isChecked) {
                            hobbies.add(items[4 * i + j])
                        }
                    }
                }
                hobbyButton.setText(changeLine(hobbies))
                hobbyButton.setTextColor(Color.parseColor(dkblue))
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.create()
            dialog.show()
        }
    }

    // 프로필 변환이 끝날 경우
    private fun setAllEnable() {
        for (editText in editTextArray) {
            editText.isEnabled = true
        }
        for (radioButton in radioButtonArray) {
            radioButton.isEnabled = true
        }
        for (button in buttonArray) {
            button.isEnabled = true
        }
    }

    // 프로필 변환이 시작될 때
    private fun setAllDisable() {
        for (editText in editTextArray) {
            editText.isEnabled = false
        }
        for (radioButton in radioButtonArray) {
            radioButton.isEnabled = false
        }
        for (button in buttonArray) {
            button.isEnabled = false
        }
    }

    // animation 사진으로 변환해주는 python server 와 socket communication 을 한다.
    // animation 의 총 3가지 flag 를 통해 response 한다.
    // (1) request : server 에게 요청하기 전 false, server 측에게 응답받으면 true
    // (2) person : server 에서 인물 사진임을 판단하면 true, 그 외엔 false
    // (3) permission : 프로필을 변환하고 완벽하게 저장할 경우 true, 그 외엔 false
    //     프로필을 변환을 한 후에 비정상적으로 program 을 종료하거나 뒤로 이동할 경우
    //     permission 값이 false 가 되어 의도치 않은 사진(실물 사진)이 나타나는 것을 방지
    private fun characterization() {
        val transformText = findViewById<TextView>(R.id.profile_setting_transform_text)
        val image = findViewById<ImageButton>(R.id.profile_setting_imageAddButton)

        transformText.text = "사진 변환중..."
        Thread {
            val animation = mutableMapOf<String, Any>()

            userDB = Firebase.database.reference.child(DB_ANIMATION).child(CURRENT_USERID)
            animation["request"] = false
            animation["person"] = false
            animation["permission"] = false
            userDB.updateChildren(animation)

            // 1. server 에게 connection request 및 buffer write
            try {
                socket = Socket(ip, port)
                dos = DataOutputStream(socket.getOutputStream())
                dos.writeUTF(CURRENT_USERID)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // 2. server 에서 요청(request = true)이 올 때 까지 대기
            while (!ANIMATION[CURRENT_USERID]!!.request) {
                Thread.sleep(100)
            }

            // 3. server 에서 요청이 온 경우, person flag 에 따라 변환이 되었는지 판단
            Handler(Looper.getMainLooper()).post {
                setAllEnable()
                transformText.text = ""
                progressBar.visibility = View.INVISIBLE
                Glide.with(this@ProfileSettingActivity)
                    .load(URI[CURRENT_USERID])
                    .circleCrop()
                    .into(image)
                if (ANIMATION[CURRENT_USERID]!!.person) {
                    Toast.makeText(this@ProfileSettingActivity,"애니메이션 변환 완료!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProfileSettingActivity,"인물사진을 넣어주세요!",Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPhoto() {
        val photoButton = findViewById<ImageButton>(R.id.profile_setting_imageAddButton)
        val imagesRef = storage.reference
            .child("photo/" + CURRENT_USERID + "/real.jpg")

        val getFromAlbumResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            setAllDisable()
            progressBar.visibility = View.VISIBLE
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data // 선택한 이미지의 주소
                // 이미지 파일 읽어와서 설정하기
                if (uri != null) {
                    // 사진 가져오기
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                    // 사진의 회전 정보 가져오기
                    val orientation = getOrientationOfImage(uri).toFloat()
                    // 이미지 회전하기
                    val newBitmap = getRotatedBitmap(bitmap, orientation)
                    imagesRef.putFile(uri).addOnSuccessListener {
                        characterization()
                    }
                }
            } else {
                setAllEnable()
                progressBar.visibility = View.INVISIBLE
            }
        }

        photoButton.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            getFromAlbumResultLauncher.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getOrientationOfImage(uri: Uri): Int {
        val inputStream = contentResolver.openInputStream(uri)
        val exif: ExifInterface? = try {
            ExifInterface(inputStream!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
        inputStream.close()

        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if (orientation != -1) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return 0
    }

    @Throws(Exception::class)
    private fun getRotatedBitmap(bitmap: Bitmap?, degrees: Float): Bitmap? {
        if (bitmap == null) return null
        if (degrees == 0F) return bitmap

        val m = Matrix()
        m.setRotate(degrees, bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
    }

    // 모든 정보를 입력했는지를 확인
    private fun checkInformation() : Boolean {
        val smokeGroup = findViewById<RadioGroup>(R.id.profile_setting_smoke_radioGroup)
        val drinkingGroup = findViewById<RadioGroup>(R.id.profile_setting_drink_radioGroup)

        nickname = findViewById<EditText>(R.id.profile_setting_inputNickname).text.toString()
        job = findViewById<EditText>(R.id.profile_setting_inputJob).text.toString()
        introMe = findViewById<EditText>(R.id.profile_setting_inputIntroduce).text.toString()


        smoke = when (smokeGroup.checkedRadioButtonId) {
            R.id.profile_setting_smoke_radioButton1 -> "비흡연"
            R.id.profile_setting_smoke_radioButton2 -> "흡연"
            else -> ""
        }
        drinking = when (drinkingGroup.checkedRadioButtonId) {
            R.id.profile_setting_drink_radioButton1 -> "비음주"
            R.id.profile_setting_drink_radioButton2 -> "음주"
            else -> ""
        }

        if (nickname.isEmpty() || job.isEmpty() || introMe.isEmpty() || smoke.isEmpty()
            || drinking.isEmpty() || mbti.isEmpty() || religion.isEmpty()
            || personalities.size == 0 || hobbies.size == 0 || !ANIMATION[CURRENT_USERID]!!.person)
            return false
        return true
    }

    // firebase realtime database 에 입력한 모든 정보들을 insertion
    private fun insertProfileInformation() {
        val profile : Profile
        val animation : Animation
        var personality : String =  ""
        var hobby : String = ""

        for (data in personalities) {
            personality += data
            if (!data.equals(personalities.last())) {
                personality += "/"
            }
        }
        for (data in hobbies) {
            hobby += data
            if (!data.equals(hobbies.last())) {
                hobby += "/"
            }
        }

        if (birth.length > 0) {
            val age = SimpleDateFormat("yyyy-MM-dd hh:mm")
                .format(System.currentTimeMillis())
                .split("-")[0].toInt() - birth.split("/")[0].toInt() + 1
            profile = Profile(age.toString(), birth, drinking, gender,
                hobby, introMe, job, mbti, nickname, personality,
                religion, smoke, PROFILE[CURRENT_USERID]?.tel, true)
        } else {
            profile = Profile(PROFILE[CURRENT_USERID]?.age, PROFILE[CURRENT_USERID]?.birth, drinking,
                PROFILE[CURRENT_USERID]?.gender, hobby, introMe, job, mbti, nickname, personality,
                religion, smoke, PROFILE[CURRENT_USERID]?.tel, true)
        }
        animation = Animation(permission = true, person = true, request = true)

        userDB = Firebase.database.reference.child(DB_ANIMATION).child(CURRENT_USERID)
        userDB.setValue(animation)

        userDB = Firebase.database.reference.child(DB_PROFILE).child(CURRENT_USERID)
        userDB.setValue(profile)
    }

    private fun completeProfileSetting(dialog : AlertDialog) {
        insertProfileInformation()
        Thread {
            Thread.sleep(2000)
            Handler(Looper.getMainLooper()).post {
                dialog.dismiss()
                dialog.cancel()
                finish()
            }
        }.start()
    }

    private fun showAnimationPhoto() {
        val dialog = AlertDialog.Builder(this).create()
        val edialog : LayoutInflater = LayoutInflater.from(this)
        val mView : View = edialog.inflate(R.layout.dialog_check_animation,null)
        val image : CircleImageView = mView.findViewById(R.id.dialog_check_animation_photo)
        val cancel : Button = mView.findViewById(R.id.dialog_check_animation_cancel)
        val save : Button = mView.findViewById(R.id.dialog_check_animation_save)
        val dialog_progressBar : DotProgressBar = mView.findViewById(R.id.dialog_check_animation_progressbar)
        val nicknameTextView = mView.findViewById<TextView>(R.id.nickname_TextView)

        Glide.with(this)
            .load(UserInformation.URI[CURRENT_USERID])
            .circleCrop()
            .into(image)

        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        save.setOnClickListener {
            dialog_progressBar.visibility = View.VISIBLE
            completeProfileSetting(dialog)
        }

        dialog.setView(mView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        nicknameTextView.setText(nickname + " 님")
        dialog.create()
        dialog.show()
    }

    private fun saveProfile() {
        val saveButton = findViewById<Button>(R.id.profile_setting_saveButton)

        saveButton.setOnClickListener {
            if (checkInformation()) {
                showAnimationPhoto()
            }
            else {
                if (!ANIMATION[CURRENT_USERID]!!.person) {
                    Toast.makeText(this, "인물 사진을 넣어주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "정보를 완벽하게 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
